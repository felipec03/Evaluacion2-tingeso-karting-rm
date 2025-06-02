-- filepath: ms-tarifasconfig/src/main/resources/data.sql

-- Insert general configuration (assuming only one row is needed)
-- Delete any existing configuration first to ensure a clean slate if IDs are fixed
DELETE FROM configuracion_general;
INSERT INTO configuracion_general (id, duracion_minima_horas_reserva, duracion_maxima_horas_reserva, intervalo_permitido_horas_reserva) VALUES
    (1, 1, 4, 1); -- Min 1h, Max 4h, Intervals of 1h

-- Insert some sample holidays (format MM-dd)
DELETE FROM feriados;
INSERT INTO feriados (fecha, descripcion) VALUES
                                              ('01-01', 'Año Nuevo'),
                                              ('05-01', 'Día del Trabajador'),
                                              ('12-25', 'Navidad');
-- Add a specific date from your monolith data if you want to test it as a holiday
-- For example, if 2025-05-01 was a holiday in your monolith's logic:
-- INSERT INTO feriados (fecha, descripcion) VALUES ('05-01', 'Feriado Ejemplo');

-- Insert sample tariffs
-- tipoReserva: 1 for Adulto, 2 for Niño, 3 for Mixta (as an example)
DELETE FROM tarifas;
INSERT INTO tarifas (tipo_reserva, descripcion, precio_base_por_persona, porcentaje_recargo_fin_de_semana, porcentaje_recargo_feriado, activa) VALUES
                                                                                                                                                   (1, 'Adulto (General)', 20000.00, 0.10, 0.20, true), -- 10% weekend, 20% holiday
                                                                                                                                                   (2, 'Niño (General)', 15000.00, 0.10, 0.15, true),  -- 10% weekend, 15% holiday
                                                                                                                                                   (3, 'Grupo Mixto (Especial)', 22000.00, 0.15, 0.25, true), -- 15% weekend, 25% holiday
                                                                                                                                                   (4, 'Adulto (Desactivada)', 18000.00, 0.05, 0.10, false); -- An inactive tariff