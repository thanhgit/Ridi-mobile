package saveteam.com.ridesharing.presentation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.firebase.ui.auth.util.ui.SupportVectorDrawablesButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.gofynd.gravityview.GravityView;
import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.database.RidesharingDB;
import saveteam.com.ridesharing.database.model.Profile;
import saveteam.com.ridesharing.database.model.User;
import saveteam.com.ridesharing.firebase.FirebaseDB;
import saveteam.com.ridesharing.presentation.home.MainActivity;
import saveteam.com.ridesharing.presentation.profile.RegisterProfileActivity;
import saveteam.com.ridesharing.utils.activity.ActivityUtils;
import saveteam.com.ridesharing.utils.google.MyGoogleAuthen;
import saveteam.com.ridesharing.utils.MyPermission;
import saveteam.com.ridesharing.utils.activity.SharedRefUtils;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.imgView_where_login)
    ImageView imgView;
    @BindView(R.id.btn_signin_where_login)
    SupportVectorDrawablesButton btnSignin;

    GravityView gravityView;

    MyGoogleAuthen authen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        MyPermission.getInstance(this).requestPermission();

        initGravityView();

        authen = new MyGoogleAuthen(this, new MyGoogleAuthen.CheckSignInListener() {
            @Override
            public void success(FirebaseUser user) {

                User requestUser = new User(user.getUid(),
                        user.getDisplayName(),
                        user.getEmail(),
                        user.getPhoneNumber() == null ? "" : user.getPhoneNumber(),
                        user.getPhotoUrl() == null ? "" : user.getPhotoUrl().toString(),
                        new Date(user.getMetadata().getCreationTimestamp()),
                        new Date(user.getMetadata().getLastSignInTimestamp()));

                SharedRefUtils.saveEmail(user.getEmail(), LoginActivity.this);
                SharedRefUtils.saveUid(user.getUid(), LoginActivity.this);

                InsertUserTask insertUserTask = new InsertUserTask(requestUser);
                insertUserTask.execute();
            }

            @Override
            public void fail() {
                openNetWork();
            }
        });

        authen.init();
    }

    public void openNetWork() {
        if (!ActivityUtils.checkInternetConnection(this)) {
            ActivityUtils.displayAlert("Network problem", "Do you want to open network to use apps?", this,
                    new ActivityUtils.OnOkClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        }
                    });
        }
    }

    private void initGravityView() {
        gravityView = GravityView.getInstance(this)
                .setImage(imgView, R.drawable.bg_where_login)
                .center();
        if (!gravityView.deviceSupported()) {
            ActivityUtils.displayToast(this, "Gyroscope sensor not available in your device");
        }
    }

    @OnClick(R.id.btn_signin_where_login)
    public void clickButtonSignin(View view) {
        authen.signIn();
    }


    @Override
    protected void onStart() {
        super.onStart();
        authen.checkSignIn();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gravityView.registerListener();
//        openNetWork();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gravityView.unRegisterListener();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        authen.getResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MyPermission.getInstance(this).onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private class InsertUserTask extends AsyncTask<Void, Void, Void> {

        private User user;

        public InsertUserTask(User user) {
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RidesharingDB.getInstance(getApplicationContext()).getUserDao().insertUsers(user);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            DatabaseReference dbref = FirebaseDB.getInstance().child("profiles");
            dbref.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Profile profile = dataSnapshot.getValue(Profile.class);
                    if (profile != null) {
                        ActivityUtils.changeActivity(LoginActivity.this, MainActivity.class);

                    } else {
                        ActivityUtils.changeActivity(LoginActivity.this, RegisterProfileActivity.class);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private class GetAllUserTask extends AsyncTask<Void, Void, List<User>> {
        private List<User> users;
        @Override
        protected List<User> doInBackground(Void... voids) {
            return Arrays.asList(RidesharingDB.getInstance(getApplicationContext()).getUserDao().loadAllUsers());
        }

        @Override
        protected void onPostExecute(List<User> users) {
            this.users = users;
            Log.d("thanhuit", "size is " + users.size());
        }

        public List<User> getUsers() {
            return this.users;
        }
    }
}
