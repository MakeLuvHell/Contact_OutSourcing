package com.example.contacts.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.contacts.R;
import com.example.contacts.activity.ContactDetailActivity;
import com.example.contacts.model.Contact;
import com.example.contacts.viewmodel.ContactViewModel;

public class ContactAdapter extends ListAdapter<Contact, ContactAdapter.ContactViewHolder> {
    private final Context context;
    private final ContactViewModel contactViewModel;

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
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact_card, parent, false);

        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
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
            if (contact.getPhotoUri() != null) {
                Glide.with(context)
                        .load(contact.getPhotoUri())
                        .into(contactImageView);
            } else {
                contactImageView.setImageResource(R.drawable.ic_default);
            }
        }

        private void showDeleteConfirmationDialog(Contact contact) {
            new AlertDialog.Builder(context)
                    .setTitle("删除联系人")
                    .setMessage("你确定要删除该联系人？")
                    .setPositiveButton("是的", (dialog, which) -> contactViewModel.delete(contact))
                    .setNegativeButton("取消", null)
                    .show();
        }
    }
}
