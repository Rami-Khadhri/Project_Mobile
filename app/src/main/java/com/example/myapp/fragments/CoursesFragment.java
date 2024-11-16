package com.example.myapp.fragments;

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

import com.example.myapp.adapters.CourseAdapter;
import com.example.myapp.database.Course;
import com.example.myapp.databinding.FragmentCoursesBinding;
import com.example.myapp.viewmodels.CourseViewModel;

public class CoursesFragment extends Fragment {

    private FragmentCoursesBinding binding;
    private CourseViewModel courseViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCoursesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize RecyclerView
        binding.recyclerViewCourses.setLayoutManager(new LinearLayoutManager(getContext()));
        final CourseAdapter adapter = new CourseAdapter(new CourseAdapter.CourseDiff());
        binding.recyclerViewCourses.setAdapter(adapter);

        // Initialize ViewModel
        courseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);
        courseViewModel.getAllCourses().observe(getViewLifecycleOwner(), adapter::submitList);

        // Add Course Button
        binding.buttonAddCourse.setOnClickListener(v -> {
            String courseName = binding.editTextCourseName.getText().toString();
            if (!courseName.isEmpty()) {
                courseViewModel.insert(new Course(courseName));
                binding.editTextCourseName.setText("");
                Toast.makeText(getContext(), "Course added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Course name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
