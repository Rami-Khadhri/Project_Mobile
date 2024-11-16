package com.example.myapp.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "courses")
public class Course {

     @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;

    // Constructors, getters et setters

    public Course(String name) {
        this.name = name;
    }

    // Getters et setters...

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
