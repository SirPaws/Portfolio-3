package com.example.demo;

import Database.DataBase;
import Database.DataBaseTable;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InvalidClassException;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class HelloApplication extends Application {
    Button button;
    Button confirm;
    Button closeprogrm;
    Stage window;
    CheckBox[] boxes = new CheckBox[10];

    DataBase db = new DataBase();

    ArrayList<TimeTable> tables = new ArrayList<>();
    ArrayList<Room> rooms = new ArrayList<>();
    ArrayList<Lecturer> Lecturer = new ArrayList<>();
    ArrayList<Course> courses = new ArrayList<>();

    // choice boxes
    ChoiceBox<String> Lecture;
    ChoiceBox<String> Room;

    // labels
    Label lecturer_label;
    Label LecEmail;
    Label StudentCount;
    Label RoomCap;
    Label timePM;
    Label timeAM;

    private void initialiseDataBase() {
        db.dropNewTables(false);
        db.open();
        DataBaseTable<Lecturer> lecturer_table = new DataBaseTable<>(Lecturer.class);
        DataBaseTable<LecturerList> lecturer_list_table = new DataBaseTable<>(LecturerList.class);

        DataBaseTable<Room> room_table = new DataBaseTable<>(Room.class);
        DataBaseTable<RoomList> room_list_table = new DataBaseTable<>(RoomList.class);

        DataBaseTable<TimeTable> time_table_table = new DataBaseTable<>(TimeTable.class);
        DataBaseTable<TimeSlot> time_slot_table = new DataBaseTable<>(TimeSlot.class);
        DataBaseTable<Course> course_table = new DataBaseTable<>(Course.class);

        try {
            db.addTable(lecturer_list_table);
            db.addTable(lecturer_table);
            db.addTable(room_table);
            db.addTable(room_list_table);
            db.addTable(course_table);
            db.addTable(time_slot_table);
            db.addTable(time_table_table);
        } catch(InvalidClassException ignored) { }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        initialiseDataBase();
        Lecturer = db.select(com.example.demo.Lecturer.class);
        rooms = db.select(Room.class);
        tables = db.select(TimeTable.class);
        courses = db.select(Course.class);

        window = primaryStage;
        window.setTitle("Hello!");
        window.setOnCloseRequest(e -> {
            e.consume();
            closeProgram();

        });

        // Course klasse = tables.get(0).slot.course;

        //Test Data


        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10,10,10,10));
        grid.setVgap(8);
        grid.setHgap(8);

        GridPane layout = new GridPane();
        layout.setPadding(new Insets(10,10,10,10));
        layout.setVgap(8);
        layout.setHgap(8);

        GridPane Time = new GridPane();
        Time.setPadding(new Insets(10,10,10,10));
        Time.setVgap(8);
        Time.setHgap(8);

        BorderPane border = new BorderPane();
        border.setTop(grid);
        border.setBottom(layout);
        border.setLeft(Time);

        Lecture = new ChoiceBox<>();

        ArrayList<String> course_names = new ArrayList<>();
        for (Course c: courses) {
            course_names.add(c.name);
        }
        course_names.add(0, "Select Course");

        Lecture.getItems().addAll(course_names);
        Lecture.setValue("Select Course");
        Lecture.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue)-> updateCourse(newValue));

        Room = new ChoiceBox<>();

        ArrayList<String> room_names = new ArrayList<>();
        for (Room r: rooms) {
            room_names.add(r.name);
        }
        room_names.add(0, "Select Room");
        Room.getItems().addAll(room_names);
        Room.setValue("Select Room");
        Room.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue)-> checktime(newValue));

        button = new Button();
        button.setText("Edit Information");
        button.setOnAction(e -> {
            Course c = getCourseByName(Lecture.getValue());
            if (c != null) {
                new EditLecture(db, c).Display();

                Lecturer = db.select(com.example.demo.Lecturer.class);
                rooms = db.select(Room.class);
                tables = db.select(TimeTable.class);
                courses = db.select(Course.class);

                for (Course course: courses) {
                    if (Objects.equals(c.id, course.id)) c = course;
                }
                updateUI(c);
            }
        });
        Button create_new_course = new Button("Create New Course");
        create_new_course.setOnAction(e->{
            Course c = new CreateCourse(db).display();
            if (c != null) {
                Lecturer = db.select(com.example.demo.Lecturer.class);
                rooms = db.select(Room.class);
                tables = db.select(TimeTable.class);
                courses = db.select(Course.class);

                for (Course course: courses) {
                    if (Objects.equals(c.id, course.id)) c = course;
                }
                updateUI(c);
            }
        });

        confirm = new Button();
        confirm.setText("Confirm Booking");
        confirm.setOnAction(e -> ConfirmUpdate());
        closeprogrm = new Button();

        // ArrayList<String> strings = new ArrayList<>();
        // for (Lecturer lecture: klasse.lecturers)
        //     strings.add(lecture.name);

        // String lecturers = String.join(",", strings);
        lecturer_label = new Label("");

        // ArrayList<String> stringss = new ArrayList<>();
        // for (Lecturer lecture: klasse.lecturers)
        //     stringss.add(lecture.email);
        // String email = String.join(",", stringss);

        LecEmail = new Label("");

        StudentCount= new Label("");


        // ArrayList<String> stringsss = new ArrayList<>();
        // for (Room room: klasse.rooms)
        //     stringsss.add(room.capacity.toString());
        // String Capped = String.join(",", stringsss);
        RoomCap = new Label("");


        timePM = new Label("PM");
        timeAM = new Label("AM");

        GridPane.setConstraints(timeAM, 0,0);
        GridPane.setConstraints(timePM, 1,0);
        boxes[0] = new CheckBox("M");
        GridPane.setConstraints(boxes[0], 0,1);
        boxes[1] = new CheckBox("T");
        GridPane.setConstraints(boxes[1], 0,2);
        boxes[2] = new CheckBox("W");
        GridPane.setConstraints(boxes[2], 0,3);
        boxes[3] = new CheckBox("T");
        GridPane.setConstraints(boxes[3], 0,4);
        boxes[4] = new CheckBox("F");
        GridPane.setConstraints(boxes[4], 0,5);
        boxes[5] = new CheckBox();
        GridPane.setConstraints(boxes[5], 1,1);
        boxes[6] = new CheckBox();
        GridPane.setConstraints(boxes[6], 1,2);
        boxes[7] = new CheckBox();
        GridPane.setConstraints(boxes[7], 1,3);
        boxes[8] = new CheckBox();
        GridPane.setConstraints(boxes[8], 1,4);
        boxes[9] = new CheckBox();
        GridPane.setConstraints(boxes[9], 1,5);

        GridPane.setConstraints(confirm,            4,7);
        GridPane.setConstraints(button,             4,6);
        GridPane.setConstraints(Room,               4,2);
        GridPane.setConstraints(Lecture,            4,1);

        GridPane.setConstraints(lecturer_label, 3,1);
        GridPane.setConstraints(LecEmail, 3,2);
        GridPane.setConstraints(StudentCount, 3,3);
        GridPane.setConstraints(RoomCap, 3,4);
        GridPane.setConstraints(create_new_course,  0,7);

        grid.getChildren().addAll( );

        Time.getChildren().addAll(
                timeAM, timePM, lecturer_label, LecEmail, button,
                confirm, Room, Lecture, StudentCount, create_new_course,
                RoomCap,
                boxes[0], boxes[1], boxes[2], boxes[3], boxes[4],
                boxes[5], boxes[6], boxes[7], boxes[8], boxes[9]
        );

        layout.getChildren().addAll();

        Scene scene = new Scene (border, 600,300);
        window.setScene(scene);
        window.show();
    }

    private void ConfirmUpdate() {
        Course c = getCourseByName(Lecture.getValue());
        if (c == null) return;
        TimeSlot any_slot = null;
        for (TimeTable t : tables) {
            if (t.slot.course.name.equals(c.name)) {
                any_slot = t.slot;
            }
        }
        if (any_slot == null) throw new Error("no slot with the '" + c.name+"' course!");

        HashMap<Integer, TimeSlot> map = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            boolean is_set = boxes[i].isSelected();
            boolean found = false;

            for (TimeTable t : tables) {
                if (t.time == i) {
                    found = true;
                    break;
                }
            }

            if (!found && is_set) {
                String[] days = new String[]{
                        "monday",
                        "tuesday",
                        "wednesday",
                        "thursday",
                        "friday",
                };
                System.out.println("need to add a course " + c.name + " on " + days[i % 5] +  (i > 4 ? "PM" : "AM"));
                map.put(i, any_slot);
            }
        }

        for (Integer key: map.keySet()) {
            HashMap<Class<?>, Integer> refs = new HashMap<>();
            refs.put(TimeSlot.class, map.get(key).id);
            db.insert(new TimeTable(key), refs);
        }

        Lecturer = db.select(com.example.demo.Lecturer.class);
        rooms = db.select(Room.class);
        tables = db.select(TimeTable.class);
        courses = db.select(Course.class);
    }

    private void updateUI(Course c) {
        for (TimeTable booked: tables) {
            if (booked.slot.course.name.equals(c.name)) {
                boxes[booked.time].setSelected(true);
            }
        }
        ArrayList<String> strings = new ArrayList<>();
        for (Lecturer lecture: c.lecturers)
            strings.add(lecture.name);

        String lecturers = String.join(",", strings);
        lecturer_label.setText(lecturers);

        ArrayList<String> stringss = new ArrayList<>();
        for (Lecturer lecture: c.lecturers)
            stringss.add(lecture.email);
        String email = String.join(",", stringss);

        LecEmail.setText(email);

        StudentCount.setText(c.expected_student_count.toString());


        ArrayList<String> stringsss = new ArrayList<>();
        for (Room room: c.rooms)
            stringsss.add(room.capacity.toString());
        String Capped = String.join(",", stringsss);
        RoomCap.setText(Capped);
    }

    private Course getCourseByName(String name) {
        for (Course c: courses)
            if (c.name.equals(name)) return c;
        return null;
    }

    private void updateCourse(String str) {
        Course course = getCourseByName(str);
        if (course == null) return;
        updateUI(course);
    }

    private void checktime(String room){
        for (CheckBox box : boxes) {
            box.setSelected(false);
        }

        for (TimeTable booked: tables) {

            Room[] rooms = booked.slot.course.rooms;
            for (Room room20: rooms) {
                if (room20.name.equals(room)) {
                    boxes[booked.time].setSelected(true);
                }
            }
        }
    }
    private void closeProgram(){
        System.out.println("saved");
        window.close();
    }
    public static void main(String[] args) {
        launch();
    }
}
