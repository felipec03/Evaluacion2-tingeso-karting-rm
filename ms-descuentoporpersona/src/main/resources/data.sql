INSERT INTO descuentos_por_persona (min_personas, max_personas, porcentaje_descuento, descripcion, activo) VALUES
(2, 3, 0.05, 'Descuento para grupos pequeños (2-3 personas)', true),  -- 5%
(4, 6, 0.10, 'Descuento para grupos medianos (4-6 personas)', true), -- 10%
(7, 10, 0.15, 'Descuento para grupos grandes (7-10 personas)', true), -- 15%
(11, 999, 0.20, 'Descuento para grupos muy grandes (11+ personas)', true), -- 20%
(2, 5, 0.03, 'Promoción especial fin de semana (2-5 personas)', false); -- Regla inactiva