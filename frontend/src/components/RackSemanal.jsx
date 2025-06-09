import React, { useState, useEffect } from 'react';
import RackSemanalService from '../services/RackSemanalService';
import { useNavigate } from 'react-router-dom';
import { 
  format, 
  startOfWeek,
  endOfWeek,
  addDays,
  addWeeks,
  subWeeks,
  isSameDay
} from 'date-fns';
import { es } from 'date-fns/locale';
import './RackSemanal.css';

const DIAS_SEMANA_ORDER = ["Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"];

const BLOQUES_TIEMPO_DEFINIDOS = [
    "09:00-10:00", "10:00-11:00", "11:00-12:00", "12:00-13:00",
    "13:00-14:00", "14:00-15:00", "15:00-16:00", "16:00-17:00",
    "17:00-18:00", "18:00-19:00", "19:00-20:00"
];

const RackSemanal = () => {
  const [rackMatriz, setRackMatriz] = useState({}); // Stores Map<String, Map<String, RackCeldaDTO>>
  const [currentDate, setCurrentDate] = useState(new Date()); // Keep for potential future use, though currentWeek drives display
  const [currentWeek, setCurrentWeek] = useState({
    start: startOfWeek(new Date(), { weekStartsOn: 1 }),
    end: endOfWeek(new Date(), { weekStartsOn: 1 })
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const [reservaDetalles, setReservaDetalles] = useState({});

  useEffect(() => {
    const fetchRackData = async () => {
      try {
        setLoading(true);
        setError(null);
        // Optional: Call actualizarRack before fetching matrix if real-time updates are critical
        // await RackSemanalService.actualizarRack();
        const response = await RackSemanalService.obtenerMatrizRack();
        setRackMatriz(response.data || {});
      } catch (err) {
        console.error('Error al cargar el rack semanal:', err);
        setError('Error al cargar el rack semanal. Por favor, intente nuevamente.');
        setRackMatriz({}); // Ensure it's an object on error
      } finally {
        setLoading(false);
      }
    };
    
    fetchRackData();
    // The backend matrix is for the whole week structure, not tied to a specific date range from client.
    // If you want to fetch data for *specific weeks* (e.g. past/future beyond current week's structure),
    // the backend would need to support date parameters. For now, it's a generic weekly template.
    // Re-fetching on currentWeek change might be redundant if the backend always returns the same structure
    // and updates it based on *all* reservations.
    // However, if `actualizarRack` is called frequently, re-fetching ensures fresh data.
  }, [currentWeek]); // Consider if re-fetch on week change is truly needed or if an explicit refresh button is better.

  const weekDays = [];
  for (let i = 0; i < 7; i++) {
    weekDays.push(addDays(currentWeek.start, i));
  }

  const timeSlots = BLOQUES_TIEMPO_DEFINIDOS;

  const getDiaSemanaString = (date) => {
    const dayName = format(date, 'EEEE', { locale: es });
    return dayName.charAt(0).toUpperCase() + dayName.slice(1);
  };
  
  const handleAddReservation = (day, timeSlot) => {
    const dateStr = format(day, 'yyyy-MM-dd');
    const startTime = timeSlot.split('-')[0];
    navigate(`/agregar-reserva?date=${dateStr}&time=${startTime}`);
  };

  const handleViewReservation = async (reservaId) => {
    if (!reservaId) return;
    if (reservaDetalles[reservaId]) { // Check cache first
        alert(`Reserva ID: ${reservaDetalles[reservaId].id}\nCliente: ${reservaDetalles[reservaId].nombreUsuario}\nEmail: ${reservaDetalles[reservaId].emailUsuario}\nTipo: ${formatTipoReserva(reservaDetalles[reservaId].tipoReserva)}`);
        return;
    }
    try {
        setLoading(true); 
        const response = await RackSemanalService.obtenerDetallesReserva(reservaId);
        if (response.data) {
            setReservaDetalles(prev => ({...prev, [reservaId]: response.data})); 
            alert(`Reserva ID: ${response.data.id}\nCliente: ${response.data.nombreUsuario}\nEmail: ${response.data.emailUsuario}\nTipo: ${formatTipoReserva(response.data.tipoReserva)}`);
        } else {
            setError(`No se pudieron cargar los detalles de la reserva ${reservaId}.`);
        }
    } catch (err) {
        console.error(`Error al obtener detalles de la reserva ${reservaId}:`, err);
        setError(`No se pudieron cargar los detalles de la reserva ${reservaId}.`);
    } finally {
        setLoading(false);
    }
  };
  
  const formatTipoReserva = (tipo) => {
    switch(tipo) {
      case 1: return '10 vueltas';
      case 2: return '15 vueltas';
      case 3: return '20 vueltas';
      default: return `Tipo ${tipo}`;
    }
  };

  const goToNextWeek = () => {
    const nextWeekStart = addWeeks(currentWeek.start, 1);
    setCurrentWeek({ start: nextWeekStart, end: endOfWeek(nextWeekStart, { weekStartsOn: 1 }) });
  };
  const goToPrevWeek = () => {
    const prevWeekStart = subWeeks(currentWeek.start, 1);
    setCurrentWeek({ start: prevWeekStart, end: endOfWeek(prevWeekStart, { weekStartsOn: 1 }) });
  };
  const goToCurrentWeek = () => {
    const today = new Date();
    setCurrentWeek({ start: startOfWeek(today, { weekStartsOn: 1 }), end: endOfWeek(today, { weekStartsOn: 1 }) });
  };

  if (loading && Object.keys(rackMatriz).length === 0) return <div className="loading-container"><div className="loading-spinner"></div></div>;
  if (error) return <div className="error-message">{error}</div>;
  if (Object.keys(rackMatriz).length === 0 && !loading) return <div className="error-message">No hay datos del rack para mostrar. Intente inicializar o actualizar.</div>;


  return (
    <div className="rack-semanal">
      <div className="rack-header">
        <h2>Rack Semanal de Reservas</h2>
        <div className="week-display">
          {format(currentWeek.start, 'd MMM', { locale: es })} - {format(currentWeek.end, 'd MMM yyyy', { locale: es })}
        </div>
        <div className="week-navigation">
          <button onClick={goToPrevWeek} className="nav-button">&laquo; Semana Anterior</button>
          <button onClick={goToCurrentWeek} className="nav-button current">Semana Actual</button>
          <button onClick={goToNextWeek} className="nav-button">Semana Siguiente &raquo;</button>
        </div>
      </div>

      <div className="rack-container">
        <div className="time-column">
          <div className="day-header" style={{ height: '62px' }}></div> {/* Adjusted for alignment */}
          {timeSlots.map((slot, index) => (
            <div key={index} className="time-slot">
              {slot.split('-')[0]}
            </div>
          ))}
        </div>
        
        <div className="days-grid">
          <div className="day-headers">
            {weekDays.map((day, index) => (
              <div key={index} className={`day-header ${isSameDay(day, new Date()) ? 'today' : ''}`}>
                <div className="day-name">{format(day, 'eee', { locale: es })}</div>
                <div className="day-date">{format(day, 'd', { locale: es })}</div>
              </div>
            ))}
          </div>
          
          <div className="time-slots-grid">
            {timeSlots.map((timeSlot, timeIndex) => (
              <div key={timeIndex} className="time-row">
                {weekDays.map((day, dayIndex) => {
                  const diaSemanaStr = getDiaSemanaString(day);
                  const celdaInfo = rackMatriz[diaSemanaStr] ? rackMatriz[diaSemanaStr][timeSlot] : null;
                  
                  const isReserved = celdaInfo ? celdaInfo.reservado : false;
                  const reservaId = celdaInfo ? celdaInfo.reservaId : null;
                  // Assuming all defined slots are "open" unless explicitly marked otherwise by backend (not currently supported by DTO)
                  const isOpen = true; 
                  const currentReservaDetalle = reservaId ? reservaDetalles[reservaId] : null;

                  return (
                    <div 
                      key={`${dayIndex}-${timeIndex}`}
                      className={`slot-cell ${!isOpen ? 'closed' : isReserved ? 'reserved' : 'available'} ${isSameDay(day, new Date()) ? 'today' : ''}`}
                      onClick={() => {
                        if (isOpen && !isReserved) {
                          handleAddReservation(day, timeSlot);
                        } else if (isReserved && reservaId) {
                          handleViewReservation(reservaId);
                        }
                      }}
                    >
                      {!isOpen ? (
                        <span className="closed-text">Cerrado</span>
                      ) : isReserved ? (
                        <div className="reservation-card">
                           <div className="reservation-email">
                            {currentReservaDetalle ? currentReservaDetalle.nombreUsuario || currentReservaDetalle.emailUsuario?.split('@')[0] : (reservaId ? `ID: ${reservaId}`: 'Reservado')}
                           </div>
                           <div className="reservation-type">
                            {currentReservaDetalle ? `${formatTipoReserva(currentReservaDetalle.tipoReserva)} • ${currentReservaDetalle.cantidadPersonas}p` : (loading && reservaId ? 'Cargando...' : '')}
                           </div>
                        </div>
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