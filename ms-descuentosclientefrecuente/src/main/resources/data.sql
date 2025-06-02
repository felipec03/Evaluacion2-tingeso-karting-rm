-- Sample data for the 'reservas' table in the descuentos-frecuentes-service
-- Note: The 'descuento_frecuente' column here represents a historical value for that past reservation.
-- The service calculates this for *new* potential reservations.

-- Juan Pérez:
-- Reserva 1 (old, outside 12 months for a 2025-05-01 check)
INSERT INTO reservas (inicio_reserva, fin_reserva, fecha, emailarrendatario, duracion, numero_personas, cumpleanios, cantidadcumple, precio_inicial, descuento_grupo, descuento_frecuente, descuento_cumple, iva, total_con_iva, tiporeserva) VALUES
    ('2023-10-10 15:00:00', '2023-10-10 16:00:00', '2023-10-10', 'juan.perez@example.com', 2, 4, '1990-05-15', 1, 80000.00, 4000.00, 0.00, 0.00, 14440.00, 90440.00, 1);

-- Reserva 2 (within 12 months for a 2025-05-01 check, if current date is e.g. 2025-04-01)
-- Let's make some reservations in the past 12 months for Juan to become frequent
INSERT INTO reservas (inicio_reserva, fin_reserva, fecha, emailarrendatario, duracion, numero_personas, cumpleanios, cantidadcumple, precio_inicial, descuento_grupo, descuento_frecuente, descuento_cumple, iva, total_con_iva, tiporeserva) VALUES
                                                                                                                                                                                                                                                  ('2024-06-15 11:00:00', '2024-06-15 12:00:00', '2024-06-15', 'juan.perez@example.com', 2, 2, '1990-05-15', 1, 40000.00, 0.00, 0.00, 0.00, 7600.00, 47600.00, 1),
                                                                                                                                                                                                                                                  ('2024-08-20 11:00:00', '2024-08-20 12:00:00', '2024-08-20', 'juan.perez@example.com', 2, 1, NULL, 0, 20000.00, 0.00, 0.00, 0.00, 3800.00, 23800.00, 1),
                                                                                                                                                                                                                                                  ('2024-11-05 11:00:00', '2024-11-05 12:00:00', '2024-11-05', 'juan.perez@example.com', 2, 3, NULL, 0, 60000.00, 3000.00, 0.00, 0.00, 10830.00, 67830.00, 2);
-- With these 3 reservations, if Juan makes a new reservation on, say, 2025-03-01, he should get the discount.

-- María Gómez: (fewer recent reservations)
INSERT INTO reservas (inicio_reserva, fin_reserva, fecha, emailarrendatario, duracion, numero_personas, cumpleanios, cantidadcumple, precio_inicial, descuento_grupo, descuento_frecuente, descuento_cumple, iva, total_con_iva, tiporeserva) VALUES
                                                                                                                                                                                                                                                  ('2023-10-10 15:00:00', '2023-10-10 16:00:00', '2023-10-10', 'maria.gomez@example.com', 2, 7, '1985-08-22', 0, 140000.00, 14000.00, 0.00, 0.00, 23940.00, 149940.00, 1),
                                                                                                                                                                                                                                                  ('2024-09-01 15:00:00', '2024-09-01 16:00:00', '2024-09-01', 'maria.gomez@example.com', 2, 5, '1985-08-22', 0, 100000.00, 5000.00, 0.00, 0.00, 18050.00, 113050.00, 1);
-- Maria only has 1 reservation in the last 12 months (assuming current check date is e.g. 2025-03-01)

-- Carlos Ruiz:
INSERT INTO reservas (inicio_reserva, fin_reserva, fecha, emailarrendatario, duracion, numero_personas, cumpleanios, cantidadcumple, precio_inicial, descuento_grupo, descuento_frecuente, descuento_cumple, iva, total_con_iva, tiporeserva) VALUES
    ('2023-10-11 17:00:00', '2023-10-11 18:00:00', '2023-10-11', 'carlos.ruiz@example.com', 2, 3, NULL, 0, 54000.00, 2700.00, 0.00, 0.00, 9747.00, 61047.00, 2);

-- Ana Sánchez:
INSERT INTO reservas (inicio_reserva, fin_reserva, fecha, emailarrendatario, duracion, numero_personas, cumpleanios, cantidadcumple, precio_inicial, descuento_grupo, descuento_frecuente, descuento_cumple, iva, total_con_iva, tiporeserva) VALUES
    ('2023-10-11 20:30:00', '2023-10-11 21:30:00', '2023-10-11', 'ana.sanchez@example.com', 2, 5, '2000-02-10', 1, 120000.00, 0.00, 0.00, 12000.00, 20520.00, 128520.00, 3);

-- Luis Fernández:
INSERT INTO reservas (inicio_reserva, fin_reserva, fecha, emailarrendatario, duracion, numero_personas, cumpleanios, cantidadcumple, precio_inicial, descuento_grupo, descuento_frecuente, descuento_cumple, iva, total_con_iva, tiporeserva) VALUES
    ('2023-10-10 16:00:00', '2023-10-10 17:00:00', '2023-10-10', 'luis.fernandez@example.com', 2, 2, '1999-12-05', 0, 40000.00, 0.00, 0.00, 0.00, 7600.00, 47600.00, 1);

-- Felipe Cubillos (no previous reservations in this sample set)
-- No inserts for felipecubillos13@gmail.com to test non-frequent case easily