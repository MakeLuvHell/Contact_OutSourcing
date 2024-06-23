package com.example.contacts.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contacts.R;
import com.example.contacts.adapter.ContactAdapter;

import com.example.contacts.viewmodel.ContactViewModel;
//test git checkout -b

public class ContactFragment extends Fragment {
    private RecyclerView recyclerView;
    private ContactViewModel contactViewModel;
    private ContactAdapter contactAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        setUpRecyclerView(view);
        observeContacts();
        return view;
    }

     void observeContacts() {
        contactViewModel.getAllContacts().observe(getViewLifecycleOwner(), contacts -> {
            Log.d("ContactFragment","Contact observed" + contacts);
            contactAdapter.submitList(contacts); // 更新列表
        });
    }

    private void setUpRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);
        contactAdapter = new ContactAdapter(getContext(), contactViewModel);
        recyclerView.setAdapter(contactAdapter);
        Log.d("ContactFragment","RecycleView setup");

        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                contactAdapter.filter(newText);
                return true;
            }
        });
        contactViewModel.getAllContacts().observe(getViewLifecycleOwner(), contacts -> {
            if (contacts != null) {
                contactAdapter.submitList(contacts);
            }
        });
    }

}