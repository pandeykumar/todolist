#Tasks schema

# --- !Ups


CREATE TABLE task (
	id bigint primary key auto_increment,
	label varchar(255)
);

# --- !Downs

DROP TABLE task;


