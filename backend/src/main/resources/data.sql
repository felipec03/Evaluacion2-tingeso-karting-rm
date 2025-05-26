INSERT INTO usuarios (nombre, apellido, email, telefono) VALUES
('Juan', 'Pérez', 'juan.perez@example.com', '+34 600 123 456'),
('María', 'Gómez', 'maria.gomez@example.com', '+34 611 234 567'),
('Carlos', 'Ruiz', 'carlos.ruiz@example.com', '+34 622 345 678'),
('Ana', 'Sánchez', 'ana.sanchez@example.com', '+34 633 456 789'),
('Felipe', 'Cubillos', 'felipecubillos13@gmail.com', '+56 9 7890 7299'),
('Luis', 'Fernández', 'luis.fernandez@example.com', '+34 644 567 890');

-- Updated INSERT for reservas with calculated financial values
INSERT INTO reservas (
inicio_reserva,
fin_reserva,
fecha,
emailarrendatario,
duracion,
numero_personas,
cumpleanios,
cantidadcumple,
precio_inicial,
descuento_grupo,
descuento_frecuente,
descuento_cumple,
iva,
total_con_iva,
tiporeserva
) VALUES
-- Reserva ID assumed to be 1
('2025-05-01 11:00:00', '2025-05-01 12:00:00', '2025-05-01', 'juan.perez@example.com', 2, 2, '1990-05-15', 1, 40000.00, 0.00, 2000.00, 0.00, 7220.00, 45220.00, 1),
-- Reserva ID assumed to be 2
('2025-05-02 15:00:00', '2025-05-02 16:00:00', '2025-05-02', 'maria.gomez@example.com', 2, 5, '1985-08-22', 0, 100000.00, 5000.00, 4750.00, 0.00, 17147.50, 107397.50, 1),
-- Reserva ID assumed to be 3
('2025-05-03 16:00:00', '2025-05-03 17:00:00', '2025-05-03', 'carlos.ruiz@example.com', 2, 3, NULL, 0, 54000.00, 2700.00, 2565.00, 0.00, 9259.65, 57994.65, 2),
-- Reserva ID assumed to be 4
('2025-05-04 15:30:00', '2025-05-04 16:30:00', '2025-05-04', 'ana.sanchez@example.com', 2, 1, '2000-02-10', 1, 24000.00, 0.00, 1200.00, 2280.00, 3898.80, 24418.80, 3),
-- Reserva ID assumed to be 5
('2025-05-05 16:00:00', '2025-05-05 17:00:00', '2025-05-05', 'luis.fernandez@example.com', 2, 2, '1999-12-05', 0, 40000.00, 0.00, 2000.00, 0.00, 7220.00, 45220.00, 1),
-- Reserva ID assumed to be 6
('2023-10-10 15:00:00', '2023-10-10 16:00:00', '2023-10-10', 'juan.perez@example.com', 2, 4, '1990-05-15', 1, 80000.00, 4000.00, 0.00, 0.00, 14440.00, 90440.00, 1),
-- Reserva ID assumed to be 7
('2023-10-10 15:00:00', '2023-10-10 16:00:00', '2023-10-10', 'maria.gomez@example.com', 2, 7, '1985-08-22', 0, 140000.00, 14000.00, 0.00, 0.00, 23940.00, 149940.00, 1),
-- Reserva ID assumed to be 8
('2023-10-11 17:00:00', '2023-10-11 18:00:00', '2023-10-11', 'carlos.ruiz@example.com', 2, 3, NULL, 0, 54000.00, 2700.00, 0.00, 0.00, 9747.00, 61047.00, 2),
-- Reserva ID assumed to be 9
('2023-10-11 20:30:00', '2023-10-11 21:30:00', '2023-10-11', 'ana.sanchez@example.com', 2, 5, '2000-02-10', 1, 120000.00, 0.00, 0.00, 12000.00, 20520.00, 128520.00, 3),
-- Reserva ID assumed to be 10
('2023-10-10 16:00:00', '2023-10-10 17:00:00', '2023-10-10', 'luis.fernandez@example.com', 2, 2, '1999-12-05', 0, 40000.00, 0.00, 0.00, 0.00, 7600.00, 47600.00, 1);

INSERT INTO karts (model, codificacion, estado) VALUES
('Model X', 'K001', 'PERFECTO'),
('Model X', 'K002', 'PERFECTO'),
('Model Y', 'K003', 'EN_MANTENIMIENTO'),
('Model Y', 'K004', 'FUERA_DE_SERVICIO'),
('Model Z', 'K005', 'PERFECTO'),
('Model Z', 'K006', 'PERFECTO'),
('Model A', 'K007', 'EN_MANTENIMIENTO'),
('Model B', 'K008', 'PERFECTO'),
('Model C', 'K009', 'FUERA_DE_SERVICIO'),
('Model A', 'K010', 'PERFECTO'),
('Model B', 'K011', 'PERFECTO'),
('Model C', 'K012', 'EN_MANTENIMIENTO'),
('Model X', 'K013', 'PERFECTO'),
('Model Y', 'K014', 'FUERA_DE_SERVICIO'),
('Model Z', 'K015', 'PERFECTO');

-- Updated INSERT for comprobantes to include reserva_id and match each reservation
-- The financial details here are placeholders as the report uses reserva.totalConIva
-- The 'codigo' should ideally be unique for each comprobante.
INSERT INTO comprobantes (reserva_id, email, codigo, tarifa_base, descuento_cumple, descuento_frecuente, descuento_grupo, iva, precio_sin_iva, total) VALUES
(1, 'juan.perez@example.com', 'COD001', 40000.00, 0.00, 2000.00, 0.00, 7220.00, 38000.00, 45220.00),
(2, 'maria.gomez@example.com', 'COD002', 100000.00, 0.00, 4750.00, 5000.00, 17147.50, 90250.00, 107397.50),
(3, 'carlos.ruiz@example.com', 'COD003', 54000.00, 0.00, 2565.00, 2700.00, 9259.65, 48735.00, 57994.65),
(4, 'ana.sanchez@example.com', 'COD004', 24000.00, 2280.00, 1200.00, 0.00, 3898.80, 20520.00, 24418.80),
(5, 'luis.fernandez@example.com', 'COD005', 40000.00, 0.00, 2000.00, 0.00, 7220.00, 38000.00, 45220.00),
(6, 'juan.perez@example.com', 'COD006', 80000.00, 0.00, 0.00, 4000.00, 14440.00, 76000.00, 90440.00),
(7, 'maria.gomez@example.com', 'COD007', 140000.00, 0.00, 0.00, 14000.00, 23940.00, 126000.00, 149940.00),
(8, 'carlos.ruiz@example.com', 'COD008', 54000.00, 0.00, 0.00, 2700.00, 9747.00, 51300.00, 61047.00),
(9, 'ana.sanchez@example.com', 'COD009', 120000.00, 12000.00, 0.00, 0.00, 20520.00, 108000.00, 128520.00),
(10, 'luis.fernandez@example.com', 'COD010', 40000.00, 0.00, 0.00, 0.00, 7600.00, 40000.00, 47600.00);