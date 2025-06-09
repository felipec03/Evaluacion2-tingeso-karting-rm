import React, { useState, useEffect, useCallback } from 'react';
import RackSemanalService from '../services/RackSemanalService';
import { Calendar, momentLocalizer } from 'react-big-calendar';
import moment from 'moment';
import 'moment/locale/es';
import 'react-big-calendar/lib/css/react-big-calendar.css';

moment.locale('es');
const localizer = momentLocalizer(moment);

const RackSemanal = () => {
  const [reservas, setReservas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const formatTipoReservaText = (tipoReserva) => {
    // Adjust this mapping based on your actual tipoReserva definitions
    if (tipoReserva === 1) return "10 vueltas";
    if (tipoReserva === 2) return "15 vueltas";
    if (tipoReserva === 3) return "20 vueltas";
    return `Tipo ${tipoReserva}`;
  };

  useEffect(() => {
    const fetchReservasData = async () => {
      try {
        setLoading(true);
        setError(null);
        const response = await RackSemanalService.obtenerTodasLasReservas();
        
        // Check if response.data is an array
        if (Array.isArray(response.data)) {
          const validReservations = response.data.filter(
            reserva => reserva.estadoReserva && 
                       reserva.estadoReserva.toUpperCase() !== 'CANCELADA' &&
                       reserva.fechaHora && 
                       typeof reserva.duracionMinutos === 'number'
          );

          const calendarEvents = validReservations.map(reserva => {
            const startTime = moment(reserva.fechaHora);
            const endTime = moment(reserva.fechaHora).add(reserva.duracionMinutos, 'minutes');
            
            let title = `${reserva.nombreUsuario || reserva.emailUsuario || 'Cliente Desconocido'}`;
            title += ` (${reserva.cantidadPersonas || 0}p)`;
            title += ` - ${formatTipoReservaText(reserva.tipoReserva)}`;
            if (reserva.cantidadCumple && reserva.cantidadCumple > 0) {
              title += ` (Cumple: ${reserva.cantidadCumple})`;
            }
            
            return {
              id: reserva.id,
              title: title,
              start: startTime.toDate(),
              end: endTime.toDate(),
              allDay: false,
              resource: reserva, // Store original reserva data
            };
          });
          setReservas(calendarEvents);
        } else {
          // Handle cases where response.data is not an array
          console.error('Error: La respuesta de la API no es un array:', response.data);
          setError('Error: Los datos recibidos del servidor no tienen el formato esperado.');
          setReservas([]); // Set to empty array to prevent further errors
        }
      } catch (err) {
        console.error('Error al cargar las reservas:', err);
        let errorMessage = 'Error al cargar las reservas. Por favor, intente nuevamente.';
        if (err.response && err.response.data) {
            // If the error response has data, try to use its message or stringify it
            if (typeof err.response.data === 'string') {
                errorMessage = err.response.data;
            } else if (err.response.data.message) {
                errorMessage = err.response.data.message;
            } else {
                errorMessage = JSON.stringify(err.response.data);
            }
        } else if (err.message) {
            errorMessage = err.message;
        }
        setError(errorMessage);
        setReservas([]);
      } finally {
        setLoading(false);
      }
    };
    
    fetchReservasData();
  }, []);

  const handleSelectEvent = useCallback((event) => {
    const reserva = event.resource;
    let details = `Reserva ID: ${reserva.id}\n`;
    details += `Cliente: ${reserva.nombreUsuario || 'N/A'} (${reserva.emailUsuario || 'N/A'})\n`;
    details += `Teléfono: ${reserva.telefonoUsuario || 'N/A'}\n`;
    details += `RUT: ${reserva.rutUsuario || 'N/A'}\n`;
    details += `Fecha y Hora: ${moment(reserva.fechaHora).format('DD/MM/YYYY HH:mm')}\n`;
    details += `Duración: ${reserva.duracionMinutos} minutos\n`;
    details += `Tipo: ${formatTipoReservaText(reserva.tipoReserva)}\n`;
    details += `Personas: ${reserva.cantidadPersonas}\n`;
    if (reserva.cantidadCumple > 0) {
      details += `Cumpleaños: ${reserva.cantidadCumple}\n`;
    }
    details += `Estado: ${reserva.estadoReserva}\n`;
    details += `Monto Final: $${reserva.montoFinal != null ? reserva.montoFinal.toFixed(0) : 'N/A'}\n`;
    alert(details);
  }, []);


  if (loading) return <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '300px', fontSize: '18px' }}>Cargando reservas...</div>;
  if (error) return <div style={{ color: 'red', textAlign: 'center', padding: '20px', fontSize: '18px' }}>Error: {error}</div>;
  
  return (
    <div style={{ padding: '20px', fontFamily: 'Arial, sans-serif' }}>
      <h2 style={{ textAlign: 'center', marginBottom: '20px' }}>Calendario de Reservas</h2>
      <div style={{ height: '75vh' }}>
        <Calendar
          localizer={localizer}
          events={reservas}
          startAccessor="start"
          endAccessor="end"
          style={{ height: '100%' }}
          views={['month', 'week', 'day', 'agenda']}
          defaultView="week"
          selectable
          onSelectEvent={handleSelectEvent}
          messages={{
            allDay: 'Todo el día',
            previous: 'Anterior',
            next: 'Siguiente',
            today: 'Hoy',
            month: 'Mes',
            week: 'Semana',
            day: 'Día',
            agenda: 'Agenda',
            date: 'Fecha',
            time: 'Hora',
            event: 'Evento (Reserva)',
            noEventsInRange: 'No hay reservas en este rango.',
            showMore: total => `+ Ver ${total} más`,
          }}
          eventPropGetter={(event) => {
            // Example: style events based on tipoReserva or estadoReserva
            let newStyle = {
              backgroundColor: "#3174ad", // Default color
              color: "white",
              borderRadius: "5px",
              border: "none"
            };
            if (event.resource.tipoReserva === 1) { // Example: "10 vueltas"
              newStyle.backgroundColor = "#5cb85c"; // Green
            } else if (event.resource.tipoReserva === 2) { // Example: "15 vueltas"
              newStyle.backgroundColor = "#f0ad4e"; // Orange
            } else if (event.resource.tipoReserva === 3) { // Example: "20 vueltas"
              newStyle.backgroundColor = "#d9534f"; // Red
            }
            // Add more conditions for other states or types
            return {
              className: "",
              style: newStyle
            };
          }}
        />
      </div>
    </div>
  );
};

export default RackSemanal;