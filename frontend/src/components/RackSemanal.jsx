import React, { useState, useEffect } from 'react';
import ReserveService from '../services/ReserveService';
import { useNavigate } from 'react-router-dom';
import { 
  format, 
  startOfWeek,
  endOfWeek,
  addDays,
  addWeeks,
  subWeeks,
  isSameDay,
  parseISO
} from 'date-fns';
import { es } from 'date-fns/locale';
import './RackSemanal.css';


const RackSemanal = () => {
  const [reservas, setReservas] = useState([]);
  const [currentDate, setCurrentDate] = useState(new Date());
  const [currentWeek, setCurrentWeek] = useState({
    start: startOfWeek(new Date(), { weekStartsOn: 1 }),
    end: endOfWeek(new Date(), { weekStartsOn: 1 })
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  // Business hours configuration
  const businessHours = {
    weekday: { start: 14, end: 22 },
    weekend: { start: 10, end: 22 }
  };

  // Navigation functions
  const goToNextWeek = () => {
    const nextWeekStart = addWeeks(currentWeek.start, 1);
    setCurrentWeek({
      start: nextWeekStart,
      end: endOfWeek(nextWeekStart, { weekStartsOn: 1 })
    });
    setCurrentDate(nextWeekStart);
  };

  const goToPrevWeek = () => {
    const prevWeekStart = subWeeks(currentWeek.start, 1);
    setCurrentWeek({
      start: prevWeekStart,
      end: endOfWeek(prevWeekStart, { weekStartsOn: 1 })
    });
    setCurrentDate(prevWeekStart);
  };

  const goToCurrentWeek = () => {
    const today = new Date();
    setCurrentWeek({
      start: startOfWeek(today, { weekStartsOn: 1 }),
      end: endOfWeek(today, { weekStartsOn: 1 })
    });
    setCurrentDate(today);
  };

  // Generate days for the week
  const weekDays = [];
  for (let i = 0; i < 7; i++) {
    weekDays.push(addDays(currentWeek.start, i));
  }

  // Generate time slots based on business hours
  const generateTimeSlots = () => {
    const slots = [];
    // Start from the earliest opening time
    const startHour = businessHours.weekend.start;
    const endHour = businessHours.weekend.end;
    
    for (let hour = startHour; hour < endHour; hour++) {
      slots.push(`${hour.toString().padStart(2, '0')}:00`);
      slots.push(`${hour.toString().padStart(2, '0')}:30`);
    }
    return slots;
  };

  const timeSlots = generateTimeSlots();

  // Check if a given slot is within business hours for a specific day
  const isBusinessHours = (day, timeSlot) => {
    const [hour, minute] = timeSlot.split(':').map(Number);
    const isWeekend = [0, 6].includes(day.getDay()); // Sunday or Saturday
    const { start, end } = isWeekend ? businessHours.weekend : businessHours.weekday;
    
    return hour >= start && hour < end;
  };

  // Get reservation for specific date and time
  const getReservationsForSlot = (day, timeSlot) => {
    const [hour, minute] = timeSlot.split(':').map(Number);
    const slotDate = new Date(day);
    slotDate.setHours(hour, minute, 0, 0);
    
    return reservas.filter(reserva => {
      const startTime = new Date(reserva.inicio_reserva);
      const endTime = new Date(reserva.fin_reserva);
      
      return startTime <= slotDate && slotDate < endTime;
    });
  };

  // Format reservation type
  const formatTipoReserva = (tipo) => {
    switch(tipo) {
      case 1: return 'Normal';
      case 2: return 'Extendida';
      case 3: return 'Premium';
      default: return `Tipo ${tipo}`;
    }
  };

  // Handle adding a new reservation
  const handleAddReservation = (day, timeSlot) => {
    const [hour, minute] = timeSlot.split(':').map(Number);
    const reservationDate = new Date(day);
    reservationDate.setHours(hour, minute, 0, 0);
    
    navigate(`/agregar-reserva?date=${format(reservationDate, 'yyyy-MM-dd')}&time=${timeSlot}`);
  };

  // Handle viewing an existing reservation
  const handleViewReservation = (reservaId) => {
    navigate(`/reservas?id=${reservaId}`);
  };

  useEffect(() => {
    const fetchReservas = async () => {
      try {
        setLoading(true);
        const response = await ReserveService.getAllReserves();
        setReservas(Array.isArray(response.data) ? response.data : []);
        setLoading(false);
      } catch (err) {
        console.error('Error al cargar reservas:', err);
        setError('Error al cargar el rack semanal. Por favor, intente nuevamente.');
        setLoading(false);
      }
    };
    
    fetchReservas();
  }, [currentWeek]); // Refetch when the week changes

  if (loading) return <div className="loading-container"><div className="loading-spinner"></div></div>;
  if (error) return <div className="error-message">{error}</div>;

  return (
    <div className="rack-semanal">
      <div className="rack-header">
        <h2>Rack Semanal de Reservas</h2>
        
        <div className="week-display">
          {format(currentWeek.start, 'd MMM', { locale: es })} - {format(currentWeek.end, 'd MMM yyyy', { locale: es })}
        </div>
        
        <div className="week-navigation">
          <button onClick={goToPrevWeek} className="nav-button">
            &laquo; Semana Anterior
          </button>
          <button onClick={goToCurrentWeek} className="nav-button current">
            Semana Actual
          </button>
          <button onClick={goToNextWeek} className="nav-button">
            Semana Siguiente &raquo;
          </button>
        </div>
      </div>

      <div className="rack-container">
        <div className="time-column">
          <div className="day-header"></div>
          {timeSlots.map((slot, index) => (
            <div key={index} className="time-slot">
              {slot}
            </div>
          ))}
        </div>
        
        <div className="days-grid">
          {/* Day headers */}
          <div className="day-headers">
            {weekDays.map((day, index) => (
              <div 
                key={index} 
                className={`day-header ${isSameDay(day, new Date()) ? 'today' : ''}`}
              >
                <div className="day-name">{format(day, 'eee', { locale: es })}</div>
                <div className="day-date">{format(day, 'd', { locale: es })}</div>
              </div>
            ))}
          </div>
          
          {/* Time slots for each day */}
          <div className="time-slots-grid">
            {timeSlots.map((timeSlot, timeIndex) => (
              <div key={timeIndex} className="time-row">
                {weekDays.map((day, dayIndex) => {
                  const isOpen = isBusinessHours(day, timeSlot);
                  const reservationsForSlot = isOpen ? getReservationsForSlot(day, timeSlot) : [];
                  const hasReservation = reservationsForSlot.length > 0;
                  
                  return (
                    <div 
                      key={dayIndex}
                      className={`slot-cell ${!isOpen ? 'closed' : hasReservation ? 'reserved' : 'available'} ${isSameDay(day, new Date()) ? 'today' : ''}`}
                      onClick={() => {
                        if (isOpen && !hasReservation) {
                          handleAddReservation(day, timeSlot);
                        }
                      }}
                    >
                      {!isOpen ? (
                        <span className="closed-text">Cerrado</span>
                      ) : hasReservation ? (
                        reservationsForSlot.map((reserva, i) => (
                          <div 
                            key={i} 
                            className="reservation-card"
                            onClick={(e) => {
                              e.stopPropagation();
                              handleViewReservation(reserva.id);
                            }}
                          >
                            <div className="reservation-email">{reserva.emailarrendatario.split('@')[0]}</div>
                            <div className="reservation-type">{formatTipoReserva(reserva.tiporeserva)} • {reserva.numero_personas} pers.</div>
                          </div>
                        ))
                      ) : (
                        <span className="available-text">Disponible</span>
                      )}
                    </div>
                  );
                })}
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default RackSemanal;