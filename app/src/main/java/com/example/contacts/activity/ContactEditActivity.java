package com.example.contacts.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.contacts.R;
import com.example.contacts.model.Contact;
import com.example.contacts.viewmodel.ContactViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class ContactEditActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private TextInputEditText nameEditText, phoneEditText, emailEditText;
    private ImageView contactImageView;
    private Button saveContactButton;
    private ContactViewModel contactViewModel;
    private Contact contact;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_edit);

        // 初始化视图
        nameEditText = findViewById(R.id.edit_name);
        phoneEditText = findViewById(R.id.edit_phone);
        emailEditText = findViewById(R.id.edit_email);
        contactImageView = findViewById(R.id.contact_image);
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
                    if (contact.getPhotoUri() != null) {
                        photoUri = Uri.parse(contact.getPhotoUri());
                        Glide.with(this)
                                .load(photoUri)
                                .apply(RequestOptions.circleCropTransform())
                                .into(contactImageView);
                    }
                }
            });
        }
        // 设置点击事件选择图片
        contactImageView.setOnClickListener(view -> openImagePicker());
        // 保存联系人
        saveContactButton.setOnClickListener(view -> saveContact());

        setupToolbar();
    }

    // 打开相册选择器
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // 保存联系人
    private void saveContact() {
        if (contact == null) {
            contact = new Contact(
                    nameEditText.getText().toString(),
                    phoneEditText.getText().toString(),
                    emailEditText.getText().toString(),
                    photoUri != null ? photoUri.toString() : null
            );
            contactViewModel.insert(contact);
            Log.d("ContactFragment","saved！" );
        } else {
            contact.setName(nameEditText.getText().toString());
            contact.setPhone(phoneEditText.getText().toString());
            contact.setEmail(emailEditText.getText().toString());
            contact.setPhotoUri(photoUri != null ? photoUri.toString() : null);
            contactViewModel.update(contact);
        }
        finish();
    }

    // 处理图片选择结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 检查请求代码和结果代码，以确保图片被成功选择
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // 获取选中图片的URI
            Uri imageUri = data.getData();
            // 保存图片到本地存储，并获取本地存储的URI
            photoUri = saveImageToInternalStorage(imageUri);
            // 使用Glide加载本地存储的图片到ImageView
            Glide.with(this)
                    .load(photoUri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(contactImageView);
        }
    }

    // 将图片保存到应用的内部存储，并返回本地存储的URI
    private Uri saveImageToInternalStorage(Uri imageUri) {
        try {
            // 打开输入流读取选中的图片
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream == null) return null;

            // 创建一个文件用于存储图片
            File imageFile = new File(getFilesDir(), "contact_image_" + System.currentTimeMillis() + ".jpg");
            // 打开输出流将图片写入文件
            FileOutputStream outputStream = new FileOutputStream(imageFile);

            // 将图片从输入流写入输出流
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            // 关闭流
            outputStream.close();
            inputStream.close();

            // 返回本地存储的图片URI
            return Uri.fromFile(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
