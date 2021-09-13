create table messageuser (
usid int primary key,
phonenumber int not null unique,
displayname varchar(12)
);

create table messagegroup(
groupid int primary key,
groupname varchar(24) not null
);

create table membership (
groupid int references messagegroup(groupid),
usid int references messageuser(usid),
primary key (groupid, usid)
);

create table message(
messageid int primary key,
usid int references messageuser(usid),
groupid int references messagegroup(groupid),
messagetext varchar(512) not null
);
