CREATE TABLE IF	NOT EXISTS request_log (
	id INTEGER IDENTITY PRIMARY KEY,
	request_name VARCHAR ( 30 ),
	created_date TIMESTAMP
);