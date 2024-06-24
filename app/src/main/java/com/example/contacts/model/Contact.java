package com.example.contacts.model;

import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "contacts")
public class Contact {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String phone;
    private String email;
    private String photoUri;
    private boolean isHeader;

    @Ignore
    public Contact(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;

    }

    public Contact() {}

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, phone, photoUri);
    }

    @NonNull
    @Override
    public String toString() {
        return name + "," + phone + "," + email;
    }

    public static Contact fromString(String  str){
        String[] part = str.split(",");
        if(part.length < 3){
            throw new IllegalArgumentException("Invalid contact string: " + str);
        }
        Contact con = new Contact();
        con.setName(part[0]);
        con.setPhone(part[1]);
        con.setEmail(part[2]);
        return con;

    }
}
