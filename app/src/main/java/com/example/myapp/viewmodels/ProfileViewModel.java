package com.example.myapp.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.myapp.database.AppDatabase;
import com.example.myapp.database.User;
import com.example.myapp.database.UserDao;

public class ProfileViewModel extends AndroidViewModel {
    private final UserDao userDao;
    private final MutableLiveData<User> userLiveData;
    private final MutableLiveData<Boolean> updateSuccessLiveData;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        userDao = AppDatabase.getDatabase(application).userDao();
        userLiveData = new MutableLiveData<>();
        updateSuccessLiveData = new MutableLiveData<>();
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<Boolean> getUpdateSuccessLiveData() {
        return updateSuccessLiveData;
    }

    public void loadUserProfile(User authenticatedUser) {
        new Thread(() -> {
            User user = userDao.getUserById(authenticatedUser.getId());
            if (user != null) {
                userLiveData.postValue(user);
            } else {
                userLiveData.postValue(authenticatedUser);
            }
        }).start();
    }

    public void updateUserProfile(User user) {
        new Thread(() -> {
            try {
              //  userDao.updateUser(user);
              userDao.updateUserById(user.getId(), user.getUsername(), user.getEmail(), user.getPassword());
                userLiveData.postValue(user); // Update the LiveData after successful DB update
                updateSuccessLiveData.postValue(true);
            } catch (Exception e) {
                e.printStackTrace();
                updateSuccessLiveData.postValue(false);
            }
        }).start();
    }
}