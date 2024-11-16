package com.example.myapp.viewmodels;
import com.example.myapp.HomeActivity;
import com.example.myapp.SessionManager;
import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapp.SessionManager;
import com.example.myapp.database.AppDatabase;
import com.example.myapp.database.User;
import com.example.myapp.database.UserDao;
public class ProfileViewModel extends AndroidViewModel {
    private final UserDao userDao;
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        userDao = AppDatabase.getDatabase(application).userDao();
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public void updateUserProfile(User user) {
        new Thread(() -> {
            userDao.updateUser(user);
            userLiveData.postValue(user);
        }).start();
    }
}