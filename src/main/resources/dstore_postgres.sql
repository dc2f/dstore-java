/*
drop table Branch;
drop table CommitHistory;
drop table Commit;
drop table Node cascade;
drop table NodeChildren;
drop table Properties;
*/

create table Properties (
	id		INT8 PRIMARY KEY,
	properties	JSON NOT NULL
);

create table Node (
	id		INT8 PRIMARY KEY,
	childrenId	INT8, /* reference to node children id */
	propertiesId	INT8 NOT NULL REFERENCES Properties(id)
);

create table NodeChildren (
	id		INT8,
	childId		INT8 REFERENCES Node(id),
	PRIMARY KEY(id, childId)
);

create table Commit (
	id		INT8 PRIMARY KEY,
	rootNodeId	INT8 REFERENCES Tree(id),
	message		VARCHAR
);

create table CommitHistory (
	parentId	INT8 REFERENCES Commit(id),
	childId		INT8 REFERENCES Commit(id)
);

create table Branch (
	id		INT8 PRIMARY KEY,
	name		VARCHAR,
	commitId	INT8 REFERENCES Commit(id)
);
