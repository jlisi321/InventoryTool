INSERT INTO parts (part_number, description, monthly_demand, unit_cost, status) VALUES
('12345-AC','Front End Loader',4,4.25,'ACTIVE'),
('12456-AC','Brush Hog',5,5.55,'OBSOLETE'),
('34456-AC','Back Blade',6,6.25,'ACTIVE'),
('24456-AC','Brush Hog',7,4725.59,'OBSOLETE'),
('87456-AC','PTO Spline',8,10.25,'ACTIVE'),
('98456-AC','Bale Spear',9,11.25,'OBSOLETE'),
('58456-AC','Front Grapples',10,12.25,'ACTIVE');

INSERT INTO disposition_requests (part_number, type, quantity, justification, status) VALUES
('12456-AC', 'LAST_TIME_BUY', 50, 'Test data 1', 'SUBMITTED'),
('24456-AC', 'DISCONTINUE', NULL, 'Test data 2', 'DRAFT'),
('98456-AC', 'DISCONTINUE', NULL, 'Test data 3', 'APPROVED');
