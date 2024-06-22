package com.example.contacts.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.contacts.R;
import com.example.contacts.model.Contact;
import com.example.contacts.viewmodel.ContactViewModel;

import java.util.Objects;

public class ContactDetailActivity extends AppCompatActivity {
    private ImageView contactImageView;
    private TextView nameTextView, phoneTextView, emailTextView;
    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        // 初始化视图
        contactImageView = findViewById(R.id.contact_image);
        nameTextView = findViewById(R.id.name_text);
        phoneTextView = findViewById(R.id.phone_text);
        emailTextView = findViewById(R.id.email_text);

        // 获取联系人ID并加载详细信息
        int contactId = getIntent().getIntExtra("CONTACT_ID", -1);
        if (contactId != -1) {
            ContactViewModel contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);
            contactViewModel.getContactById(contactId).observe(this, contact -> {
                this.contact = contact;
                if (contact != null) {
                    // 更新UI
                    nameTextView.setText(contact.getName());
                    phoneTextView.setText(contact.getPhone());
                    emailTextView.setText(contact.getEmail());
                }
            });
        }
        setupToolbar();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("详情");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(view -> onBackPressed());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            Intent intent = new Intent(ContactDetailActivity.this, ContactEditActivity.class);
            intent.putExtra("CONTACT_ID", contact.getId());
            startActivity(intent);
        }
        return true;
    }
}
