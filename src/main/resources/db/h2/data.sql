INSERT INTO vets(id, first_name, last_name, created_by, created_date, last_modified_date) VALUES (default, 'James', 'Carter', 'system', NOW(), NOW());
INSERT INTO vets(id, first_name, last_name, created_by, created_date, last_modified_date) VALUES (default, 'Helen', 'Leary', 'system', NOW(), NOW());
INSERT INTO vets(id, first_name, last_name, created_by, created_date, last_modified_date) VALUES (default, 'Linda', 'Douglas', 'system', NOW(), NOW());
INSERT INTO vets(id, first_name, last_name, created_by, created_date, last_modified_date) VALUES (default, 'Rafael', 'Ortega', 'system', NOW(), NOW());
INSERT INTO vets(id, first_name, last_name, created_by, created_date, last_modified_date) VALUES (default, 'Henry', 'Stevens', 'system', NOW(), NOW());
INSERT INTO vets(id, first_name, last_name, created_by, created_date, last_modified_date) VALUES (default, 'Sharon', 'Jenkins', 'system', NOW(), NOW());

INSERT INTO specialties(id, name, created_by, created_date, last_modified_date) VALUES (default, 'radiology', 'system', NOW(), NOW());
INSERT INTO specialties(id, name, created_by, created_date, last_modified_date) VALUES (default, 'surgery', 'system', NOW(), NOW());
INSERT INTO specialties(id, name, created_by, created_date, last_modified_date) VALUES (default, 'dentistry', 'system', NOW(), NOW());

INSERT INTO vet_specialties VALUES (2, 1);
INSERT INTO vet_specialties VALUES (3, 2);
INSERT INTO vet_specialties VALUES (3, 3);
INSERT INTO vet_specialties VALUES (4, 2);
INSERT INTO vet_specialties VALUES (5, 1);

INSERT INTO types(id, name, created_by, created_date, last_modified_date) VALUES (default, 'cat', 'system', NOW(), NOW());
INSERT INTO types(id, name, created_by, created_date, last_modified_date) VALUES (default, 'dog', 'system', NOW(), NOW());
INSERT INTO types(id, name, created_by, created_date, last_modified_date) VALUES (default, 'lizard', 'system', NOW(), NOW());
INSERT INTO types(id, name, created_by, created_date, last_modified_date) VALUES (default, 'snake', 'system', NOW(), NOW());
INSERT INTO types(id, name, created_by, created_date, last_modified_date) VALUES (default, 'bird', 'system', NOW(), NOW());
INSERT INTO types(id, name, created_by, created_date, last_modified_date) VALUES (default, 'hamster', 'system', NOW(), NOW());

INSERT INTO owners(id, first_name, last_name, address, city, telephone, created_by, created_date, last_modified_date) VALUES (default, 'George', 'Franklin', '110 W. Liberty St.', 'Madison', '6085551023', 'system', NOW(), NOW());
INSERT INTO owners(id, first_name, last_name, address, city, telephone, created_by, created_date, last_modified_date) VALUES (default, 'Betty', 'Davis', '638 Cardinal Ave.', 'Sun Prairie', '6085551749', 'system', NOW(), NOW());
INSERT INTO owners(id, first_name, last_name, address, city, telephone, created_by, created_date, last_modified_date) VALUES (default, 'Eduardo', 'Rodriquez', '2693 Commerce St.', 'McFarland', '6085558763', 'system', NOW(), NOW());
INSERT INTO owners(id, first_name, last_name, address, city, telephone, created_by, created_date, last_modified_date) VALUES (default, 'Harold', 'Davis', '563 Friendly St.', 'Windsor', '6085553198', 'system', NOW(), NOW());
INSERT INTO owners(id, first_name, last_name, address, city, telephone, created_by, created_date, last_modified_date) VALUES (default, 'Peter', 'McTavish', '2387 S. Fair Way', 'Madison', '6085552765', 'system', NOW(), NOW());
INSERT INTO owners(id, first_name, last_name, address, city, telephone, created_by, created_date, last_modified_date) VALUES (default, 'Jean', 'Coleman', '105 N. Lake St.', 'Monona', '6085552654', 'system', NOW(), NOW());
