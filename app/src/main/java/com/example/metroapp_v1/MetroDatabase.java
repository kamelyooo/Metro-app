package com.example.metroapp_v1;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {UserHistory.class}, version = 1)

public abstract class MetroDatabase extends RoomDatabase {
    public abstract userDAO userDAO();

    public static MetroDatabase ourInstance;

    public static MetroDatabase getInstance(Context context) {

        if (ourInstance == null) {

            ourInstance = Room.databaseBuilder(context,

                    MetroDatabase.class, "metro.db")
                    .createFromAsset("database/metro.db")
                    .allowMainThreadQueries()
                    .build();
        }

        return ourInstance;

    }
}
