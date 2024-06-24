package com.example.contacts.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.contacts.dao.ContactDao;
import com.example.contacts.dao.GroupDao;
import com.example.contacts.model.Contact;
import com.example.contacts.model.Group;

@Database(entities = {Contact.class, Group.class}, version = 4, exportSchema = false)
public abstract class ContactDatabase extends RoomDatabase {
    private static volatile ContactDatabase instance;
    public abstract ContactDao contactDao();
    public abstract GroupDao groupDao();

    public static synchronized ContactDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            ContactDatabase.class, "contact_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
