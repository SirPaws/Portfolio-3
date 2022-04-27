package com.example.demo;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EditLecture {
    public static void Display() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Edit Lecture Info");
        window.setMinWidth(500);

        Label label = new Label();
        label.setText("Here you can edit the Lecture info");
        Button close = new Button("Confirm Edits");
        close.setOnAction(e -> window.close());

        Button confirmEdits = new Button("Confirm edits here");
        confirmEdits.setOnAction(e -> EditLecture.SendToDB());

        VBox layout = new VBox(15);
        layout.getChildren().addAll(label, confirmEdits, close);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

    }

    public static void SendToDB() {

    }

    public static void Confirm() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Confirm overbooking");
        window.setMinWidth(500);

        Label label = new Label();
        label.setText("You are about to overbook a room");

        Button close = new Button("Undo Edits");
        close.setOnAction(e -> window.close());

        Button confirm = new Button("Confirm Edits");
        confirm.setOnAction(e -> window.close());



        VBox layout = new VBox(15);
        layout.getChildren().addAll(label, confirm, close);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

    }
}
