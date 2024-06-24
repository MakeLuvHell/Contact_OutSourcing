package com.example.contacts.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.example.contacts.R;
import com.example.contacts.dao.ContactDao;
import com.example.contacts.database.ContactDatabase;
import com.example.contacts.model.Contact;
import com.example.contacts.model.Group;
import com.example.contacts.viewmodel.GroupViewModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SettingsActivity extends AppCompatActivity {
    private static final int PICK_FILE_REQUEST_CODE = 1;

    private final List<Group> groupList = new ArrayList<>();
    private final Executor executor = Executors.newSingleThreadExecutor();

    private ContactDao contactDao;
    private List<Contact> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setupTheme();
        setupToolbar();
        initContactDao();
        observeContacts();
        initGroupViewModel();
        setupSwitches();
        setupButtons();
    }

    /**
     * 设置应用主题，根据用户偏好设置选择暗主题或亮主题。
     */
    private void setupTheme() {
        boolean isDarkTheme = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("dark_theme", false);
        setTheme(isDarkTheme ? R.style.Base_Theme_ContactApp_Dark : R.style.Base_Theme_ContactApp);
    }

    /**
     * 初始化并设置工具栏。
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("设置");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 初始化 ContactDao 实例。
     */
    private void initContactDao() {
        ContactDatabase db = ContactDatabase.getInstance(this);
        contactDao = db.contactDao();
    }

    /**
     * 观察联系人数据变化并更新本地列表。
     */
    private void observeContacts() {
        contactDao.getAllContacts().observe(this, contacts -> contactList = contacts);
    }

    /**
     * 初始化 GroupViewModel 并观察分组数据变化。
     */
    private void initGroupViewModel() {
        GroupViewModel groupViewModel = new ViewModelProvider(this).get(GroupViewModel.class);
        groupViewModel.getAllGroups().observe(this, groups -> {
            groupList.clear();
            groupList.addAll(groups);
        });
    }

    /**
     * 设置显示方式和主题切换开关。
     */
    private void setupSwitches() {
        setupViewModeSwitch();
        setupThemeSwitch();
    }

    /**
     * 设置视图模式切换开关。
     */
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private void setupViewModeSwitch() {
        Switch viewModeSwitch = findViewById(R.id.switch_card_view);
        viewModeSwitch.setChecked(PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("card_view_mode", false));
        viewModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit().putBoolean("card_view_mode", isChecked).apply());
    }

    /**
     * 设置主题切换开关。
     */
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private void setupThemeSwitch() {
        Switch themeSwitch = findViewById(R.id.switch_theme);
        themeSwitch.setChecked(PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("dark_theme", false));
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit().putBoolean("dark_theme", isChecked).apply();
            setResult(RESULT_OK); // 通知 MainActivity 刷新
            recreate(); // 重新创建以应用新主题
        });
    }

    /**
     * 初始化按钮并设置点击事件。
     */
    private void setupButtons() {
        Button groupSettingsButton = findViewById(R.id.button_group_settings);
        groupSettingsButton.setOnClickListener(v -> showGroupSelectionDialog());

        Button exportContactButton = findViewById(R.id.button_export);
        exportContactButton.setOnClickListener(view -> exportContacts());

        Button importContactButton = findViewById(R.id.button_import);
        importContactButton.setOnClickListener(view -> openFilePicker());
    }

    /**
     * 显示分组选择对话框，允许用户选择要显示的分组。
     */
    private void showGroupSelectionDialog() {
        String[] groupNames = groupList.stream().map(Group::getName).toArray(String[]::new);
        boolean[] checkedItems = new boolean[groupList.size()];

        for (int i = 0; i < groupList.size(); i++) {
            checkedItems[i] = PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean("group_display_" + groupList.get(i).getId(), true);
        }

        new AlertDialog.Builder(this)
                .setTitle("选择分组显示")
                .setMultiChoiceItems(groupNames, checkedItems, (dialog, which, isChecked) ->
                        PreferenceManager.getDefaultSharedPreferences(this)
                                .edit().putBoolean("group_display_" + groupList.get(which).getId(), isChecked).apply())
                .setPositiveButton("确认", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * 导出联系人到文件。
     */
    private void exportContacts() {
        if (contactList == null) {
            Toast.makeText(this, "没有可导出的联系人", Toast.LENGTH_LONG).show();
            return;
        }

        File file = new File(getExternalFilesDir(null), "contacts.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Contact contact : contactList) {
                writer.write(contact.toString());
                writer.newLine();
            }
            Toast.makeText(this, "联系人已导出到：" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "导出失败", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    /**
     * 打开文件选择器以选择要导入的文件。
     */
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            importContacts(data.getData());
        }
    }

    /**
     * 从文件导入联系人。
     * @param uri 文件的 Uri
     */
    private void importContacts(Uri uri) {
        executor.execute(() -> {
            try (InputStream inputStream = getContentResolver().openInputStream(uri);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Contact contact = Contact.fromString(line);
                    contactDao.insert(contact);
                }
                runOnUiThread(() -> Toast.makeText(SettingsActivity.this, "联系人导入成功", Toast.LENGTH_LONG).show());
            } catch (IOException e) {
                runOnUiThread(() -> Toast.makeText(SettingsActivity.this, "导入失败", Toast.LENGTH_LONG).show());
                e.printStackTrace();
            }
        });
    }
}
