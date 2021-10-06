create database elefteria;
use elefteria;

create table image(
id int(20) primary key not null auto_increment,
bytes mediumblob default null,
file_name varchar(20) default null);

create table user_info(
id int(20) primary key not null auto_increment,
first_name varchar(50) default null,
last_name varchar(50) default null,
status varchar(256) default null,
avatar_id int(20) default null,
amount_of_followers int default 0,
amount_of_follows int default 0,
created_date date,
constraint fk_user_image foreign key (avatar_id)
references image(id));

create table user(
id int(20) primary key not null auto_increment,
username varchar(50) unique not null,
password varchar(128) not null,
email varchar(50) unique not null,
user_info_id int(20) default null,
is_account_non_expired bool not null,
is_account_non_locked bool not null,
is_credentials_non_expired bool not null,
is_enabled bool not null,
constraint fk_user_info foreign key (user_info_id) 
references user_info(id));

create table role(
id int(20) primary key not null auto_increment,
name varchar(50) unique not null);

create table users_roles(
id int(20) primary key not null auto_increment,
user_id int(20) not null,
role_id int(20) not null,
constraint fk_user foreign key (user_id) references user (id),
constraint fk_role foreign key (role_id) references role (id),
constraint uq_users_roles unique(user_id, role_id));

insert into user_info(first_name, last_name, status, avatar_id, created_date) values
('Monkey', 'Obama', 'I am very bad monkey, I love to eat bananas and kill people', null, now()),
('Mary', 'White', 'I am Mary, I am first admin on this site!', null, now());

insert into user(username, password, email, user_info_id,
is_account_non_expired, is_account_non_locked, is_credentials_non_expired, is_enabled) values
('obamamonkey', '$2a$10$wYP2HIbnNmoh4XuywrFuWub7jZnY0mG/1X1XGoPIFllmnxAwAoDye', 'obama.monkey@gmail.com', 1, true, true, true, true),
('mary', '$2a$10$8fAN.Xssl3v5LzITnd346u93dTbM6g3w2JvaiJ7HrVDVdx6BONluW', 'mary.white@yahoo.com', 2, true, true, true, true);

insert into role(name) values ('USER'), ('ADMIN'); 
insert into users_roles(user_id, role_id) values (1, 1), (2, 2);


create table post(
id int(20) primary key not null auto_increment,
title varchar(50) default null,
user_id int(20) not null,
`text` text default null,
amount_of_likes int default 0,
created_date datetime not null,
constraint fk_user_post foreign key (user_id)
references user (id));


alter table image add post_id int(20) default null;
alter table image add constraint fk_post foreign key
(post_id) references post (id);


create table post_like(
id int(20) primary key not null auto_increment,
post_id int(20) not null,
username varchar(50) not null,
constraint fk_post_like foreign key (post_id)
references post (id));

create table comment(
id int(20) primary key not null auto_increment,
post_id int(20) not null,
user_id int(20) not null,
`text` text not null,
created_date datetime,
constraint fk_user_comment foreign key
(user_id) references user (id),
constraint fk_post_comment foreign key
(post_id) references post (id));

create table following(
id int(20) primary key not null auto_increment,
follower_user_id int(20) not null,
followed_user_id int(20) not null,
constraint fk_follower_user foreign key (follower_user_id)
references user (id),
constraint fk_followed_user foreign key (followed_user_id)
references user (id));

create table registration_change_password_token(
id int(20) primary key not null auto_increment,
token varchar(40) not null,
created_at datetime not null,
expires_at datetime not null,
user_id int(20) not null,
change_password bool default false,
constraint fk_user_registration_token foreign key (user_id)
references user (id));

create table chat_message(
id int(20) primary key not null auto_increment,
created_date datetime not null,
user_from_id int(20) not null,
image_id int(20) default null,
message text default null,
constraint fk_user_from foreign key (user_from_id)
references user (id),
constraint fk_image_chat_message foreign key (image_id) 
references image (id));

#-----------POLL------------#

create table poll(
id int(20) primary key not null auto_increment,
question varchar(255) default null,
post_id int(20) default null,
constraint fk_post_poll foreign key (post_id)
references post(id));


create table poll_option(
id int(20) primary key not null auto_increment,
`option` varchar(128) not null,
poll_id int(20) default null,
number int(3) default 1,
constraint fk_poll_poll_option foreign key (poll_id)
references poll(id));

create table poll_option_user(
id int(20) primary key not null auto_increment,
user_id int(20) not null,
poll_option_id int(20) not null,
constraint fk_user_poll_option foreign key (user_id)
references user(id),
constraint fk_poll_option_option_user foreign key (poll_option_id)
references poll_option(id));

alter table poll_option_user add constraint uq_option_user unique(user_id, poll_option_id);

create table notification(
id int(20) primary key not null auto_increment,
user_owner_id int(20) not null,
user_actor_id int(20),
created_date datetime, 
`read` bool default false,
type enum('LIKE', 'COMMENT', 'SUBSCRIPTION'),
post_id int(20) default null,
comment_id int(20) default null,
constraint fk_user_owner_notification foreign key 
(user_owner_id) references user(id),
constraint fk_user_actor_notification foreign key 
(user_actor_id) references user(id),
constraint fk_post_notification foreign key 
(post_id) references post(id),
constraint fk_comment_notification foreign key 
(comment_id) references comment(id));





