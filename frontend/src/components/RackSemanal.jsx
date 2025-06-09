// ...existing code...
import React, { useState, useEffect, useCallback } from 'react';
import RackSemanalService from '../services/RackSemanalService';
import { Calendar, momentLocalizer } from 'react-big-calendar';
import moment from 'moment';
import 'moment/locale/es';
import 'react-big-calendar/lib/css/react-big-calendar.css';
import { useNavigate } from 'react-router-dom';
// import './RackSemanal.css'; // Ensure this is imported if you have custom styles for the component or .rbc-non-business-slot

moment.locale('es');
const localizer = momentLocalizer(moment);

// Define min and max times for the calendar view
const minCalendarTime = new Date();
minCalendarTime.setHours(10, 0, 0, 0); // Earliest start time (10:00 AM)

const maxCalendarTime = new Date();
maxCalendarTime.setHours(22, 0, 0, 0); // Latest end time (22:00 PM)


const RackSemanal = () => {
  const [reservas, setReservas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentDate, setCurrentDate] = useState(moment());
  const [currentView, setCurrentView] = useState('week');
  const navigate = useNavigate();

  const formatTipoReservaText = (tipoReserva) => {
// ...existing code...
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
              resource: reserva, 
            };
          });
          setReservas(calendarEvents);
        } else {
          console.error('Error: La respuesta de la API no es un array:', response.data);
          setError('Error: Los datos recibidos del servidor no tienen el formato esperado.');
          setReservas([]); 
        }
      } catch (err) {
        console.error('Error al cargar las reservas:', err);
        let errorMessage = 'Error al cargar las reservas. Por favor, intente nuevamente.';
        if (err.response && err.response.data) {
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

  const handleSelectSlot = useCallback(({ start }) => {
    const selectedMoment = moment(start);
    const dayOfWeek = selectedMoment.day(); // 0 (Sunday) to 6 (Saturday)
    const hour = selectedMoment.hour();
    const minute = selectedMoment.minute(); // To ensure we check the start of the hour

    // For simplicity, "Festives" are treated like weekends.
    // A more robust solution would involve a list/API of holidays.
    const isWeekendOrFestive = dayOfWeek === 0 || dayOfWeek === 6; 

    let isValidSlot = false;
    let alertMessage = 'La hora seleccionada está fuera del horario de atención.\n\nHorarios:\nLunes a Viernes: 14:00 - 22:00\nSábados, Domingos y Festivos: 10:00 - 22:00';

    if (isWeekendOrFestive) { // Saturday, Sunday, Festives
      if (hour >= 10 && (hour < 22 || (hour === 22 && minute === 0))) { // Allow up to 22:00 start
        isValidSlot = true;
      }
    } else { // Monday to Friday
      if (hour >= 14 && (hour < 22 || (hour === 22 && minute === 0))) { // Allow up to 22:00 start
        isValidSlot = true;
      }
    }
    
    // Also check if the slot is before the overall calendar min time (10:00)
    // or after the overall calendar max time (22:00)
    // This is a secondary check as slotPropGetter should visually indicate this.
    if (selectedMoment.hour() < moment(minCalendarTime).hour() || selectedMoment.hour() >= moment(maxCalendarTime).hour()) {
        // If the slot itself is outside the 10-22 range, it's definitely invalid.
        // This case should ideally be covered by the specific day logic too.
        if (selectedMoment.hour() < 10 && isWeekendOrFestive) {
             // valid if it's weekend and 10 or more
        } else if (selectedMoment.hour() < 14 && !isWeekendOrFestive) {
            // valid if it's weekday and 14 or more
        }
        else {
            // isValidSlot = false; // Redundant if already handled, but good for clarity
        }
    }


    if (isValidSlot) {
      const selectedDate = selectedMoment.format('YYYY-MM-DD');
      const selectedTime = selectedMoment.format('HH:mm');
      navigate(`/agregar-reserva?date=${selectedDate}&time=${selectedTime}`);
    } else {
      alert(alertMessage);
    }
  }, [navigate]);

  const handleNavigate = (newDate) => {
    setCurrentDate(moment(newDate));
  };

  const handleViewChange = (newView) => {
    setCurrentView(newView);
  };

  const goToBack = () => setCurrentDate(moment(currentDate).subtract(1, currentView === 'month' ? 'month' : 'week'));
  const goToNext = () => setCurrentDate(moment(currentDate).add(1, currentView === 'month' ? 'month' : 'week'));
  const goToCurrent = () => setCurrentDate(moment());

  const dayPropGetter = useCallback((date) => {
    if (moment(date).isSame(moment(), 'day')) {
      return {
        className: 'rbc-today', 
        style: {
          backgroundColor: '#eaf6ff', 
        },
      };
    }
    return {};
  }, []);

  const slotPropGetter = useCallback((date) => {
    const currentMoment = moment(date);
    const dayOfWeek = currentMoment.day(); // 0 (Sunday) to 6 (Saturday)
    const hour = currentMoment.hour();

    const isWeekendOrFestive = dayOfWeek === 0 || dayOfWeek === 6;
    let isOutsideBusinessHours = false;

    if (isWeekendOrFestive) { // Saturday, Sunday, Festives
      if (hour < 10 || hour >= 22) { // Slots before 10 AM or at/after 10 PM
        isOutsideBusinessHours = true;
      }
    } else { // Monday to Friday
      if (hour < 14 || hour >= 22) { // Slots before 2 PM or at/after 10 PM
        isOutsideBusinessHours = true;
      }
    }

    if (isOutsideBusinessHours) {
      return {
        style: {
          backgroundColor: '#e9ecef', // A light gray, Bootstrap's 'light' background
          cursor: 'not-allowed',
        },
        className: 'rbc-non-business-slot', // For further CSS customization if needed
      };
    }
    return {};
  }, []);
  
  const displayDateRange = () => {
    if (currentView === 'month') {
      return currentDate.format('MMMM YYYY');
    }
    if (currentView === 'week' || currentView === 'day') {
      const startRange = moment(currentDate).startOf(currentView === 'week' ? 'week' : 'day');
      const endRange = moment(currentDate).endOf(currentView === 'week' ? 'week' : 'day');
      if (currentView === 'day') {
        return startRange.format('dddd, D MMMM YYYY');
      }
      return `Semana del ${startRange.format('D MMM')} al ${endRange.format('D MMM, YYYY')}`;
    }
    return currentDate.format('D MMMM YYYY'); // For agenda or other views
  };

  if (loading) return <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '300px', fontSize: '18px' }}>Cargando reservas...</div>;
  if (error) return <div style={{ color: 'red', textAlign: 'center', padding: '20px', fontSize: '18px' }}>Error: {error}</div>;
  
  return (
    <div style={{ padding: '20px', fontFamily: 'Arial, sans-serif' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px', flexWrap: 'wrap', gap: '10px' }}>
        <div className="btn-group" role="group" aria-label="Navegación de fecha">
          <button onClick={goToCurrent} className="btn btn-primary">Hoy</button>
          <button onClick={goToBack} className="btn btn-outline-secondary">‹ Anterior</button>
          <button onClick={goToNext} className="btn btn-outline-secondary">Siguiente ›</button>
        </div>
        <h3 style={{ margin: 0, textAlign: 'center', flexGrow: 1 }}>{displayDateRange()}</h3>
      </div>
      
      <h2 style={{ textAlign: 'center', marginBottom: '20px' }}>Calendario de Reservas</h2>
      <div style={{ height: '75vh' }}>
        <Calendar
          localizer={localizer}
          events={reservas}
          startAccessor="start"
          endAccessor="end"
          style={{ height: '100%' }}
          views={['month', 'week', 'day', 'agenda']}
          view={currentView} 
          onView={handleViewChange} 
          date={currentDate.toDate()} 
          onNavigate={handleNavigate} 
          selectable 
          onSelectSlot={handleSelectSlot} 
          onSelectEvent={handleSelectEvent}
          dayPropGetter={dayPropGetter} 
          slotPropGetter={slotPropGetter} // Added slotPropGetter
          min={minCalendarTime} // Set minimum time for the calendar view
          max={maxCalendarTime} // Set maximum time for the calendar view
          messages={{
            allDay: 'Todo el día',
            previous: 'Anterior',
            next: 'Siguiente',
            today: 'Hoy',
            month: 'Mes',
            week: 'Semana',
// ...existing code...
            day: 'Día',
            agenda: 'Agenda',
            date: 'Fecha',
            time: 'Hora',
            event: 'Evento (Reserva)',
            noEventsInRange: 'No hay reservas en este rango.',
            showMore: total => `+ Ver ${total} más`,
          }}
          eventPropGetter={(event) => {
            let newStyle = {
              backgroundColor: "#3174ad", 
              color: "white",
              borderRadius: "5px",
              border: "none"
            };
            if (event.resource.tipoReserva === 1) { 
              newStyle.backgroundColor = "#5cb85c"; 
            } else if (event.resource.tipoReserva === 2) { 
              newStyle.backgroundColor = "#f0ad4e"; 
            } else if (event.resource.tipoReserva === 3) { 
              newStyle.backgroundColor = "#d9534f"; 
            }
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