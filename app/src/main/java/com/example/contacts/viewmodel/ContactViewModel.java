package com.example.contacts.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.contacts.model.Contact;
import com.example.contacts.repository.ContactRepository;

import java.util.List;

public class ContactViewModel extends AndroidViewModel {
    private final ContactRepository repository;
    private final LiveData<List<Contact>> allContacts;

    public ContactViewModel(@NonNull Application application) {
        super(application);
        repository = new ContactRepository(application);
        allContacts = repository.getAllContacts();
        Log.d("ContactViewModel","ViewModel Create And Fetched");
    }

    public LiveData<List<Contact>> getAllContacts() {
        Log.d("ContactViewModel","Get All_View");
        return allContacts;
    }
    public LiveData<Contact> getContactById(int id) {
        Log.d("ContactViewModel","Get id:" + id);
        return repository.getContactById(id);
    }

    public void insert(Contact contact) {
        Log.d("ContactViewModel","insert");
        repository.insert(contact);
    }

    public void update(Contact contact) {
        Log.d("ContactViewModel","update");
        repository.update(contact);
    }

    public void delete(Contact contact) {
        repository.delete(contact);
    }
}

