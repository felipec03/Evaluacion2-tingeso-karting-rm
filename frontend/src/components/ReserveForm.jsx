import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import './ReserveForm.css';
import ReserveService from '../services/ReserveService';

const ReserveForm = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);
  
  const queryParams = new URLSearchParams(location.search);
  const defaultDate = queryParams.get('date') || '';
  const defaultTime = queryParams.get('time') || '';

  const [formData, setFormData] = useState({
    emailarrendatario: '',
    tiporeserva: 1,
    fecha: defaultDate,
    hora_inicio: defaultTime,
    numero_personas: 1,
    cumpleanios: false,
    cantidadcumple: 0
  });

  const [priceInfo, setPriceInfo] = useState(null);

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData({
      ...formData,
      [name]: type === 'checkbox' ? checked : 
              type === 'number' ? parseInt(value) : value
    });
  };

  const calculateReservationEnd = () => {
    if (!formData.fecha || !formData.hora_inicio) return '';
    
    // Calculate end time based on reservation type
    const duration = formData.tiporeserva === 1 ? 1 : 
                     formData.tiporeserva === 2 ? 1.5 : 2;
                     
    const [hours, minutes] = formData.hora_inicio.split(':');
    const startDate = new Date();
    startDate.setHours(parseInt(hours));
    startDate.setMinutes(parseInt(minutes));
    
    const endDate = new Date(startDate.getTime() + duration * 60 * 60 * 1000);
    const endHours = endDate.getHours().toString().padStart(2, '0');
    const endMinutes = endDate.getMinutes().toString().padStart(2, '0');
    
    return `${endHours}:${endMinutes}`;
  };

  const calculatePrice = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }
    
    try {
      setLoading(true);
      
      // Create datetime objects for API request
      const fechaStr = formData.fecha;
      const inicioStr = `${fechaStr}T${formData.hora_inicio}:00`;
      
      // Calculate end time
      const endTime = calculateReservationEnd();
      const finStr = `${fechaStr}T${endTime}:00`;
      
      const reservaData = {
        emailarrendatario: formData.emailarrendatario,
        tiporeserva: formData.tiporeserva,
        inicio_reserva: inicioStr,
        fin_reserva: finStr,
        numero_personas: formData.numero_personas,
        cumpleanios: formData.cumpleanios ? new Date() : null,
        cantidadcumple: formData.cumpleanios ? formData.cantidadcumple : 0
      };
      
      // Calculate price without saving
      const response = await ReserveService.calculatePrice(reservaData);
      setPriceInfo(response.data);
      setLoading(false);
    } catch (err) {
      console.error('Error al calcular precio:', err);
      setError(err.response?.data || 'Error al calcular el precio de la reserva');
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }
    
    try {
      setLoading(true);
      
      // Create datetime objects for API request
      const fechaStr = formData.fecha;
      const inicioStr = `${fechaStr}T${formData.hora_inicio}:00`;
      
      // Calculate end time
      const endTime = calculateReservationEnd();
      const finStr = `${fechaStr}T${endTime}:00`;
      
      const reservaData = {
        emailarrendatario: formData.emailarrendatario,
        tiporeserva: formData.tiporeserva,
        inicio_reserva: inicioStr,
        fin_reserva: finStr,
        numero_personas: formData.numero_personas,
        cumpleanios: formData.cumpleanios ? new Date() : null,
        cantidadcumple: formData.cumpleanios ? formData.cantidadcumple : 0
      };
      
      const response = await ReserveService.createReserve(reservaData);
      console.log('Reserva creada:', response.data);
      
      setSuccess(true);
      setLoading(false);
      
      // Redirect to reservation details after short delay
      setTimeout(() => {
        navigate(`/reservas?id=${response.data.id}`);
      }, 2000);
      
    } catch (err) {
      console.error('Error al crear reserva:', err);
      setError(err.response?.data || 'Error al crear la reserva');
      setLoading(false);
    }
  };
  
  const validateForm = () => {
    if (!formData.emailarrendatario) {
      setError('El email es obligatorio');
      return false;
    }
    
    if (!formData.fecha) {
      setError('La fecha es obligatoria');
      return false;
    }
    
    if (!formData.hora_inicio) {
      setError('La hora de inicio es obligatoria');
      return false;
    }
    
    // Validar formato de email
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.emailarrendatario)) {
      setError('El formato del email es inválido');
      return false;
    }
    
    // Validar que la fecha no sea anterior a hoy
    const selectedDate = new Date(formData.fecha);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    if (selectedDate < today) {
      setError('La fecha no puede ser anterior a hoy');
      return false;
    }
    
    if (formData.cumpleanios && formData.cantidadcumple <= 0) {
      setError('Debe indicar cuántas personas cumplen años');
      return false;
    }
    
    if (formData.cumpleanios && formData.cantidadcumple > formData.numero_personas) {
      setError('El número de personas de cumpleaños no puede ser mayor al total');
      return false;
    }
    
    setError(null);
    return true;
  };

  const handleCancel = () => {
    navigate('/reservas');
  };

  return (
    <div className="reserve-form-container container mt-4">
      <h2>Nueva Reserva</h2>
      
      {success ? (
        <div className="alert alert-success">
          Reserva creada con éxito! Redirigiendo...
        </div>
      ) : (
        <form onSubmit={handleSubmit} className="form-card">
          {error && <div className="alert alert-danger">{error}</div>}
          
          <div className="mb-3">
            <label htmlFor="emailarrendatario" className="form-label">Email</label>
            <input
              type="email"
              className="form-control"
              id="emailarrendatario"
              name="emailarrendatario"
              value={formData.emailarrendatario}
              onChange={handleInputChange}
              required
            />
          </div>
          
          <div className="mb-3">
            <label htmlFor="tiporeserva" className="form-label">Tipo de Reserva</label>
            <select
              className="form-select"
              id="tiporeserva"
              name="tiporeserva"
              value={formData.tiporeserva}
              onChange={handleInputChange}
            >
              <option value={1}>Normal (10 vueltas)</option>
              <option value={2}>Extendida (15 vueltas)</option>
              <option value={3}>Premium (20 vueltas)</option>
            </select>
          </div>
          
          <div className="row mb-3">
            <div className="col">
              <label htmlFor="fecha" className="form-label">Fecha</label>
              <input
                type="date"
                className="form-control"
                id="fecha"
                name="fecha"
                value={formData.fecha}
                onChange={handleInputChange}
                required
              />
            </div>
            
            <div className="col">
              <label htmlFor="hora_inicio" className="form-label">Hora de inicio</label>
              <input
                type="time"
                className="form-control"
                id="hora_inicio"
                name="hora_inicio"
                value={formData.hora_inicio}
                onChange={handleInputChange}
                required
              />
            </div>
          </div>
          
          <div className="mb-3">
            <label htmlFor="numero_personas" className="form-label">Número de personas</label>
            <input
              type="number"
              className="form-control"
              id="numero_personas"
              name="numero_personas"
              min="1"
              max="15"
              value={formData.numero_personas}
              onChange={handleInputChange}
              required
            />
          </div>
          
          <div className="mb-3 form-check">
            <input
              type="checkbox"
              className="form-check-input"
              id="cumpleanios"
              name="cumpleanios"
              checked={formData.cumpleanios}
              onChange={handleInputChange}
            />
            <label className="form-check-label" htmlFor="cumpleanios">¿Incluye personas de cumpleaños?</label>
          </div>
          
          {formData.cumpleanios && (
            <div className="mb-3">
              <label htmlFor="cantidadcumple" className="form-label">¿Cuántas personas cumplen años?</label>
              <input
                type="number"
                className="form-control"
                id="cantidadcumple"
                name="cantidadcumple"
                min="1"
                max={formData.numero_personas}
                value={formData.cantidadcumple}
                onChange={handleInputChange}
                required
              />
            </div>
          )}
          
          <div className="d-flex justify-content-between mt-4">
            <button 
              type="button" 
              className="btn btn-secondary" 
              onClick={handleCancel}
            >
              Cancelar
            </button>
            
            <button 
              type="button" 
              className="btn btn-info"
              onClick={calculatePrice}
              disabled={loading}
            >
              {loading ? 'Calculando...' : 'Calcular Precio'}
            </button>
            
            <button 
              type="submit" 
              className="btn btn-primary"
              disabled={loading}
            >
              {loading ? 'Enviando...' : 'Crear Reserva'}
            </button>
          </div>
        </form>
      )}
      
      {priceInfo && (
        <div className="price-info-card mt-4">
          <h4>Detalles de Precio</h4>
          <table className="table">
            <tbody>
              <tr>
                <td>Precio Base</td>
                <td>${priceInfo.precioBase?.toLocaleString('es-CL')}</td>
              </tr>
              {priceInfo.descuentoGrupo > 0 && (
                <tr>
                  <td>Descuento por Grupo</td>
                  <td>-${priceInfo.descuentoGrupo?.toLocaleString('es-CL')}</td>
                </tr>
              )}
              {priceInfo.descuentoFrecuente > 0 && (
                <tr>
                  <td>Descuento Cliente Frecuente</td>
                  <td>-${priceInfo.descuentoFrecuente?.toLocaleString('es-CL')}</td>
                </tr>
              )}
              {priceInfo.descuentoCumple > 0 && (
                <tr>
                  <td>Descuento por Cumpleaños</td>
                  <td>-${priceInfo.descuentoCumple?.toLocaleString('es-CL')}</td>
                </tr>
              )}
              <tr>
                <td>Subtotal</td>
                <td>${priceInfo.totalSinIva?.toLocaleString('es-CL')}</td>
              </tr>
              <tr>
                <td>IVA (19%)</td>
                <td>${priceInfo.iva?.toLocaleString('es-CL')}</td>
              </tr>
              <tr className="table-active">
                <th>Total a Pagar</th>
                <th>${priceInfo.totalConIva?.toLocaleString('es-CL')}</th>
              </tr>
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default ReserveForm;