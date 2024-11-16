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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapp.AuthActivity;
import com.example.myapp.SessionManager;
import com.example.myapp.adapters.TaskAdapter;
import com.example.myapp.database.Task;
import com.example.myapp.databinding.FragmentTasksBinding;
import com.example.myapp.viewmodels.TaskViewModel;

public class TasksFragment extends Fragment {

    private FragmentTasksBinding binding;
    private TaskViewModel taskViewModel;
    private TaskAdapter taskAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTasksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialiser RecyclerView
        binding.recyclerViewTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        taskAdapter = new TaskAdapter();
        binding.recyclerViewTasks.setAdapter(taskAdapter);

        // Initialiser ViewModel
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        taskViewModel.getAllTasks().observe(getViewLifecycleOwner(), tasks -> taskAdapter.submitList(tasks));

        // Ajouter une tâche
        binding.buttonAddTask.setOnClickListener(v -> {
            String taskName = binding.editTextTaskName.getText().toString().trim();
            if (!taskName.isEmpty()) {
                Task task = new Task(taskName);
                taskViewModel.insert(task);
                binding.editTextTaskName.setText("");
                Toast.makeText(getContext(), "Tâche ajoutée", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Le nom de la tâche ne peut pas être vide", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void handleLogout() {
        // Clear the session and navigate to LoginActivity
        SessionManager sessionManager = new SessionManager(requireContext());
        sessionManager.clearSession();

        Intent intent = new Intent(getActivity(), AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
