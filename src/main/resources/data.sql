DROP TABLE IF EXISTS dependency;

CREATE TABLE dependency (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  table_name VARCHAR(250) NOT NULL,
  dependencies VARCHAR(250),
  etl VARCHAR(250),
  data_available INT
);

INSERT INTO dependency (table_name, dependencies, etl, data_available) VALUES
('t1', 't2,t3', 'select * from t1',1),
('t2', 't3,t4,t5', 'select * from t2',1),
('t3', '', 'select * from t3',1),
('t4', 't5', 'select * from t4',1),
('t5', '', 'select * from t5',1)
;