package com.example.pocketforager.data;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;

@Database(entities = { PlantEntity.class }, version = 1, exportSchema = false)

@TypeConverters({ Converters.class })
public abstract class AppDatabase extends RoomDatabase {
    private static final String DB_NAME = "forager-db";
    private static volatile AppDatabase INSTANCE;

    public abstract PlantDao plantDao();


    public static AppDatabase getDatabase(final Context context) {

        if (INSTANCE == null) {

            synchronized (AppDatabase.class) {

                if (INSTANCE == null) {

                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "plant_database")
                            .build();
                }
            }
        }

        return INSTANCE;
    }

    public static AppDatabase getInstance(Context context) {

        if (INSTANCE == null) {

            synchronized (AppDatabase.class) {

                if (INSTANCE == null) {

                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "plant_database")
                            .fallbackToDestructiveMigration().build();
                }
            }
        }

        return INSTANCE;
    }
}

