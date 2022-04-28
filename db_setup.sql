/*

this code is the sqlite code we use to create and initialse our database
please note however that this was autogenerated by our dabase backend

see the comment in the bottom of this document for the java equivalent
*/
create table if not exists Lecturer(
    lecturer_id integer primary key not null,
    lecturer_name text not null,
    lecturer_email text
)
create table if not exists LecturerList(
    lecturerlist_id integer not null ,
    lecturerlist_value_key Integer,
    foreign key (lecturerlist_value_key) references Lecturer (lecturerlist_value_key)
)
create table if not exists Room(
    room_id integer primary key not null,
    room_name text not null,
    room_capacity integer not null
)
create table if not exists RoomList(
    roomlist_id integer not null ,
    roomlist_value_key Integer,
    foreign key (roomlist_value_key) references Room (roomlist_value_key)
)
create table if not exists Course(
    course_id integer primary key,
    course_name text not null,
    course_expected_student_count integer not null,
    course_rooms_key Integer not null,
    course_lecturers_key Integer not null,
    foreign key (course_lecturers_key) references RoomList (course_lecturers_key),
    foreign key (course_lecturers_key) references LecturerList (course_lecturers_key)
)
create table if not exists TimeSlot(
    timeslot_id integer primary key,
    timeslot_course_key Integer not null,
    foreign key (timeslot_course_key) references Course (timeslot_course_key)
)
create table if not exists TimeTable(
    timetable_time integer,
    timetable_slot_key Integer not null,
    foreign key (timetable_slot_key) references TimeSlot (timetable_slot_key)
)
insert into Lecturer(lecturer_id, lecturer_name, lecturer_email) values (1, 'Geralt of Rivia', null)
insert into Lecturer(lecturer_id, lecturer_name, lecturer_email) values (2, 'Ulfric Stormcloak', null)
insert into LecturerList(lecturerlist_id, lecturerlist_value_key) values (0, 1)
insert into LecturerList(lecturerlist_id, lecturerlist_value_key) values (0, 2)
insert into Room(room_id, room_name, room_capacity) values (1, '4.206.9', 420)
insert into Room(room_id, room_name, room_capacity) values (2, '10.42.1', 420)
insert into RoomList(roomlist_id, roomlist_value_key) values (0, 1)
insert into RoomList(roomlist_id, roomlist_value_key) values (0, 2)
insert into Course(course_id, course_name, course_expected_student_count, course_rooms_key, course_lecturers_key) values (1, 'SD', 120, 0, 0)
insert into TimeSlot(timeslot_id, timeslot_course_key) values (1, 1)
insert into TimeTable(timetable_time, timetable_slot_key) values (1, 1)


/* This is the java equivalent of the code above
```java
DataBaseTable<Lecturer> lecturer_table = new DataBaseTable<>(Lecturer.class);
DataBaseTable<LecturerList> lecturer_list_table = new DataBaseTable<>(LecturerList.class);

DataBaseTable<Room> room_table = new DataBaseTable<>(Room.class);
DataBaseTable<RoomList> room_list_table = new DataBaseTable<>(RoomList.class);

DataBaseTable<TimeTable> time_table_table = new DataBaseTable<>(TimeTable.class);
DataBaseTable<TimeSlot> time_slot_table = new DataBaseTable<>(TimeSlot.class);
DataBaseTable<Course> course_table = new DataBaseTable<>(Course.class);

try {
    println(lecturer_table.createTable());
    println(lecturer_list_table.createTable());
    println(room_table.createTable());
    println(room_list_table.createTable());
    println(course_table.createTable());
    println(time_slot_table.createTable());
    println(time_table_table.createTable());
    println(lecturer_table.insert(new Lecturer(1, "Geralt of Rivia")));
    println(lecturer_table.insert(new Lecturer(2, "Ulfric Stormcloak")));

    HashMap<Class<?>, Integer> refs = new HashMap<>();
    refs.put(Lecturer.class, 1);
    println(lecturer_list_table.insert(new LecturerList(0), refs));
    refs.put(Lecturer.class, 2);
    println(lecturer_list_table.insert(new LecturerList(0), refs));
    refs.clear();

    println(room_table.insert(new Room(1, "4.206.9", 420), null));
    println(room_table.insert(new Room(2, "10.42.1", 420), null));
    refs.put(Room.class, 1);
    println(room_list_table.insert(new RoomList(0), refs));
    refs.put(Room.class, 2);
    println(room_list_table.insert(new RoomList(0), refs));
    refs.clear();

    refs.put(RoomList.class, 0);
    refs.put(LecturerList.class, 0);
    println(course_table.insert(new Course(1, "SD", 120), refs));
    refs.clear();

    refs.put(Course.class, 1);
    println(time_slot_table.insert(new TimeSlot(1), refs));
    refs.clear();

    refs.put(TimeSlot.class, 1);
    println(time_table_table.insert(new TimeTable(1), refs));

} catch(InvalidClassException ignored) {
}
```

if you wanted to do this with an actual database it would be
like this instead
```java
DataBase db = new DataBase();
db.dropNewTables(true); // optional if you want the system to drop the old table when a new one is created

db.addTable(lecturer_list_table);
db.addTable(lecturer_table);
db.addTable(room_table);
db.addTable(room_list_table);
db.addTable(course_table);
db.addTable(time_slot_table);
db.addTable(time_table_table);

db.insert(new Lecturer(1, "Geralt of Rivia"), null);
db.insert(new Lecturer(2, "Ulfric Stormcloak"), null);
....
```
*/



