
drop table if exists Lecturer;
create table Lecturer ( 
    lecturer_id integer primary key autoincrement not null,
    name text not null,
    email text
);

drop table if exists Rooms;
create table Rooms ( 
    room_id integer primary key autoincrement not null,
    name text not null,
    capacity int not null
);

drop table if exists TimeSlotValue;
create table TimeSlotValue ( 
    timeslot_value_id integer primary key autoincrement not null,
    room_key int,
    lecturer_key int,
    foreign key(room_key) references Rooms(room_key),
    foreign key(lecturer_key) references Lecturer(lecturer_key)
);

insert into Lecturer (name) values ('Garry');
insert into Rooms (name, capacity) values ('0.10.1', 50);
insert into TimeSlotValue (room_key, lecturer_key) values ( (select room_id from Rooms), (select lecturer_id from Lecturer));

select * from (
    select * from Rooms inner join Lecturer where room_id = (select room_key from TimeSlotValue)
);
