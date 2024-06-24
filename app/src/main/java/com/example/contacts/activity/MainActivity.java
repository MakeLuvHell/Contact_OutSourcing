package com.example.contacts.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;


import com.example.contacts.R;
import com.example.contacts.adapter.ViewPagerAdapter;
import com.example.contacts.fragment.ContactFragment;
import com.example.contacts.fragment.GroupFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar();
        setupViewPagerAndTabs();
    }

    /**
     * 初始化并设置工具栏。
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("联系人");
    }

    /**
     * 初始化并设置 ViewPager 和 TabLayout。
     */
    private void setupViewPagerAndTabs() {
        TabLayout tabLayout = findViewById(R.id.tab_layout_test);
        ViewPager viewPager = findViewById(R.id.view_pager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ContactFragment(), "联系人");
        adapter.addFragment(new GroupFragment(), "分组");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    // 待设置页面实现添加跳转逻辑
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add){
            Intent intent = new Intent(this, ContactEditActivity.class);
            startActivity(intent);
        }
        return true;
    }
}

