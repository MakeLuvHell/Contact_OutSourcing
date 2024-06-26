package com.example.contacts.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.contacts.R;
import com.example.contacts.activity.ContactDetailActivity;
import com.example.contacts.model.Contact;
import com.example.contacts.viewmodel.ContactViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends ListAdapter<Contact, ContactAdapter.ContactViewHolder> {
    private final Context context;
    private List<Contact> contactListFull; // 这个列表保存所有联系人数据
    private final ContactViewModel contactViewModel;
    private List<Contact> contacts = new ArrayList<>(); // 初始化为空列表

    public ContactAdapter(Context context, ContactViewModel contactViewModel) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.contactViewModel = contactViewModel;
    }

    private static final DiffUtil.ItemCallback<Contact> DIFF_CALLBACK = new DiffUtil.ItemCallback<Contact>() {
        @Override
        public boolean areItemsTheSame(@NonNull Contact oldItem, @NonNull Contact newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Contact oldItem, @NonNull Contact newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 创建并返回 ContactViewHolder
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact_card, parent, false);

        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        // 绑定联系人数据到视图
        Contact currentContact = getItem(position);
        holder.bind(currentContact);
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView phoneTextView;
        private final ImageView contactImageView;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.contact_name);
            phoneTextView = itemView.findViewById(R.id.contact_phone);
            contactImageView = itemView.findViewById(R.id.contact_image);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Contact contact = getItem(position);
                    Intent intent = new Intent(context, ContactDetailActivity.class);
                    intent.putExtra("CONTACT_ID", contact.getId());
                    context.startActivity(intent);
                }
            });

            itemView.setOnLongClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Contact contact = getItem(position);
                    showDeleteConfirmationDialog(contact);
                }
                return true;
            });
        }

        public void bind(Contact contact) {
            nameTextView.setText(contact.getName());
            phoneTextView.setText(contact.getPhone());
            // 检查联系人是否有图片URI
            if (contact.getPhotoUri() != null) {
                // 解析图片URI
                Uri photoUri = Uri.parse(contact.getPhotoUri());
                Log.d("ContactAdapter", "Photo URI: " + photoUri.toString());

                // 创建文件对象，检查文件是否存在
                File file = new File(photoUri.getPath());
                if (file.exists()) {
                    // 使用Glide加载图片文件到ImageView
                    Glide.with(context)
                            .load(file)
                            .apply(RequestOptions.circleCropTransform())
                            .into(contactImageView);
                } else {
                    // 如果文件不存在，使用默认图片
                    contactImageView.setImageResource(R.drawable.ic_default);
                }
            } else {
                // 如果没有图片URI，使用默认图片
                contactImageView.setImageResource(R.drawable.ic_default);
            }
        }

        private void showDeleteConfirmationDialog(Contact contact) {
            // 显示删除确认对话框
            new AlertDialog.Builder(context)
                    .setTitle("删除联系人")
                    .setMessage("你确定要删除该联系人？")
                    .setPositiveButton("是的", (dialog, which) -> contactViewModel.delete(contact))
                    .setNegativeButton("取消", null)
                    .show();
        }
    }

    // 对 Adapter 中的 submitList() 方法进行重写
    // 用于提交新的联系人列表并更新数据集。
    @Override
    public void submitList(@Nullable List<Contact> list){
        if (contacts == null) {
            contacts = new ArrayList<>(); // 防止传入空列表
        }
        super.submitList(list != null ? new ArrayList<>(list) : null);
        if (list != null) {
            if (contactListFull == null) {
                contactListFull = new ArrayList<>(list);
            } else {
                contactListFull.clear();
                contactListFull.addAll(list);
            }
        }
    }
}
