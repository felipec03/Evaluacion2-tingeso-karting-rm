import React, { useState, useEffect, useCallback } from 'react';
import RackSemanalService from '../services/RackSemanalService';
import { Calendar, momentLocalizer } from 'react-big-calendar';
import moment from 'moment';
import 'moment/locale/es'; // Spanish locale for moment
import 'react-big-calendar/lib/css/react-big-calendar.css';
import { useNavigate } from 'react-router-dom';
import { Modal, Button, ListGroup, Badge, Spinner, Alert } from 'react-bootstrap';
import './RackSemanal.css'; // Make sure to create and link this CSS file

moment.locale('es');
const localizer = momentLocalizer(moment);

// Define min and max times for the calendar view
const minCalendarTime = new Date();
minCalendarTime.setHours(10, 0, 0, 0); // Calendar starts at 10:00 AM

const maxCalendarTime = new Date();
maxCalendarTime.setHours(22, 0, 0, 0); // Calendar ends at 10:00 PM

// Define 24-hour formats for the calendar
const calendarFormats = {
  timeGutterFormat: 'HH:mm',
  eventTimeRangeFormat: ({ start, end }, culture, local) =>
    local.format(start, 'HH:mm', culture) + ' - ' + local.format(end, 'HH:mm', culture),
  agendaTimeRangeFormat: ({ start, end }, culture, local) =>
    local.format(start, 'HH:mm', culture) + ' - ' + local.format(end, 'HH:mm', culture),
  selectRangeFormat: ({ start, end }, culture, local) =>
    local.format(start, 'HH:mm', culture) + ' - ' + local.format(end, 'HH:mm', culture),
  dayFormat: 'ddd D/M',
  dayHeaderFormat: (date, culture, local) => local.format(date, 'dddd D MMMM', culture),
  dayRangeHeaderFormat: ({ start, end }, culture, local) =>
    local.format(start, 'D MMM', culture) + ' - ' + local.format(end, 'D MMM YYYY', culture),
};

