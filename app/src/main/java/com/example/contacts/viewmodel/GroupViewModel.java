package com.example.contacts.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.contacts.model.Group;
import com.example.contacts.repository.GroupRepository;

import java.util.List;

public class GroupViewModel extends AndroidViewModel {

    private final GroupRepository repository;
    private final LiveData<List<Group>> allGroups;
    public GroupViewModel(@NonNull Application application) {
        super(application);
        repository = new GroupRepository(application);
        allGroups = repository.getAllGroups();
    }

    public LiveData<List<Group>> getAllGroups() {
        return allGroups;
    }

    public void insertGroup(Group group) {
        repository.insert(group);
    }

    public void delete(Group group) {
        repository.delete(group);
    }

    public boolean checkIfGroupExists(String groupName) {
        List<Group> groups = allGroups.getValue();
        if (groups != null) {
            for (Group group : groups) {
                if (group.getName().equals(groupName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
