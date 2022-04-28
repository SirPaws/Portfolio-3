package com.example.demo;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EditTeacher {
    Lecturer old_teacher;
    Lecturer new_teacher;

    TextField teacher_name = new TextField();
    TextField teacher_email = new TextField();

    EditTeacher(Lecturer r) {
        old_teacher = r;
        new_teacher = new Lecturer(r.id, r.name, r.email);
    }

    Lecturer display() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Edit Room Info");
        window.setMinWidth(500);

        teacher_name.setText(new_teacher.name);
        teacher_email.setText(new_teacher.email == null ? "" : new_teacher.email);

        VBox fields_layout = new VBox(15);
        fields_layout.setAlignment(Pos.CENTER);
        fields_layout.getChildren().addAll(new Label("Name: "), teacher_name, new Label("email"), teacher_email);

        Button close = new Button("Cancel");
        close.setOnAction(e -> {
            new_teacher = old_teacher;
            window.close();
        });

        Button confirm_edits = new Button("Confirm edits here");
        confirm_edits.setOnAction(e -> {
            updateTeacher();
            window.close();
        });
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.BOTTOM_CENTER);
        layout.getChildren().addAll(confirm_edits, close);


        Scene scene = new Scene(new VBox(fields_layout, layout));
        window.setScene(scene);
        window.showAndWait();

        return new_teacher;
    }


    void updateTeacher() {
        String email = teacher_email.getText();
        String name  = teacher_name.getText();
        if (!email.equals(new_teacher.email == null ? "" : new_teacher.email)) new_teacher.email = email;
        if (!new_teacher.name.equals(name))   new_teacher.name = name;

        teacher_email.setText(new_teacher.email == null ? "" : new_teacher.email);
        teacher_name.setText(new_teacher.name);
    }
}
