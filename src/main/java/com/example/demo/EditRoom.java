package com.example.demo;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Box;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EditRoom {
    Room old_room;
    Room new_room;

    TextField room_name = new TextField();
    TextField room_capacity = new TextField();

    EditRoom(Room r) {
        old_room = r;
        new_room = new Room(r.id, r.name, r.capacity);
    }

    Room display() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Edit Room Info");
        window.setMinWidth(500);

        room_name.setTextFormatter(new TextFormatter<>(c -> {
            if (!c.getControlNewText().matches("[\\d.]*"))
                return null;
            else
                return c;
        }
        ));

        room_capacity.setTextFormatter(new TextFormatter<>(c -> {
            if (!c.getControlNewText().matches("\\d*"))
                return null;
            else
                return c;
        }
        ));

        room_name.setText(old_room.name);
        room_capacity.setText(old_room.capacity.toString());

        VBox fields_layout = new VBox(15);
        fields_layout.setAlignment(Pos.CENTER);
        fields_layout.getChildren().addAll(new Label("Room Number: "), room_name, new Label("Capacity"), room_capacity);

        Button close = new Button("Cancel");
        close.setOnAction(e -> {
            new_room = old_room;
            window.close();
        });

        Button confirm_edits = new Button("Confirm edits here");
        confirm_edits.setOnAction(e -> {
            updateRoom();
            window.close();
        });
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.BOTTOM_CENTER);
        layout.getChildren().addAll(confirm_edits, close);


        Scene scene = new Scene(new VBox(fields_layout, layout));
        window.setScene(scene);
        window.showAndWait();

        return new_room;
    }

    void updateRoom() {
        String capacity_text = room_capacity.getText();
        Integer capacity = Integer.valueOf(capacity_text.equals("") ? "0" : capacity_text);

        String name  = room_name.getText();

        if (!capacity.equals(new_room.capacity)) new_room.capacity = capacity;
        if (!new_room.name.equals(name))   new_room.name = name;

        room_capacity.setText(new_room.capacity.toString());
        room_name.setText(new_room.name);
    }
}
