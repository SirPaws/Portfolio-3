package com.example.demo;

import Database.Annotations.*;

class TimeTable {
    public TimeTable() {}
    public TimeTable(Integer time_) { time = time_; }
    // 0-9
    Integer time;
    @NotNull
    @Reference(TimeSlot.class)
    TimeSlot slot;
}

class TimeSlot {
    public TimeSlot() {}
    public TimeSlot(Integer id_) { id = id_; }
    @PrimaryKey
    Integer id;
    @NotNull @Reference(Course.class)
    Course course;
}

class Course {

    public Course() {}
    public Course(Integer id_, String name_, Integer num_students) {
        id = id_;
        name = name_;
        expected_student_count = num_students;
    }

    @PrimaryKey Integer id;

    @NotNull
    String name;

    @NotNull
    Integer expected_student_count;

    @NotNull @Array(RoomList.class)
    Room[] rooms;

    @NotNull @Array(LecturerList.class)
    Lecturer[] lecturers;
}

class Room {
    public Room() {}
    public Room(Integer _id, String _name, Integer _capacity) {
        id       = _id;
        name     = _name;
        capacity = _capacity;
    }
    @Override
    public String toString() {
        return name + " (" + capacity + ")";
    }
    @NotNull
    @PrimaryKey
    Integer id;

    @NotNull
    String name;

    @NotNull
    Integer capacity;
}

class Lecturer {
    public Lecturer() { }
    public Lecturer(Integer _id, String _name, String _email) {
        id    = _id;
        name  = _name;
        email = _email;
    }
    public Lecturer(Integer _id, String _name) {
        id    = _id;
        name  = _name;
    }
    @Override
    public String toString() {
        return name + " (" + email + ")";
    }

    @NotNull
    @PrimaryKey
    Integer id;

    @NotNull
    String name;

    @Optional
    String email;
}

class LecturerList {
    public LecturerList() {}
    public LecturerList(Integer i) { id = i; }
    @NotNull @Key
    Integer id;
    @Value
    Lecturer value;
}

class RoomList {
    public RoomList() {}
    public RoomList(Integer i) { id = i; }
    @NotNull @Key
    Integer id;
    @Value
    Room value;
}
