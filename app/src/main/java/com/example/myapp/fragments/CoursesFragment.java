package com.example.myapp.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapp.R;
import com.example.myapp.adapters.CourseAdapter;
import com.example.myapp.database.Course;
import com.example.myapp.database.DatabaseHelper;
import com.example.myapp.database.Teacher;
import com.example.myapp.databinding.FragmentCoursesBinding;
import com.example.myapp.viewmodels.CourseViewModel;

import java.util.ArrayList;
import java.util.List;

public class CoursesFragment extends Fragment {
    private FragmentCoursesBinding binding;
    private CourseViewModel courseViewModel;
    private CourseAdapter courseAdapter;
    private List<Teacher> teacherList = new ArrayList<>();
    private List<Course> allCourses = new ArrayList<>(); // Store all courses
    private List<Course> filteredCourses = new ArrayList<>(); // Store filtered courses

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCoursesBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        courseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);
        // Setup RecyclerView
        setupRecyclerView();
        // Setup Add Course Button
        setupAddCourseButton();
        // Observe Courses
        observeCourses();
        // Observe Teachers
        observeTeachers();
        setupSearchFunctionality();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        // Get DatabaseHelper instance
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());

        // Pass dbHelper to adapter
        courseAdapter = new CourseAdapter(dbHelper);
        binding.recyclerViewCourses.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewCourses.setAdapter(courseAdapter);

        // Setup item click listeners
        courseAdapter.setOnItemClickListener(new CourseAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Course course) {
                showAddEditCourseDialog(course);
            }

            @Override
            public void onDeleteClick(Course course) {
                courseViewModel.deleteCourse(course);
                observeCourses();
                setupRecyclerView();
            }
        });
    }

    private void setupAddCourseButton() {
        binding.buttonAddCourse.setOnClickListener(v -> showAddEditCourseDialog(null));
    }

    private void observeCourses() {
        courseViewModel.getAllCourses().observe(getViewLifecycleOwner(), courses -> {
            if (courses != null && !courses.isEmpty()) {
                // Log the courses
                for (Course course : courses) {
                    Log.d("CoursesFragment", "Course: " + course.getCourse_name());
                }

                // Clear existing lists and add new data
                allCourses.clear();
                allCourses.addAll(courses); // Store all courses
                filteredCourses.clear();
                filteredCourses.addAll(courses); // Initially show all courses
            } else {
                Log.d("CoursesFragment", "No courses available.");
            }

            // Update the adapter with the filtered list
            courseAdapter.submitList(filteredCourses);

            // Show/hide empty state based on courses
            binding.textViewNoCourses.setVisibility(
                    courses == null || courses.isEmpty() ? View.VISIBLE : View.GONE
            );
        });
    }


    private void observeTeachers() {
        courseViewModel.getAllTeachers().observe(getViewLifecycleOwner(), teachers -> {
            teacherList = teachers;
        });
    }

    private void showAddEditCourseDialog(Course existingCourse) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_edit_course, null);

        EditText courseNameEditText = dialogView.findViewById(R.id.editTextCourseName);
        EditText coefficientEditText = dialogView.findViewById(R.id.editTextCoefficient);
        Spinner teacherSpinner = dialogView.findViewById(R.id.spinnerTeachers);

        // Populate teacher spinner
        List<String> teacherNames = new ArrayList<>();
        for (Teacher teacher : teacherList) {
            teacherNames.add(teacher.getName());
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                teacherNames
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teacherSpinner.setAdapter(spinnerAdapter);

        // If editing an existing course, pre-populate fields
        if (existingCourse != null) {
            courseNameEditText.setText(existingCourse.getCourse_name());
            coefficientEditText.setText(String.valueOf(existingCourse.getCoefficient()));

            // Set selected teacher
            for (int i = 0; i < teacherList.size(); i++) {
                if (teacherList.get(i).getId() == existingCourse.getTeacherId()) {
                    teacherSpinner.setSelection(i);
                    break;
                }
            }
        }

        builder.setTitle(existingCourse == null ? "Add Course" : "Edit Course")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String courseName = courseNameEditText.getText().toString().trim();
                    String coefficientStr = coefficientEditText.getText().toString().trim();
                    int selectedTeacherIndex = teacherSpinner.getSelectedItemPosition();

                    if (courseName.isEmpty() || coefficientStr.isEmpty()) {
                        Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double coefficient = Double.parseDouble(coefficientStr);
                        int teacherId = teacherList.get(selectedTeacherIndex).getId();

                        Course course = existingCourse == null
                                ? new Course(courseName, teacherId, coefficient)
                                : existingCourse;

                        course.setCourse_name(courseName);
                        course.setTeacherId(teacherId);
                        course.setCoefficient(coefficient);

                        if (existingCourse == null) {
                            courseViewModel.insertCourse(course);
                            observeCourses();
                            setupRecyclerView();
                        } else {
                            courseViewModel.updateCourse(course);
                            observeCourses();
                            setupRecyclerView();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(requireContext(), "Invalid coefficient", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void setupSearchFunctionality() {
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                filterCourses(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // No action needed here
            }
        });
    }

    private void filterCourses(String query) {
        filteredCourses.clear();
        if (query.isEmpty()) {
            // Show all courses if the query is empty
            filteredCourses.addAll(allCourses);
        } else {
            // Filter the courses based on the query (by course_name)
            for (Course course : allCourses) {
                if (course.getCourse_name().toLowerCase().contains(query.toLowerCase())) {
                    filteredCourses.add(course);
                }
            }
        }
        // Submit the filtered list to the adapter
        courseAdapter.submitList(new ArrayList<>(filteredCourses));
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}