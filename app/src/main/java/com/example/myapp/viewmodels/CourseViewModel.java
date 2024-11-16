package com.example.myapp.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.myapp.database.AppDatabase;
import com.example.myapp.database.Course;
import com.example.myapp.database.CourseDao;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CourseViewModel extends AndroidViewModel {

    private final CourseDao courseDao;
    private final LiveData<List<Course>> allCourses;
    private final ExecutorService executorService;

    public CourseViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        courseDao = db.courseDao();
        allCourses = courseDao.getAllCourses();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Course>> getAllCourses() {
        return allCourses;
    }

    public void insert(Course course) {
        executorService.execute(() -> courseDao.insert(course));
    }

    public void update(Course course) {
        executorService.execute(() -> courseDao.update(course));
    }

    public void delete(Course course) {
        executorService.execute(() -> courseDao.delete(course));
    }
}
