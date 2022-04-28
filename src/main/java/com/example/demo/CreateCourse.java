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

import java.util.*;

public class CreateCourse {
    DataBase db;
    Course course = new Course();

    TextField teacher_name = new TextField();
    TextField teacher_email = new TextField();
    Integer teacher_index = 0;
    Label teacher_index_label = new Label();

    TextField student_count = new TextField();
    TextField course_name = new TextField();

    HashMap<Integer, Room>     updated_rooms = new HashMap<>();
    HashMap<Integer, Lecturer> updated_lecturers = new HashMap<>();
    boolean course_updated = false;

    ComboBox<Lecturer> lecturers = new ComboBox<>();
    ComboBox<Room> rooms = new ComboBox<>();
    CreateCourse(DataBase database) {
        db = database;
        course.lecturers = new Lecturer[0];
        course.rooms     = new Room[0];

        ArrayList<Lecturer> db_lecturers = db.select(Lecturer.class);
        lecturers.getItems().addAll(db_lecturers);

        ArrayList<Room> db_rooms = db.select(Room.class);
        rooms.getItems().addAll(db_rooms);
    }

    public Course display() {
        Ref<Boolean> is_valid = new Ref<>(false);
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Edit Lecture Info");
        window.setMinWidth(500);

        student_count.setTextFormatter(new TextFormatter<>(c -> {
            if (!c.getControlNewText().matches("\\d*"))
                return null;
            else
                return c;
        }
        ));

        Label label = new Label();
        label.setText("Here you can edit the Lecture info");
        Button close = new Button("Cancel");
        close.setOnAction(e -> window.close());

        Button confirmEdits = new Button("Confirm edits here");

        HashMap<Class<?>, Integer> refs = new HashMap<>();
        confirmEdits.setOnAction(e -> {
            checkUpdates();
            Integer room_list_id     = db.getNextArrayID(RoomList.class);
            Integer lecturer_list_id = db.getNextArrayID(LecturerList.class);

            if (course.rooms.length < 1) {
                showError("no rooms were added to the course");
                return;
            }
            if (course.lecturers.length < 1) {
                showError("no lecturers were added to the course");
                return;
            }
            if (course.name == null || course.name.length() == 0) {
                showError("course requires a name");
                return;
           }
            if (course.expected_student_count == null || course.expected_student_count == 0) {
                if (showWarning("this course expects no students")) return;
            }

            for (Room room: course.rooms) {
                String first = "the expected number of students (" +
                        course.expected_student_count + ") exceed the rooms capacity (" + room.capacity + ")";
                String second = "are you sure you want to book room " + room.name + "?";
                if (room.capacity < course.expected_student_count)
                    if (showWarning(first, second)) return;
            }

            for (Lecturer lecturer : course.lecturers) {
                refs.clear();
                refs.put(Lecturer.class, lecturer.id);
                db.insert(new LecturerList(lecturer_list_id), refs);
            }
            for (Room room: course.rooms) {
                refs.clear();
                refs.put(Room.class, room.id);
                db.insert(new RoomList(room_list_id), refs);
            }


            refs.clear();
            refs.put(LecturerList.class, lecturer_list_id);
            refs.put(RoomList.class, room_list_id);
            db.insert(course, refs);

            is_valid.set(true);
            window.close();
        });


        HBox student_count = new HBox(10, new Label("number of students: "), this.student_count);
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

        if (is_valid.get())
             return course;
        else return null;
    }

    void checkUpdates() {
        Integer num = 0;
        String number_string = student_count.getText();
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
        pane.setMinWidth(150);
        pane.setMinHeight(200);

        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        initialiseRoomGui(box);

        rooms.setPromptText("Add Room");
        rooms.setOnAction( e -> {
            ArrayList<Room> room_list = new ArrayList<>();
            Collections.addAll(room_list, course.rooms);

            room_list.add(rooms.getValue());
            course.rooms = new Room[room_list.size()];

            for (int i = 0; i < room_list.size(); i++) course.rooms[i] = room_list.get(i);

            initialiseRoomGui(box);
        });
        pane.setContent(box);
        VBox layout = new VBox(10);
        layout.getChildren().addAll(new Label("Rooms"), rooms, pane);
        return layout;
    }