const RackSemanal = () => {
  const [reservas, setReservas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentDate, setCurrentDate] = useState(moment()); // For calendar navigation state
  const [currentView, setCurrentView] = useState('week'); // Default view

  const navigate = useNavigate();

  // State for the reservation detail modal
  const [showDetailModal, setShowDetailModal] = useState(false);
  const [selectedReservaDetail, setSelectedReservaDetail] = useState(null);

  // State for the "Out of Business Hours" modal
  const [showOutOfHoursModal, setShowOutOfHoursModal] = useState(false);
  const [outOfHoursMessage, setOutOfHoursMessage] = useState({ title: '', body: [] });

  const formatTipoReservaText = (tipoReserva) => {
    if (tipoReserva === 1) return "Normal (10 vueltas)";
    if (tipoReserva === 2) return "Extendida (15 vueltas)";
    if (tipoReserva === 3) return "Premium (20 vueltas)";
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
            
            let titleParts = [];
            titleParts.push(reserva.nombreUsuario || reserva.emailUsuario || 'Cliente');
            titleParts.push(`(${reserva.cantidadPersonas || 0}p)`);
            titleParts.push(formatTipoReservaText(reserva.tipoReserva).split(' ')[0]);
            if (reserva.cantidadCumple && reserva.cantidadCumple > 0) {
              titleParts.push(`(🎂 ${reserva.cantidadCumple})`);
            }
            
            return {
              id: reserva.id,
              title: titleParts.join(' '),
              start: startTime.toDate(),
              end: endTime.toDate(),
              allDay: false,
              resource: reserva, 
            };
          });
          setReservas(calendarEvents);
        } else {
          setError('Error: Los datos recibidos del servidor no tienen el formato esperado.');
          setReservas([]); 
        }
      } catch (err) {
        let errorMessage = 'Error al cargar las reservas. Por favor, intente nuevamente.';
        if (err.response && err.response.data) {
            errorMessage = typeof err.response.data === 'string' ? err.response.data : (err.response.data.message || JSON.stringify(err.response.data));
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
    setSelectedReservaDetail(event.resource); // Store the full reservation object
    setShowDetailModal(true);
  }, []);

  const handleCloseDetailModal = () => {
    setShowDetailModal(false);
    setSelectedReservaDetail(null);
  };

  const handleCloseOutOfHoursModal = () => {
    setShowOutOfHoursModal(false);
  };

  const handleSelectSlot = useCallback(({ start }) => {
    const selectedMoment = moment(start);
    const dayOfWeek = selectedMoment.day(); 
    const hour = selectedMoment.hour();
    const minute = selectedMoment.minute(); 

    const isWeekendOrFestive = dayOfWeek === 0 || dayOfWeek === 6; 
    let isValidSlot = false;
    
    // Define the message for the modal
    const alertTitle = "Horario No Disponible";
    const alertBodyLines = [
        "La hora seleccionada está fuera de nuestro horario de atención.",
        "",
        "Nuestros Horarios:",
        "Lunes a Viernes: 14:00 - 22:00",
        "Sábados, Domingos y Festivos: 10:00 - 22:00"
    ];

    if (isWeekendOrFestive) { 
      if (hour >= 10 && (hour < 22 || (hour === 22 && minute === 0))) { 
        isValidSlot = true;
      }
    } else { 
      if (hour >= 14 && (hour < 22 || (hour === 22 && minute === 0))) { 
        isValidSlot = true;
      }
    }
    
    if (selectedMoment.hour() < moment(minCalendarTime).hour() || selectedMoment.hour() >= moment(maxCalendarTime).hour()) {
        if (!((isWeekendOrFestive && selectedMoment.hour() >= 10) || (!isWeekendOrFestive && selectedMoment.hour() >= 14)) || selectedMoment.hour() >= 22 ) {
            isValidSlot = false; 
        }
    }

    if (isValidSlot) {
      const selectedDate = selectedMoment.format('YYYY-MM-DD');
      const selectedTime = selectedMoment.format('HH:mm');
      navigate(`/agregar-reserva?date=${selectedDate}&time=${selectedTime}`);
    } else {
      // Show the custom modal instead of alert
      setOutOfHoursMessage({ title: alertTitle, body: alertBodyLines });
      setShowOutOfHoursModal(true);
    }
  }, [navigate]);

  const handleNavigate = (newDate) => {
    setCurrentDate(moment(newDate));
  };

  const handleViewChange = (newView) => {
    setCurrentView(newView);
  };

  const dayPropGetter = useCallback((date) => {
    if (moment(date).isSame(moment(), 'day')) {
      return { className: 'rbc-today' };
    }
    return {};
  }, []);

  const slotPropGetter = useCallback((date) => {
    const currentMoment = moment(date);
    const dayOfWeek = currentMoment.day(); 
    const hour = currentMoment.hour();
    const isWeekendOrFestive = dayOfWeek === 0 || dayOfWeek === 6;
    let isOutsideBusinessHours = false;

    if (isWeekendOrFestive) { 
      if (hour < 10 || hour >= 22) { 
        isOutsideBusinessHours = true;
      }
    } else { 
      if (hour < 14 || hour >= 22) { 
        isOutsideBusinessHours = true;
      }
    }

    if (isOutsideBusinessHours) {
      return { className: 'rbc-non-business-slot' };
    }
    return {};
  }, []);
  
  if (loading) {
    return (
        <div className="loading-container">
            <Spinner animation="border" variant="primary" style={{ width: '3rem', height: '3rem' }} />
            <span className="ms-3 fs-5">Cargando reservas...</span>
        </div>
    );
  }
  if (error) {
    return (
        <Alert variant="danger" className="text-center m-4">
            <h4>Error al Cargar Reservas</h4>
            <p>{error}</p>
        </Alert>
    );
  }
  
  return (
    <div className="container-fluid mt-4 rack-semanal-container">
      <h2 className="text-center mb-4">Rack Semanal de Reservas</h2>
      <div className="calendar-wrapper">
        {/* Calendar component remains the same */}
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
          slotPropGetter={slotPropGetter} 
          min={minCalendarTime} 
          max={maxCalendarTime} 
          formats={calendarFormats}
          step={30}
          timeslots={1}
          messages={{
            allDay: 'Todo el día',
            previous: '‹',
            next: '›',
            today: 'Hoy',
            month: 'Mes',
            week: 'Semana',
            day: 'Día',
            agenda: 'Agenda',
            date: 'Fecha',
            time: 'Hora',
            event: 'Reserva',
            noEventsInRange: 'No hay reservas en este rango.',
            showMore: total => `+ Ver ${total} más`,
          }}
          eventPropGetter={(event) => {
            let newStyle = {
              backgroundColor: "#3174ad", 
              color: "white",
              borderRadius: "4px",
              border: "none",
              padding: "8px 8px",
              fontSize: "0.875em", 
              lineHeight: "1.4", 
            };
            if (event.resource.tipoReserva === 1) newStyle.backgroundColor = "#5cb85c";
            else if (event.resource.tipoReserva === 2) newStyle.backgroundColor = "#f0ad4e";
            else if (event.resource.tipoReserva === 3) newStyle.backgroundColor = "#d9534f";
            return { style: newStyle };
          }}
          components={{
            toolbar: (toolbar) => {
              const goToBack = () => toolbar.onNavigate('PREV');
              const goToNext = () => toolbar.onNavigate('NEXT');
              const goToCurrent = () => toolbar.onNavigate('TODAY');
              const goToView = (view) => toolbar.onView(view);

              return (
                <div className="rbc-toolbar custom-toolbar">
                  <span className="rbc-btn-group">
                    <Button variant="outline-secondary" size="sm" onClick={goToCurrent}>Hoy</Button>
                    <Button variant="outline-secondary" size="sm" onClick={goToBack}>‹ Anterior</Button>
                    <Button variant="outline-secondary" size="sm" onClick={goToNext}>Siguiente ›</Button>
                  </span>
                  <span className="rbc-toolbar-label">{toolbar.label}</span>
                  <span className="rbc-btn-group">
                    {toolbar.views.includes('month') && <Button variant={toolbar.view === 'month' ? 'primary' : 'outline-primary'} size="sm" onClick={() => goToView('month')}>Mes</Button>}
                    {toolbar.views.includes('week') && <Button variant={toolbar.view === 'week' ? 'primary' : 'outline-primary'} size="sm" onClick={() => goToView('week')}>Semana</Button>}
                    {toolbar.views.includes('day') && <Button variant={toolbar.view === 'day' ? 'primary' : 'outline-primary'} size="sm" onClick={() => goToView('day')}>Día</Button>}
                    {toolbar.views.includes('agenda') && <Button variant={toolbar.view === 'agenda' ? 'primary' : 'outline-primary'} size="sm" onClick={() => goToView('agenda')}>Agenda</Button>}
                  </span>
                </div>
              );
            }
          }}
        />
      </div>

      {/* Reservation Detail Modal (existing) */}
      {selectedReservaDetail && (
        <Modal show={showDetailModal} onHide={handleCloseDetailModal} centered size="lg">
          <Modal.Header closeButton>
            <Modal.Title>
              Detalles de la Reserva <Badge bg="primary">ID: {selectedReservaDetail.id}</Badge>
            </Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <ListGroup variant="flush">
              <ListGroup.Item><strong>Cliente:</strong> {selectedReservaDetail.nombreUsuario || 'N/A'}</ListGroup.Item>
              <ListGroup.Item><strong>Email:</strong> {selectedReservaDetail.emailUsuario || 'N/A'}</ListGroup.Item>
              <ListGroup.Item><strong>Teléfono:</strong> {selectedReservaDetail.telefonoUsuario || 'N/A'}</ListGroup.Item>
              <ListGroup.Item><strong>RUT:</strong> {selectedReservaDetail.rutUsuario || 'N/A'}</ListGroup.Item>
              <ListGroup.Item><strong>Fecha y Hora:</strong> {moment(selectedReservaDetail.fechaHora).format('DD/MM/YYYY HH:mm')}</ListGroup.Item>
              <ListGroup.Item><strong>Duración:</strong> {selectedReservaDetail.duracionMinutos} minutos</ListGroup.Item>
              <ListGroup.Item><strong>Tipo de Reserva:</strong> {formatTipoReservaText(selectedReservaDetail.tipoReserva)}</ListGroup.Item>
              <ListGroup.Item><strong>Cantidad de Personas:</strong> {selectedReservaDetail.cantidadPersonas}</ListGroup.Item>
              {selectedReservaDetail.cantidadCumple > 0 && (
                <ListGroup.Item><strong>Personas en Cumpleaños:</strong> {selectedReservaDetail.cantidadCumple}</ListGroup.Item>
              )}
              <ListGroup.Item>
                <strong>Estado:</strong> <Badge bg={selectedReservaDetail.estadoReserva === 'CONFIRMADA' ? 'success' : 'warning'}>{selectedReservaDetail.estadoReserva}</Badge>
              </ListGroup.Item>
              <ListGroup.Item>
                <strong>Monto Final:</strong> {selectedReservaDetail.montoFinal != null ? `$${Number(selectedReservaDetail.montoFinal).toLocaleString('es-CL')}` : 'N/A'}
              </ListGroup.Item>
            </ListGroup>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="outline-secondary" onClick={handleCloseDetailModal}>
              Cerrar
            </Button>
          </Modal.Footer>
        </Modal>
      )}

      {/* Out of Business Hours Modal (New) */}
      <Modal show={showOutOfHoursModal} onHide={handleCloseOutOfHoursModal} centered>
        <Modal.Header closeButton className="bg-warning text-dark">
          <Modal.Title>
            <i className="bi bi-exclamation-triangle-fill me-2"></i>
            {outOfHoursMessage.title}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {outOfHoursMessage.body.map((line, index) => (
            <p key={index} className={index === 2 ? "mt-3 mb-1 fw-bold" : "mb-1"}>
              {line}
            </p>
          ))}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="primary" onClick={handleCloseOutOfHoursModal}>
            Entendido
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default RackSemanal;