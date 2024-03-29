package saveteam.com.quagiang.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import saveteam.com.quagiang.database.dao.ProfileDao;
import saveteam.com.quagiang.database.dao.SearchPlaceHistoryDao;
import saveteam.com.quagiang.database.dao.UserDao;
import saveteam.com.quagiang.database.model.Converters;
import saveteam.com.quagiang.database.model.SearchPlaceHistory;
import saveteam.com.quagiang.database.model.User;
import saveteam.com.quagiang.firebase.model.ProfileFB;

@Database(entities = {User.class, ProfileFB.class, SearchPlaceHistory.class}, version = 2, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class RidesharingDB extends RoomDatabase {
    private static final String DATABASE_NAME = "ridesharing_db";

    public abstract UserDao getUserDao();
    public abstract ProfileDao getProfileDao();
    public abstract SearchPlaceHistoryDao getSearchPlaceHistoryDao();

    private static volatile RidesharingDB instance;

    public static RidesharingDB getInstance(Context context) {
        if (instance == null) {
            synchronized (RidesharingDB.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            RidesharingDB.class,
                            DATABASE_NAME)
                            .fallbackToDestructiveMigration().build();
                }
            }
        }

        return instance;
    }
}
