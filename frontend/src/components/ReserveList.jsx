import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './ReserveList.css';
import ReserveService from '../services/ReserveService';

const ReserveList = () => {
  const [reservas, setReservas] = useState([]); // Changed back to reservas
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchReservas = async () => {
      try {
        setLoading(true);
        const response = await ReserveService.getAllReserves();
        console.log('Respuesta API (Reservas):', response.data);
        
        const data = Array.isArray(response.data) 
          ? response.data 
          : response.data.content || []; // Adjusted fallback
        
        setReservas(data);
        setLoading(false);
      } catch (err) {
        console.error('Error al cargar reservas:', err);
        setError('Error al cargar las reservas. Por favor intente nuevamente.');
        setLoading(false);
      }
    };

    fetchReservas();
  }, []);

  const formatDateTime = (dateTimeString) => {
    if (!dateTimeString) return 'N/A';
    const date = new Date(dateTimeString);
    return date.toLocaleString('es-ES', { 
      year: 'numeric', month: '2-digit', day: '2-digit',
      hour: '2-digit', minute: '2-digit' 
    });
  };

  const getTipoReservaDescription = (tipo) => {
    switch (tipo) {
      case 1: return '10 vueltas';
      case 2: return '15 vueltas';
      case 3: return '20 vueltas';
      default: return 'Desconocido';
    }
  };

  const handleDelete = (idReserva) => {
    if (window.confirm('¿Está seguro que desea eliminar esta reserva?')) {
      console.log(`Intentando eliminar reserva con ID: ${idReserva}`);
      ReserveService.deleteReserve(idReserva)
        .then(() => {
          setReservas(prevReservas => prevReservas.filter(reserva => reserva.id !== idReserva));
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
              <th>Fecha y Hora</th>
              <th>Tipo</th>
              <th>Personas</th>
              <th>Cliente</th>
              <th>Email</th>
              <th>Monto Final</th>
              <th>Estado</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {reservas.map((reserva) => (
              <tr key={reserva.id}>
                <td>{reserva.id}</td>
                <td>{formatDateTime(reserva.fechaHora)}</td>
                <td>{getTipoReservaDescription(reserva.tipoReserva)}</td>
                <td>{reserva.cantidadPersonas}</td>
                <td>{reserva.nombreUsuario || 'N/A'}</td>
                <td>{reserva.emailUsuario || 'N/A'}</td>
                <td>${reserva.montoFinal?.toLocaleString('es-CL') || 'N/A'}</td>
                <td>{reserva.estadoReserva || 'N/A'}</td>
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