package com.example.contacts.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contacts.R;
import com.example.contacts.adapter.SectionAdapter;
import com.example.contacts.model.Contact;
import com.example.contacts.viewmodel.ContactViewModel;
import com.example.contacts.view.SideLetterBar;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class ContactFragment extends Fragment {
    private RecyclerView recyclerView;
    private ContactViewModel contactViewModel;
    private SectionAdapter sectionAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        // 设置 RecyclerView
        setUpRecyclerView(view);
        return view;
    }

    private void setUpRecyclerView(View view) {
        // 初始化 RecyclerView 和布局管理器
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 初始化 ViewModel 和适配器
        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);
        boolean useCardView = true;
        sectionAdapter = new SectionAdapter(getContext(), contactViewModel, useCardView);
        recyclerView.setAdapter(sectionAdapter);

        Log.d("ContactFragment","RecycleView setup");

         // 设置搜索视图的查询监听器
        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // 不处理提交事件
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText); // 过滤联系人列表
                return true;
            }
        });

        // 初始化侧边字母栏
        SideLetterBar sideLetterBar = view.findViewById(R.id.side_letter_bar);
        TextView letterOverlay = new TextView(getContext());
        letterOverlay.setVisibility(View.GONE); // 默认隐藏字母覆盖层
        sideLetterBar.setOverlay(letterOverlay);
        sideLetterBar.setOnLetterChangedListener(letter -> {
            // 根据字母滚动到相应位置
            int position = sectionAdapter.getPositionForSection(letter.charAt(0));
            if (position != -1) {
                ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).scrollToPositionWithOffset(position, 0);
            }
        });

        // 观察联系人数据变化
        contactViewModel.getAllContacts().observe(getViewLifecycleOwner(), contacts -> {
            // 对联系人按拼音排序
            contacts.sort((c1, c2) -> {
                String name1 = getPinyin(c1.getName());
                String name2 = getPinyin(c2.getName());
                return name1.compareToIgnoreCase(name2);
            });
            // 提交排序后的联系人列表到适配器
            sectionAdapter.submitListWithHeaders(contacts);
        });
    }


    private String getPinyin(String input) {
        // 将字符串转换为拼音
        StringBuilder pinyin = new StringBuilder();
        for (char c : input.toCharArray()) {
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
            if (pinyinArray != null && pinyinArray.length > 0) {
                pinyin.append(pinyinArray[0]);
            } else {
                pinyin.append(c);
            }
        }
        return pinyin.toString();
    }

    private void filter(String text) {
        // 过滤联系人列表
        contactViewModel.getAllContacts().observe(getViewLifecycleOwner(), contacts -> {
            List<Contact> filteredList = contacts.stream()
                    .filter(contact -> contact.getName().toLowerCase().contains(text.toLowerCase()))
                    .collect(Collectors.toList());
            sectionAdapter.submitListWithHeaders(filteredList);
        });
    }
}