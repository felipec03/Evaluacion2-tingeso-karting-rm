import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './ReserveList.css';
import ReserveService from '../services/ReserveService';

const ReserveList = () => {
  const [reservas, setReservas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchReservas = async () => {
      try {
        setLoading(true);
        // Usar la URL correcta con la ruta base adecuada
        const response = await ReserveService.getAllReserves();
        console.log('Respuesta API:', response.data);
        
        const reservasData = Array.isArray(response.data) 
          ? response.data 
          : response.data.content || response.data.reservas || [];
        
        setReservas(reservasData);
        setLoading(false);
      } catch (err) {
        console.error('Error al cargar reservas:', err);
        setError('Error al cargar las reservas. Por favor intente nuevamente.');
        setLoading(false);
      }
    };

    fetchReservas();
  }, []);

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('es-ES');
  };

  const formatTime = (startTime, endTime) => {
    if (!startTime || !endTime) return 'N/A';
    
    const start = new Date(startTime);
    const end = new Date(endTime);
    
    const formatOptions = { hour: '2-digit', minute: '2-digit', hour12: false };
    return `${start.toLocaleTimeString('es-ES', formatOptions)} - ${end.toLocaleTimeString('es-ES', formatOptions)}`;
  };
  
  // Función para convertir tipo de reserva a texto legible
  const getTipoReserva = (tipo) => {
    switch(tipo) {
      case 1: return 'Normal (10 vueltas)';
      case 2: return 'Extendida (15 vueltas)';
      case 3: return 'Premium (20 vueltas)';
      default: return `Tipo ${tipo}`;
    }
  };

  const handleDelete = (id) => {
    // Confirmar antes de eliminar
    if (window.confirm('¿Está seguro que desea eliminar esta reserva?')) {
      console.log(`Eliminando reserva con ID: ${id}`);
      ReserveService.deleteReserve(id)
        .then(() => {
          // Actualizar la lista de reservas después de eliminar
          setReservas(reservas.filter(reserva => reserva.id !== id));
        })
        .catch(err => {
          console.error('Error al eliminar reserva:', err);
          setError('Error al eliminar la reserva. Por favor intente nuevamente.');
        });
    }
  };

  const navigateToAddReserve = () => {
    navigate('/agregar-reserva');
  };

  if (loading) return <div className="loading">Cargando reservas...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="reservas-container">
      <h2>Lista de Reservas</h2>
      <div className="add-reserve-btn-container">
        <button className="add-reserve-btn" onClick={navigateToAddReserve}>
          Agregar Nueva Reserva
        </button>
      </div>
      
      {reservas.length === 0 ? (
        <div className="no-data">No hay reservas disponibles</div>
      ) : (
        <table className="reservas-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Cliente</th>
              <th>Tipo de Reserva</th>
              <th>Fecha</th>
              <th>Hora</th>
              <th>Personas</th>
              <th>Total</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {reservas.map((reserva) => (
              <tr key={reserva.id}>
                <td>{reserva.id}</td>
                <td>{reserva.emailarrendatario || 'N/A'}</td>
                <td>{getTipoReserva(reserva.tiporeserva)}</td>
                <td>{formatDate(reserva.fecha)}</td>
                <td>{formatTime(reserva.inicio_reserva, reserva.fin_reserva)}</td>
                <td>{reserva.numero_personas}</td>
                <td>${reserva.totalConIva?.toLocaleString('es-CL') || 'N/A'}</td>
                <td>
                  <button 
                    className="btn btn-delete" 
                    onClick={() => handleDelete(reserva.id)}
                  >
                    Eliminar
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default ReserveList;