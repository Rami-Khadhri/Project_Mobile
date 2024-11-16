package com.example.myapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapp.AuthActivity;
import com.example.myapp.SessionManager;
import com.example.myapp.database.User;
import com.example.myapp.databinding.FragmentProfileBinding;
import com.example.myapp.viewmodels.ProfileViewModel;
public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;
    private final int userId;
    private final String username, email, password;

    public ProfileFragment(int userId, String username, String email, String password) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        populateUserProfile();
        binding.buttonUpdateProfile.setOnClickListener(v -> updateProfile());
        binding.btnLogout.setOnClickListener(v -> logout());

        // Observe user data changes
        viewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.textViewUsername.setText(String.format("Username: %s", user.getUsername()));
                binding.textViewPassword.setText(String.format("Password: %s", user.getPassword()));
                binding.textViewEmail.setText(String.format("Email: %s", user.getEmail()));
            }
        });
    }

    private void populateUserProfile() {
        binding.textViewUsername.setText(String.format("Username: %s", username));
        binding.textViewPassword.setText(String.format("Password: %s", password));
        binding.textViewEmail.setText(String.format("Email: %s", email));
        binding.editTextUsername.setText(username);
        binding.editTextPassword.setText(password);
        binding.editTextEmail.setText(email);
    }

    private void updateProfile() {
        String updatedUsername = binding.editTextUsername.getText().toString().trim();
        String updatedPassword = binding.editTextPassword.getText().toString().trim();
        String updatedEmail = binding.editTextEmail.getText().toString().trim();

        if (updatedUsername.isEmpty() || updatedEmail.isEmpty()) {
            Toast.makeText(getContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        User updatedUser = new User(userId, updatedUsername, updatedEmail, updatedPassword);
        viewModel.updateUserProfile(updatedUser);
        Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
    }

    private void logout() {
        new SessionManager(requireContext()).clearSession();
        startActivity(new Intent(requireContext(), AuthActivity.class));
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}