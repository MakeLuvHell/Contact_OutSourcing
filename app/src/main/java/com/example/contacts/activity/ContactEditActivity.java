package com.example.contacts.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.contacts.R;
import com.example.contacts.model.Contact;
import com.example.contacts.viewmodel.ContactViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class ContactEditActivity extends AppCompatActivity {

    private TextInputEditText nameEditText, phoneEditText, emailEditText;
    private Button saveContactButton;
    private ContactViewModel contactViewModel;
    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_edit);

        // 初始化视图
        nameEditText = findViewById(R.id.edit_name);
        phoneEditText = findViewById(R.id.edit_phone);
        emailEditText = findViewById(R.id.edit_email);
        saveContactButton = findViewById(R.id.save_contact_button);

        // 初始化ViewModel
        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);

        // 获取联系人ID并加载详细信息
        int contactId = getIntent().getIntExtra("CONTACT_ID", -1);
        if (contactId != -1) {
            contactViewModel.getContactById(contactId).observe(this, contact -> {
                this.contact = contact;
                if (contact != null) {
                    // 加载数据到视图
                    nameEditText.setText(contact.getName());
                    phoneEditText.setText(contact.getPhone());
                    emailEditText.setText(contact.getEmail());

                }
            });
        }


        // 保存联系人
        saveContactButton.setOnClickListener(view -> saveContact());

        setupToolbar();
    }


    // 保存联系人
    private void saveContact() {
        if (contact == null) {
            contact = new Contact(
                    nameEditText.getText().toString(),
                    phoneEditText.getText().toString(),
                    emailEditText.getText().toString()
            );
            contactViewModel.insert(contact);
            Log.d("ContactFragment","saved！" );
        } else {
            contact.setName(nameEditText.getText().toString());
            contact.setPhone(phoneEditText.getText().toString());
            contact.setEmail(emailEditText.getText().toString());
            contactViewModel.update(contact);
        }
        finish();
    }


    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_edit);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("编辑");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(view -> onBackPressed());
        }
    }
}
