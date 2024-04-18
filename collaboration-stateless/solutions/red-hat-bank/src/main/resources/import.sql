CREATE SEQUENCE HIBERNATE_SEQUENCE START WITH 1 INCREMENT BY 1;

INSERT INTO bankaccount(id, balance, profile) VALUES (nextval('hibernate_sequence'), 1000, 'regular');
INSERT INTO bankaccount(id, balance, profile) VALUES (nextval('hibernate_sequence'), 2000, 'regular');
