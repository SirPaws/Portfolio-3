package com.example.demo;

public class Course {
    Integer id;
    String name;
    Integer expected_student_count;
    Room[] rooms;
    Lecturer[] lecturers;
    Course(Integer id, String name,  Room[] rooms, Lecturer[] lecturers) {
        this.id = id;
        this.name = name;
        this.expected_student_count = 2;
        this.rooms = rooms;
        this.lecturers = lecturers;

    }

}class Room {
    public Room() {}
    public Room(Integer _id, String _name, Integer _capacity) {
        id       = _id;
        name     = _name;
        capacity = _capacity;
    }
    public Integer id;
    public String name;
    public Integer capacity;
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
    public Integer id;
    public String name;
    public String email;
}
class TimeTable {

    Integer time;
    TimeSlot slot;
    public TimeTable(Integer time, TimeSlot slot) {
        this.time = time;
        this.slot = slot;
    }

}

class TimeSlot {
    Integer id;
    Course course;
    public TimeSlot(Integer id, Course course) {
        this.id = id;
        this.course = course;
    }
}