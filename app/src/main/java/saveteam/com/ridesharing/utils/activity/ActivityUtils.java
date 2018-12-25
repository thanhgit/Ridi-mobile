package saveteam.com.ridesharing.utils.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.model.Geo;
import saveteam.com.ridesharing.utils.google.S2Utils;

public class ActivityUtils {
    public static final String TAG = "thanhuit";

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    public static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CAMERA
    };


    /**
     * Display log
     */
    public static void displayLog(String message) {
        Log.d(TAG, message);
    }

    public static void displayToast(Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }


    /**
     * Change activity
     */
    public static void changeActivity(AppCompatActivity app, Class<?> cls) {
        Intent changeActivity = new Intent(app.getApplicationContext(), cls);
        app.startActivity(changeActivity);
    }

    /**
     * Get image
     */
    public static Image getMarker(Context context, int res, int color) {
        Bitmap sourceBitmap = BitmapFactory.decodeResource(context.getResources() ,res);

        Bitmap source = changeBitmapColor(sourceBitmap, color);
        Image marker_img = new Image();
        marker_img.setBitmap(source);
        return marker_img;
    }

    public static Image getMarker(int res) {
        Image image = new Image();
        try {
            image.setImageResource(res);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    public static Image getMarker() {
        Image image = new Image();

        try {
            image.setImageResource(R.drawable.marker);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    public static Bitmap changeBitmapColor(Bitmap sourceBitmap, int color)
    {
        Bitmap resultBitmap = sourceBitmap.copy(sourceBitmap.getConfig(),true);
        Paint paint = new Paint();
        ColorFilter filter = new LightingColorFilter(color, 1);
        paint.setColorFilter(filter);
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, paint);
        return resultBitmap;
    }

    /**
     * Convert from Geo to GeoCoordinate
     */
    public static List<GeoCoordinate> convertFrom(List<Geo> geos) {
        List<GeoCoordinate> result = new ArrayList<>();
        for (Geo geo : geos) {
            result.add(new GeoCoordinate(geo.lat, geo.lng));
        }
        return result;
    }

    public static GeoCoordinate convertFrom(Geo geo) {
        return new GeoCoordinate(geo.lat, geo.lng);
    }

    /**
     * Convert from GeoCoordinate to Geo
     */
    public static List<Geo> convertToGeo(List<GeoCoordinate> geos) {
        List<Geo> result = new ArrayList<>();

        for (GeoCoordinate geo : geos) {
            long cellId = S2Utils.getCellId(geo.getLatitude(), geo.getLongitude()).id();
            result.add(new Geo(geo.getLatitude(), geo.getLongitude(), cellId));
        }
        return result;
    }

    public static Geo convertToGeo(GeoCoordinate geo) {
        long cellId = S2Utils.getCellId(geo.getLatitude(), geo.getLongitude()).id();
        return new Geo(geo.getLatitude(), geo.getLongitude(), cellId);
    }

    /**
     * Random utils
     */
    public static int randomColorArgb() {
        int R = (int)(Math.random()*256);
        int G = (int)(Math.random()*256);
        int B= (int)(Math.random()*256);
        return Color.argb(100,R,G,B);
    }

    /**
     * Network utils
     */
    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else if(Build.VERSION.SDK_INT >= 21){
            Network[] info = connectivity.getAllNetworks();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i] != null && connectivity.getNetworkInfo(info[i]).isConnected()) {
                        return true;
                    }
                }
            }
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
            final NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected()) {
                return true;
            }
        }

        return false;
    }


    public interface OnOkClickListener{
        void onClick(DialogInterface dialog, int which);
    }

    public static void displayAlert(@NonNull final String title,@NonNull final String message,@NonNull final Activity context, final OnOkClickListener listener) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(context)
                        .setTitle(title)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.onClick(dialog, which);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }
}