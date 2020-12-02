create table employee (id bigint not null auto_increment primary key, name varchar(255), department_id bigint);
create table department (id bigint not null auto_increment primary key, name varchar(255));
create table client (id bigint not null auto_increment primary key, name varchar(255));
create table project (id bigint not null auto_increment primary key, name varchar(255), client_id bigint);
create table work_log (id bigint not null auto_increment primary key, entry_date date, hours int, rate numeric(18,2), project_id bigint, employee_id bigint);