package com.example.demo;

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
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;

public class HelloApplication extends Application {
    Rooms room10;
    Button button;
    Button confirm;
    Button closeprogrm;
    Stage window;
    CheckBox[] boxes = new CheckBox[10];
    ArrayList<TimeTable> tables = new ArrayList<>();
    ArrayList<Room> rooms = new ArrayList<>();
    ArrayList<Lecturer> Lecturer = new ArrayList<>();

    /*
    TimeTable booked = new TimeTable(6, new TimeSlot(15,
            new Course(4, "name",
                    new Room[]{new Room( 11, "SD", 13)},
                    new Lecturer[]{new Lecturer(12,"ehj", "@mail"),
                            new Lecturer(12,"nejj", "@mail")})));
*/
    @Override
    public void start(Stage primaryStage) throws Exception {


        Lecturer.add(new Lecturer(12,"ehj", "@mail"));
        rooms.add(new Room( 11, "10.47", 13));

                tables.add(new TimeTable( (0), new TimeSlot(15,
                new Course(4, "SD",
                        new Room[]{rooms.get(0)},
                        new Lecturer[]{Lecturer.get(0)}))));
        tables.add(new TimeTable( (6), new TimeSlot(15,
                new Course(4, "SD",
                        new Room[]{new Room( 11, "10.47", 13)},
                        new Lecturer[]{new Lecturer(12,"ehj", "@mail"),
                                new Lecturer(12,"nejj", "@mail")}))));
        tables.add(new TimeTable( (3), new TimeSlot(15,
                new Course(4, "IDS",
                        new Room[]{new Room( 11, "10.47", 10)},
                        new Lecturer[]{new Lecturer(12,"ehj", "@mail"),
                                new Lecturer(12,"nejj", "@mail")}))));
        window = primaryStage;
        window.setTitle("Hello!");
        window.setOnCloseRequest(e -> {
            e.consume();
            closeProgram();

        });

        Course klasse = tables.get(0).slot.course;

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

        ChoiceBox<String> Lecture = new ChoiceBox<>();
        Lecture.getItems().addAll("Select Course","SD", "IDS");
        Lecture.setValue("Select Course");



        ChoiceBox<String> Room = new ChoiceBox<>();
        Room.getItems().addAll("Select Room","10.47", "10.48", "9.47");
        Room.setValue("Select Room");
        Room.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue)-> checktime(newValue));

        button = new Button();
        button.setText("Edit Information");
        button.setOnAction(e -> EditLecture.Display());
        confirm = new Button();
        confirm.setText("Confirm Booking");
        confirm.setOnAction(e -> ConfirmUpdate());
        closeprogrm = new Button();


        ArrayList<String> strings = new ArrayList<>();
        for (Lecturer lecture: klasse.lecturers)
            strings.add(lecture.name);

        String lecturers = String.join(",", strings);

        Label Lecturer = new Label(lecturers);

        ArrayList<String> stringss = new ArrayList<>();
        for (Lecturer lecture: klasse.lecturers)
            stringss.add(lecture.email);
        String email = String.join(",", stringss);

        Label LecEmail = new Label(email);

        Label StudentCount= new Label(klasse.expected_student_count.toString());


        ArrayList<String> stringsss = new ArrayList<>();
        for (Room room: klasse.rooms)
            stringsss.add(room.capacity.toString());
        String Capped = String.join(",", stringsss);
        Label RoomCap = new Label(Capped);


        Label timePM = new Label("PM");
        Label timeAM = new Label("AM");

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

        GridPane.setConstraints(button, 4,3);
        GridPane.setConstraints(confirm, 4,4);
        GridPane.setConstraints(Room, 4,2);
        GridPane.setConstraints(Lecture, 4,1);

        GridPane.setConstraints(Lecturer, 3,1);
        GridPane.setConstraints(LecEmail, 3,2);
        GridPane.setConstraints(StudentCount, 3,3);
        GridPane.setConstraints(RoomCap, 3,4);

        grid.getChildren().addAll( );

        Time.getChildren().addAll(timeAM,timePM,Lecturer, LecEmail,button, confirm, Room, Lecture, StudentCount, RoomCap,boxes[0], boxes[1], boxes[2], boxes[3], boxes[4], boxes[5], boxes[6], boxes[7], boxes[8], boxes[9]);





        layout.getChildren().addAll();

        Scene scene = new Scene (border, 500,300);

        window.setScene(scene);
        window.show();
    }
    private void ConfirmUpdate(){
        //if(tables.get(0).slot.course.expected_student_count < tables.get(0).);


    }

    private void checktime(String room){
        for (int i = 0; i < boxes.length; i++) {
            boxes[i].setSelected(false);
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
