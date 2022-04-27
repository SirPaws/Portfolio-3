package com.example.demo;

import javafx.scene.control.CheckBox;

import java.security.Timestamp;
import java.util.Arrays;

public class Rooms {
    public Float RoomID;
    public Float RoomCapacity;

    public Boolean Occupied;

    public static void ChekRoom(CheckBox[] boxe){
        Boolean[] TimeStamp = new Boolean[10];
        for (int i = 0; i < TimeStamp.length ; i++) {
            if(TimeStamp[i]){
                boxe[i].setSelected(true);
            }
        }
    }

}
