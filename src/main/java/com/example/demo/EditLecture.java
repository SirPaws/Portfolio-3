package com.example.demo;

import Database.DataBase;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class EditLecture {
    DataBase db;
    Course course;
    EditLecture(DataBase database, Course c) {
        db = database;
        course = c;
    }

    TextField Laerer = new TextField();
    TextField laerermail = new TextField();
    Integer teacher_index = 0;
    Label teacher_index_label = new Label();

    TextField Elevnmr = new TextField();
    TextField course_name = new TextField();

    HashMap<Integer, Room>     updated_rooms = new HashMap<>();
    HashMap<Integer, Lecturer> updated_lecturers = new HashMap<>();
    boolean course_updated = false;

    public void Display() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Edit Lecture Info");
        window.setMinWidth(500);

        Elevnmr.setTextFormatter(new TextFormatter<>(c -> {
            if (!c.getControlNewText().matches("\\d*"))
                return null;
            else
                return c;
        }
        ));

        course_name.setText(course.name);
        Elevnmr.setText(course.expected_student_count.toString());

        if (course.lecturers.length > teacher_index) {
            String email = course.lecturers[teacher_index].email;
            Laerer.setText(course.lecturers[teacher_index].name);
            laerermail.setText(email == null ? "" : email);
        }

        Label label = new Label();
        label.setText("Here you can edit the Lecture info");
        Button close = new Button("Cancel");
        close.setOnAction(e -> window.close());

        Button confirmEdits = new Button("Confirm edits here");

        HashMap<Class<?>, Integer> map = new HashMap<>();
        Integer room_list_id     = db.getArrayID(course, RoomList.class);
        Integer lecturer_list_id = db.getArrayID(course, LecturerList.class);
        map.put(RoomList.class, room_list_id);
        map.put(LecturerList.class, lecturer_list_id);
        confirmEdits.setOnAction(e -> {
            checkUpdates();

            for (Room r: updated_rooms.values()) {
                if (r.capacity < course.expected_student_count) {
                    if (warnOverbook(r, course.expected_student_count)) {
                        window.close(); return;
                    }
                }
                db.update(r, null);
            }
            for (Lecturer l: updated_lecturers.values()) {
                db.update(l, null);
            }
            if (course_updated) {
                db.update(course, map);
            }
            window.close();
        });


        HBox student_count = new HBox(10, new Label("number of students: "), Elevnmr);
        student_count.setAlignment(Pos.CENTER);

        HBox course_name_box = new HBox(10, new Label("name: "), course_name);
        course_name_box.setAlignment(Pos.CENTER);

        VBox course_box = new VBox(15);
        course_box.getChildren().addAll(new Label("Course info"), course_name_box, student_count);
        course_box.setAlignment(Pos.CENTER);

        HBox horis_layout = new HBox(15);
        horis_layout.getChildren().addAll(createTeachersArea(), createRoomsArea());
        horis_layout.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15);
        layout.getChildren().addAll(horis_layout, course_box, confirmEdits, close);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }

    void checkUpdates() {
        Integer num = 0;
        String number_string = Elevnmr.getText();
        if (number_string.length() > 0) num = Integer.valueOf(number_string);

        if (num != 0 && !num.equals(course.expected_student_count)) {
            course.expected_student_count = num;
            course_updated = true;
        }

        String str = course_name.getText();
        if (str.length() > 0 && !str.equals(course.name)) {
            course.name = str;
            course_updated = true;
        }
    }

    Node createRoomsArea() {
        ScrollPane pane = new ScrollPane();

        VBox box = new VBox(15, new Label("Rooms"));
        box.setAlignment(Pos.CENTER);
        ArrayList<Button> buttons = new ArrayList<>();

        for (Room room: course.rooms) {
            Button b = new Button(room.name + " (" + room.capacity + ")");
            b.setOnAction( e -> {
                System.out.println("pressed room with ID: " + room.id);
                Room new_room = new EditRoom(room).display();
                if (!Objects.equals(new_room.capacity, room.capacity) ||
                    !Objects.equals(new_room.name, room.name))
                {
                    room.name = new_room.name;
                    room.capacity = new_room.capacity;
                    b.setText(room.name + " (" + room.capacity + ")");
                    updated_rooms.put(room.id, room);
                }
            });
            box.getChildren().add(b);
        }

        pane.setContent(box);
        return pane;
    }

    Node createTeachersArea() {
        ScrollPane pane = new ScrollPane();

        VBox box = new VBox(15, new Label("Lecturers"));
        box.setAlignment(Pos.CENTER);

        for (Lecturer l: course.lecturers) {
            Button b = new Button(l.name + " (" + l.email + ")");
            b.setOnAction( e -> {
                System.out.println("pressed lecturer with ID: " + l.id);
                Lecturer new_teacher = new EditTeacher(l).display();
                if (!Objects.equals(new_teacher.email, l.email) ||
                    !Objects.equals(new_teacher.name, l.name))
                {
                    l.name = new_teacher.name;
                    l.email = new_teacher.email;
                    b.setText(l.name + " (" + l.email + ")");
                    updated_lecturers.put(l.id, l);
                }
            });
            box.getChildren().add(b);
        }

        pane.setContent(box);
        return pane;
    }

    public boolean warnOverbook(Room r, Integer expected_student_count) {
        Stage window = new Stage();
        final Ref<Boolean> should_cancel = new Ref<>(false);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Confirm overbooking");
        window.setMinWidth(500);

        Label warning_label = new Label("Warning!");
        Label line1 = new Label("the expected number of students (" + expected_student_count + ") exceed the rooms capacity (" + r.capacity + ")");
        Label line2 = new Label("are you sure you want to book room " + r.name + "?");

        Button close = new Button("Undo Edits");
        close.setOnAction(e -> {
            should_cancel.set(true);
            window.close();
        });

        Button confirm = new Button("Confirm Edits");
        confirm.setOnAction(e -> window.close());



        VBox layout = new VBox(15);
        layout.getChildren().addAll(warning_label, line1, line2, confirm, close);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        return should_cancel.get();
    }
}

class Ref<T> {
    private T value;
    public Ref(T v) { value = v; };

    void set(T v) { value = v; };
    T get() { return value; };
}