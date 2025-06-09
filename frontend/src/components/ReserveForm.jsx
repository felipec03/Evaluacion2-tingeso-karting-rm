import React, { useState, useEffect } from 'react';
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
    nombreUsuario: '',
    rutUsuario: '',
    emailUsuario: '',
    telefonoUsuario: '',
    tipoReserva: 1,
    fecha: defaultDate,
    hora_inicio: defaultTime,
    cantidadPersonas: 1,
    cumpleanios: false,
    cantidadCumple: 0
  });

  // Removed priceInfo state
  // const [priceInfo, setPriceInfo] = useState(null);

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prevFormData => ({
      ...prevFormData,
      [name]: type === 'checkbox' ? checked : 
              (type === 'number' || name === 'tipoReserva' || name === 'cantidadPersonas' || name === 'cantidadCumple') && value !== '' ? parseInt(value) : value
    }));
  };

  const validateForm = () => {
    if (!formData.nombreUsuario) {
      setError('El nombre es obligatorio');
      return false;
    }
    if (!formData.rutUsuario) {
      setError('El RUT es obligatorio');
      return false;
    }
    if (!formData.emailUsuario) {
      setError('El email es obligatorio');
      return false;
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.emailUsuario)) {
      setError('El formato del email es inválido');
      return false;
    }
    if (!formData.telefonoUsuario) {
      setError('El teléfono es obligatorio');
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
    const selectedDate = new Date(formData.fecha + "T00:00:00");
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    if (selectedDate < today) {
      setError('La fecha no puede ser anterior a hoy');
      return false;
    }
    if (formData.cumpleanios && formData.cantidadCumple <= 0) {
      setError('Debe indicar cuántas personas cumplen años');
      return false;
    }
    if (formData.cumpleanios && formData.cantidadCumple > formData.cantidadPersonas) {
      setError('El número de personas de cumpleaños no puede ser mayor al total');
      return false;
    }
    setError(null);
    return true;
  };
  
  const prepareReservaData = () => {
    const fechaHoraISO = `${formData.fecha}T${formData.hora_inicio}:00`;

    return {
      nombreUsuario: formData.nombreUsuario,
      rutUsuario: formData.rutUsuario,
      emailUsuario: formData.emailUsuario,
      telefonoUsuario: formData.telefonoUsuario,
      fechaHora: fechaHoraISO,
      tipoReserva: parseInt(formData.tipoReserva),
      cantidadPersonas: parseInt(formData.cantidadPersonas),
      cantidadCumple: formData.cumpleanios ? parseInt(formData.cantidadCumple) : 0,
    };
  };

  // Removed calculatePrice function
  // const calculatePrice = async (e) => { ... };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;
    
    const reservaDataToSubmit = prepareReservaData();

    try {
      setLoading(true);
      setError(null); // Clear previous errors
      const response = await ReserveService.createReserve(reservaDataToSubmit);
      console.log('Reserva creada:', response.data);
      setSuccess(true);
      setLoading(false);
      setTimeout(() => {
        navigate(`/reservas`); 
      }, 2000);
    } catch (err) {
      console.error('Error al crear reserva:', err);
      setError(err.response?.data?.message || err.response?.data || 'Error al crear la reserva');
      setLoading(false);
    }
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
            <label htmlFor="nombreUsuario" className="form-label">Nombre Completo</label>
            <input
              type="text"
              className="form-control"
              id="nombreUsuario"
              name="nombreUsuario"
              value={formData.nombreUsuario}
              onChange={handleInputChange}
              required
            />
          </div>

          <div className="mb-3">
            <label htmlFor="rutUsuario" className="form-label">RUT (Ej: 12345678-9)</label>
            <input
              type="text"
              className="form-control"
              id="rutUsuario"
              name="rutUsuario"
              value={formData.rutUsuario}
              onChange={handleInputChange}
              required
            />
          </div>

          <div className="mb-3">
            <label htmlFor="emailUsuario" className="form-label">Email</label>
            <input
              type="email"
              className="form-control"
              id="emailUsuario"
              name="emailUsuario"
              value={formData.emailUsuario}
              onChange={handleInputChange}
              required
            />
          </div>

          <div className="mb-3">
            <label htmlFor="telefonoUsuario" className="form-label">Teléfono</label>
            <input
              type="tel"
              className="form-control"
              id="telefonoUsuario"
              name="telefonoUsuario"
              value={formData.telefonoUsuario}
              onChange={handleInputChange}
              required
            />
          </div>
          
          <div className="mb-3">
            <label htmlFor="tipoReserva" className="form-label">Tipo de Reserva</label>
            <select
              className="form-select"
              id="tipoReserva"
              name="tipoReserva"
              value={formData.tipoReserva}
              onChange={handleInputChange}
            >
              <option value={1}>10 vueltas</option>
              <option value={2}>15 vueltas</option>
              <option value={3}>20 vueltas</option>
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
            <label htmlFor="cantidadPersonas" className="form-label">Número de personas</label>
            <input
              type="number"
              className="form-control"
              id="cantidadPersonas"
              name="cantidadPersonas"
              min="1"
              max="20"
              value={formData.cantidadPersonas}
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
              <label htmlFor="cantidadCumple" className="form-label">¿Cuántas personas cumplen años?</label>
              <input
                type="number"
                className="form-control"
                id="cantidadCumple"
                name="cantidadCumple"
                min="1"
                max={formData.cantidadPersonas}
                value={formData.cantidadCumple}
                onChange={handleInputChange}
                required={formData.cumpleanios}
              />
            </div>
          )}
          
          <div className="d-flex justify-content-between mt-4">
            <button 
              type="button" 
              className="btn btn-secondary" 
              onClick={handleCancel}
              disabled={loading}
            >
              Cancelar
            </button>
            
            {/* Removed Calculate Price button */}
            
            <button 
              type="submit" 
              className="btn btn-primary"
              disabled={loading} // Only disable if loading
            >
              {loading ? 'Enviando...' : 'Crear Reserva'}
            </button>
          </div>
        </form>
      )}
      
      {/* Removed priceInfo display section */}
      {/* {priceInfo && ( ... )} */}
    </div>
  );
};

export default ReserveForm;