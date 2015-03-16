CREATE TABLE crossword
(
  ref int,
  id bigint NOT NULL
);

CREATE TABLE id_generator (
  id int
);

CREATE TABLE solutions
(
  id bigint NOT NULL,
  clue character varying(250),
  solution_length integer,
  solution character varying(100),
  edmund_solution character varying(100),
  hinted boolean,
  CONSTRAINT solutions_pkey PRIMARY KEY (id)
);

INSERT INTO id_generator VALUES(0);
INSERT INTO crossword VALUES (0, 26330);

