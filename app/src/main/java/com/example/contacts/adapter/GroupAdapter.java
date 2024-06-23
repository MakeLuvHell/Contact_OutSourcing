package com.example.contacts.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contacts.R;
import com.example.contacts.model.Contact;
import com.example.contacts.model.Group;
import com.example.contacts.viewmodel.ContactViewModel;
import com.example.contacts.viewmodel.GroupViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroupAdapter extends ListAdapter<Group, GroupAdapter.GroupViewHolder> {
    private final Context context;
    private final ContactViewModel contactViewModel;
    private final GroupViewModel groupViewModel;
    private final List<Contact> contactList;

    public GroupAdapter(Context context, ContactViewModel contactViewModel, GroupViewModel groupViewModel, List<Contact> contactList) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.contactViewModel = contactViewModel;
        this.groupViewModel = groupViewModel;
        this.contactList = contactList;
    }

    private static final DiffUtil.ItemCallback<Group> DIFF_CALLBACK = new DiffUtil.ItemCallback<Group>() {
        @Override
        public boolean areItemsTheSame(@NonNull Group oldItem, @NonNull Group newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Group oldItem, @NonNull Group newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 创建并返回 GroupViewHolder
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        // 绑定分组数据到视图
        Group currentGroup = getItem(position);
        holder.bind(currentGroup);
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {
        private final TextView groupNameTextView;
        private final RecyclerView contactsRecyclerView;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupNameTextView = itemView.findViewById(R.id.group_name);
            contactsRecyclerView = itemView.findViewById(R.id.contacts_recycler_view);
        }

        public void bind(Group group) {
            // 将分组数据填充到视图中
            groupNameTextView.setText(group.getName());

            // 过滤当前组的联系人
            List<Contact> filteredContacts = contactList.stream()
                    .filter(contact -> contact.getGroupId() == group.getId())
                    .collect(Collectors.toList());

            ContactAdapter contactAdapter = new ContactAdapter(context, contactViewModel);
            contactsRecyclerView.setAdapter(contactAdapter);
            contactsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            contactAdapter.submitList(filteredContacts);

            // 设置点击展开逻辑
            itemView.setOnClickListener(v -> {
                if (contactsRecyclerView.getVisibility() == View.GONE) {
                    contactsRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    contactsRecyclerView.setVisibility(View.GONE);
                }
            });

            // 设置长按删除逻辑
            itemView.setOnLongClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("删除分组")
                        .setMessage("确定要删除该分组及其下的所有联系人吗？")
                        .setPositiveButton("删除", (dialog, which) -> {
                            // 删除该分组及其下的所有联系人
                            deleteGroupAndContacts(group);
                        })
                        .setNegativeButton("取消", null)
                        .show();
                return true;
            });
        }
    }

    private void deleteGroupAndContacts(Group group) {
        // 删除该分组下的所有联系人
        List<Contact> contactsToDelete = contactList.stream()
                .filter(contact -> contact.getGroupId() == group.getId())
                .collect(Collectors.toList());

        for (Contact contact : contactsToDelete) {
            contactViewModel.delete(contact);
        }

        // 删除分组
        groupViewModel.delete(group);

        // 从当前列表中移除
        List<Group> currentGroups = new ArrayList<>(getCurrentList());
        currentGroups.remove(group);
        submitList(currentGroups);
    }
}
