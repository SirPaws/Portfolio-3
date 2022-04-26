import Database.Annotations.*;
import Database.DataBase;
import Database.DataBaseTable;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        DataBaseTable<Lecturer> lecturer_table = new DataBaseTable<>(Lecturer.class);
        DataBaseTable<Room> room_table = new DataBaseTable<>(Room.class);
        DataBaseTable<Slot> slot_table = new DataBaseTable<>(Slot.class);
        // DataBaseTable<RoomList> room_list_table = new DataBaseTable<>(RoomList.class);

        HashMap<Class<?>, Integer> room_list_map = new HashMap<>();
        room_list_map.put(Room.class,     1);

        HashMap<Class<?>, Integer> slot_ref_map = new HashMap<>();
        // slot_ref_map.put(RoomList.class, 0);
        slot_ref_map.put(Room.class, 1);
        slot_ref_map.put(Lecturer.class, 1);
        try {

            System.out.println(lecturer_table.dropTable() + ";");
            System.out.println(lecturer_table.createTable() + ";");
            System.out.println(lecturer_table.insert(new Lecturer(1, "Geralt of Rivia")) + ";");

            System.out.println();
            System.out.println(room_table.dropTable() + ";");
            System.out.println(room_table.createTable() + ";");
            System.out.println(room_table.insert(new Room(1, "42.10.1", 50)) + ";");

            /*
            System.out.println();
            System.out.println(room_list_table.dropTable() + ";");
            System.out.println(room_list_table.createTable() + ";");
            System.out.println(room_list_table.insert(new RoomList(0), room_list_map) + ";");
            */

            System.out.println();
            System.out.println(slot_table.dropTable() + ";");
            System.out.println(slot_table.createTable() + ";");
            System.out.println(slot_table.insert(new Slot(0), slot_ref_map) + ";");
        } catch(InvalidClassException ignored) {
        }

        DataBase db = new DataBase();
        db.dropNewTables(true);
        try {
            db.addTable(lecturer_table);
            db.insert(new Lecturer(1, "Geralt of Rivia"), null);

            db.addTable(room_table);
            db.insert(new Room(1, "42.10.1", 50), null);

            db.addTable(slot_table);
            db.insert(new Slot(0), slot_ref_map);

            ArrayList<Slot> slots = db.select(Slot.class);
            for (Slot slot: slots) {

                System.out.println("Slot " + slot.id);
                /*
                for (RoomList room: slot.rooms) {
                    System.out.println(slot.room.id + ": " + slot.room.name + " (" + slot.room.capacity + ")");
                }
                */
                System.out.println(slot.room.id + ": " + slot.room.name + " (" + slot.room.capacity + ")");
                System.out.println(slot.lecturer.id + ": " + slot.lecturer.name + "(" + slot.lecturer.email + ")");
            }

            // ArrayList<String> names = db.selectField("name", Lecturer.class);
        } catch(InvalidClassException ignored) {
        }

    }
}


/*
create table Lecturer (
    lecturer_id integer primary key autoincrement not null,
    name text not null,
    email text
);

create table Rooms (
    room_id integer primary key autoincrement not null,
    name text not null,
    capacity int not null
);

create table TimeSlotValue (
    timeslot_value_id integer primary key autoincrement not null,
    room_key int,
    lecturer_key int,
    foreign key(room_key) references Rooms(room_key),
    foreign key(lecturer_key) references Lecturer(lecturer_key)
);
*/

class Room {
    public Room() {}
    public Room(Integer _id, String _name, Integer _capacity) {
        id       = _id;
        name     = _name;
        capacity = _capacity;
    }
    @NotNull
    @PrimaryKey
    Integer id;

    @NotNull
    String name;

    @NotNull
    Integer capacity;
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
    @NotNull
    @PrimaryKey
    Integer id;

    @NotNull
    String name;

    @Optional
    String email;
}

class RoomList {
    public RoomList() {}
    public RoomList(Integer i) { id = i; }
    @NotNull @Key
    Integer id;
    @Value
    Room    value;
}

class Slot {
    public Slot() { }
    public Slot(Integer _id) { id = _id; }

    @NotNull @PrimaryKey
    Integer id;

    @Reference(Room.class)
    Room room;
    // @Array(RoomList.class)
    // Room[] rooms;

    @Reference(Lecturer.class)
    Lecturer lecturer;
}

