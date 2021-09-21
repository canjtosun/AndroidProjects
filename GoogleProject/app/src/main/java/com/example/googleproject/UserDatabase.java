package com.example.googleproject;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {User.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class UserDatabase extends RoomDatabase {

    private static UserDatabase instance;
    private static Context activity;

    public abstract UserDao userDao();

    public static synchronized UserDatabase getInstance(Context context) {
        activity = context.getApplicationContext();

        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    UserDatabase.class, "user_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallBack)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallBack = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private UserDao userDao;


        private PopulateDbAsyncTask(UserDatabase db) {
            userDao = db.userDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}

