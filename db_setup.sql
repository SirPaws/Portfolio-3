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
