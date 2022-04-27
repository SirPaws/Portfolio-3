package com.example.demo;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class HelloController {
    public static void Display(){
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Edit Lecture Info");
        window.setMinWidth(300);

        Label label = new Label();
        label.setText("Here you can edit the Lecture info");
        Button close = new Button("Confirm Edits");
        close.setOnAction(e -> window.close());

        VBox layout = new VBox(15);
        layout.getChildren().addAll(label, close);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

    }
}
