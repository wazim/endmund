CREATE TABLE crossword
(
  ref int,
  id bigint NOT NULL
)


CREATE TABLE solutions
(
  id bigint NOT NULL,
  clue character varying(250),
  solution_length integer,
  solution character varying(100),
  edmund_solution character varying(100),
  hinted boolean,
  CONSTRAINT solutions_pkey PRIMARY KEY (id)
)

INSERT INTO crossword VALUES (0, 26330);