    private void initialiseLecturerGui(VBox box) {
        box.getChildren().clear();
        for (Lecturer teacher: course.lecturers) {
            Label label = new Label(teacher.name + " (" + teacher.email + ")");
            Button b = new Button("Remove");
            b.setOnAction(e->{
                System.out.println("removing lecturer " + teacher.id + " from list");
                ArrayList<Lecturer> room_list = new ArrayList<>();
                Collections.addAll(room_list, course.lecturers);
                room_list.remove(teacher);

                course.lecturers = new Lecturer[room_list.size()];
                for (int i = 0; i < room_list.size(); i++) course.lecturers[i] = room_list.get(i);
                initialiseLecturerGui(box);
            });

            HBox hbox = new HBox(5);
            hbox.setAlignment(Pos.CENTER);
            hbox.getChildren().addAll(label, b);

            box.getChildren().add(hbox);
        }
    }

    private void initialiseRoomGui(VBox box) {
        box.getChildren().clear();
        for (Room room: course.rooms) {
            Label label = new Label(room.name + " (" + room.capacity + ")");
            Button b = new Button("Remove");
            b.setOnAction(e->{
                System.out.println("removing room " + room.id + " from list");
                ArrayList<Room> room_list = new ArrayList<>();
                Collections.addAll(room_list, course.rooms);
                room_list.remove(room);

                course.rooms = new Room[room_list.size()];
                for (int i = 0; i < room_list.size(); i++) course.rooms[i] = room_list.get(i);
                initialiseRoomGui(box);
            });

            HBox hbox = new HBox(5);
            hbox.setAlignment(Pos.CENTER);
            hbox.getChildren().addAll(label, b);

            box.getChildren().add(hbox);
        }
    }

    Node createTeachersArea() {
        ScrollPane pane = new ScrollPane();
        pane.setMinWidth(100);
        ArrayList<Button> buttons = new ArrayList<>();

        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        initialiseLecturerGui(box);

        lecturers.setPromptText("Add Lecturer");
        lecturers.setOnAction( e -> {
            ArrayList<Lecturer> lecturer_list = new ArrayList<>();
            Collections.addAll(lecturer_list, course.lecturers);

            lecturer_list.add(lecturers.getValue());
            course.lecturers = new Lecturer[lecturer_list.size()];

            for (int i = 0; i < lecturer_list.size(); i++) course.lecturers[i] = lecturer_list.get(i);

            initialiseLecturerGui(box);
        });
        pane.setContent(box);
        VBox layout = new VBox(10);
        layout.getChildren().addAll(new Label("Lecturers"), lecturers, pane);
        return layout;
    }
    public void showError(String... strings) {
        Stage window = new Stage();
        final Ref<Boolean> should_cancel = new Ref<>(false);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Error!");
        window.setMinWidth(500);
        VBox layout = new VBox(15, new Label("Error!"));

        for (String s: strings) {
            layout.getChildren().add(new Label(s));
        }

        Button confirm = new Button("ok");
        confirm.setOnAction(e -> window.close());

        layout.getChildren().addAll(confirm);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }

    public boolean showWarning(String... strings) {
        Stage window = new Stage();
        final Ref<Boolean> should_cancel = new Ref<>(false);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Warning!");
        window.setMinWidth(500);
        VBox layout = new VBox(15, new Label("Warning!"));

        for (String s: strings) {
            layout.getChildren().add(new Label(s));
        }

        Button close = new Button("Cancel");
        close.setOnAction(e -> {
            should_cancel.set(true);
            window.close();
        });

        Button confirm = new Button("Confirm");
        confirm.setOnAction(e -> window.close());

        layout.getChildren().addAll(confirm, close);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        return should_cancel.get();
    }
}
