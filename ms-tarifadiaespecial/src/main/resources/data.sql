-- Ensure the table name matches your Entity's @Table annotation
INSERT INTO tarifas_dias_especiales (fecha, descripcion, tipo_tarifa, valor) VALUES
('2024-12-25', 'Navidad', 'FIJA', 25000.00)
ON CONFLICT (fecha) DO NOTHING; -- Avoid errors if data already exists

INSERT INTO tarifas_dias_especiales (fecha, descripcion, tipo_tarifa, valor) VALUES
('2025-01-01', 'Año Nuevo', 'PORCENTUAL_RECARGO', 10.0) -- 10% recargo
ON CONFLICT (fecha) DO NOTHING;

INSERT INTO tarifas_dias_especiales (fecha, descripcion, tipo_tarifa, valor) VALUES
('2024-09-18', 'Fiestas Patrias Chile', 'FIJA', 30000.00)
ON CONFLICT (fecha) DO NOTHING;

INSERT INTO tarifas_dias_especiales (fecha, descripcion, tipo_tarifa, valor) VALUES
('2024-05-01', 'Día del Trabajador', 'PORCENTUAL_DESCUENTO', 15.0) -- 15% descuento
ON CONFLICT (fecha) DO NOTHING;