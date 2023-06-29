CREATE TABLE company
(
    id integer NOT NULL,
    name character varying,
    CONSTRAINT company_pkey PRIMARY KEY (id)
);

CREATE TABLE person
(
    id integer NOT NULL,
    name character varying,
    company_id integer references company(id),
    CONSTRAINT person_pkey PRIMARY KEY (id)
);

INSERT INTO company (id, name) VALUES
(1, 'Apple'),
(2, 'Google'),
(3, 'IBM'),
(5, 'Bad Company');

INSERT INTO person (id, name, company_id) VALUES
(1, 'Ivan Yablochkov', 1),
(2, 'Ivan Ogrizok', 1),
(3, 'Ivan Aidared', 1),
(4, 'Sergei Brin', 2),
(5, 'Oleg Guglyaev', 2),
(6, 'Bill Gates', 3),
(7, 'John Windows', 3),
(8, 'John Connor', 3),
(9, 'Poor Boy', 5),
(10, 'New Boy', 3),
(11, 'Bad Boy', 5);

INSERT INTO person (id, name, company_id) VALUES
(12, 'very Bad Boy', 1);

SELECT person.name as Имя, company.name as Компания FROM
person join company on  company_id = company.id
WHERE company.id != 5;

CREATE VIEW maximum_employees AS
SELECT c.name AS Компания, COUNT(p.id) as Количество
FROM company c
JOIN person p ON c.id = p.company_id
GROUP BY c.name
HAVING COUNT(p.id) = (
    SELECT MAX(pc)
    FROM (
        SELECT COUNT(p.id) AS pc
        FROM company c
        JOIN person p ON c.id = p.company_id
        GROUP BY c.id
    ) AS subquery
);

SELECT * FROM maximum_employees;