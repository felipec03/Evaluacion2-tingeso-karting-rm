import React, { useState, useEffect } from 'react';
import ComprobanteService from '../services/ComprobanteService';

const ComprobanteForm = ({ initialReservaId = '' }) => {
    const [reservaId, setReservaId] = useState(initialReservaId);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState(false);
    const [comprobanteDetails, setComprobanteDetails] = useState(null);

    // If initialReservaId is provided and not empty, automatically generate the comprobante
    useEffect(() => {
        if (initialReservaId && !success && !loading) {
            handleSubmit(new Event('submit'));
        }
    }, [initialReservaId]);

    const handleChange = (e) => {
        setReservaId(e.target.value);
    };
    
    const handleSubmit = (e) => {
        e.preventDefault();
        if (!reservaId || isNaN(reservaId) || reservaId <= 0) {
            setError('Por favor ingrese un ID de reserva válido');
            return;
        }

        setLoading(true);
        setError('');
        setSuccess(false);
        
        ComprobanteService.generateComprobante(reservaId)
            .then(response => {
                setComprobanteDetails(response.data);
                setSuccess(true);
                setLoading(false);
            })
            .catch(err => {
                setError('Error al generar el comprobante: ' + (err.response?.data || err.message));
                setLoading(false);
            });
    };
    
    const downloadPdf = () => {
        if (!reservaId) return;
        
        setLoading(true);
        setError('');
        
        ComprobanteService.downloadComprobantePdf(reservaId)
            .then(response => {
                // Check if the response is a PDF (application/pdf) or an error message
                const contentType = response.headers['content-type'];
                
                if (contentType && contentType.includes('application/pdf')) {
                    // Process as PDF
                    const url = window.URL.createObjectURL(new Blob([response.data]));
                    const link = document.createElement('a');
                    link.href = url;
                    
                    // Get filename from content-disposition header or use a default
                    const contentDisposition = response.headers['content-disposition'];
                    let filename = `comprobante-reserva-${reservaId}.pdf`;
                    
                    if (contentDisposition) {
                        const filenameMatch = contentDisposition.match(/filename="(.+)"/);
                        if (filenameMatch && filenameMatch.length > 1) {
                            filename = filenameMatch[1];
                        }
                    }
                    
                    link.setAttribute('download', filename);
                    document.body.appendChild(link);
                    link.click();
                    link.remove();
                    
                    // Clean up the URL object
                    setTimeout(() => window.URL.revokeObjectURL(url), 100);
                } else {
                    // Handle as error message
                    const reader = new FileReader();
                    reader.onload = () => {
                        setError('Error: ' + reader.result);
                    };
                    reader.readAsText(new Blob([response.data]));
                }
            })
            .catch(err => {
                console.error('Error downloading PDF:', err);
                let errorMessage = 'Error al descargar el PDF';
                
                if (err.response) {
                    if (err.response.data instanceof Blob) {
                        // Try to read the blob as text
                        const reader = new FileReader();
                        reader.onload = () => {
                            setError(errorMessage + ': ' + reader.result);
                        };
                        reader.readAsText(err.response.data);
                    } else {
                        setError(errorMessage + ': ' + (err.response.data || err.message));
                    }
                } else {
                    setError(errorMessage + ': ' + err.message);
                }
            })
            .finally(() => {
                setLoading(false);
            });
    };

    const resetForm = () => {
        setReservaId('');
        setSuccess(false);
        setError('');
        setComprobanteDetails(null);
    };
    
    return (
        <div className="container">
            <div className="card shadow-sm">
                <div className="card-body">
                    <h3 className="card-title mb-4">Generador de Comprobantes</h3>
                    
                    {error && (
                        <div className="alert alert-danger" role="alert">
                            {error}
                        </div>
                    )}
                    
                    {!success ? (
                        <form onSubmit={handleSubmit}>
                            <div className="mb-3">
                                <label htmlFor="reservaId" className="form-label">
                                    ID de la Reserva
                                </label>
                                <input
                                    type="number"
                                    className="form-control"
                                    id="reservaId"
                                    value={reservaId}
                                    onChange={handleChange}
                                    min="1"
                                    required
                                    placeholder="Ingrese el ID de la reserva"
                                />
                                <div className="form-text">
                                    Ingrese el número de identificación de la reserva para generar su comprobante
                                </div>
                            </div>
                            
                            <button
                                type="submit"
                                className="btn btn-primary"
                                disabled={loading}
                            >
                                {loading ? (
                                    <>
                                        <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                                        Generando...
                                    </>
                                ) : 'Generar Comprobante'}
                            </button>
                        </form>
                    ) : (
                        <div>
                            <div className="alert alert-success" role="alert">
                                <h4 className="alert-heading">¡Comprobante generado correctamente!</h4>
                                <p>Se ha generado el comprobante para la reserva #{reservaId}</p>
                            </div>
                            
                            {comprobanteDetails && (
                                <div className="card mb-3 bg-light">
                                    <div className="card-body">
                                        <h5 className="card-title">Detalles del Comprobante</h5>
                                        <p><strong>Código:</strong> {comprobanteDetails.codigo}</p>
                                        <p><strong>Email:</strong> {comprobanteDetails.email}</p>
                                        <p><strong>Tarifa Base:</strong> ${comprobanteDetails.tarifaBase.toLocaleString()}</p>
                                        <p><strong>Precio sin IVA:</strong> ${comprobanteDetails.precioSinIva.toLocaleString()}</p>
                                        <p><strong>IVA:</strong> ${comprobanteDetails.iva.toLocaleString()}</p>
                                        <p><strong>Total:</strong> ${comprobanteDetails.total.toLocaleString()}</p>
                                        
                                        {(comprobanteDetails.descuentoGrupo > 0 || 
                                          comprobanteDetails.descuentoFrecuente > 0 || 
                                          comprobanteDetails.descuentoCumple > 0) && (
                                            <div>
                                                <h6 className="mt-3">Descuentos aplicados:</h6>
                                                {comprobanteDetails.descuentoGrupo > 0 && (
                                                    <p>Descuento por grupo: ${comprobanteDetails.descuentoGrupo.toLocaleString()}</p>
                                                )}
                                                {comprobanteDetails.descuentoFrecuente > 0 && (
                                                    <p>Descuento cliente frecuente: ${comprobanteDetails.descuentoFrecuente.toLocaleString()}</p>
                                                )}
                                                {comprobanteDetails.descuentoCumple > 0 && (
                                                    <p>Descuento por cumpleaños: ${comprobanteDetails.descuentoCumple.toLocaleString()}</p>
                                                )}
                                            </div>
                                        )}
                                    </div>
                                </div>
                            )}
                            
                            <div className="d-flex gap-2">
                                <button 
                                    className="btn btn-primary" 
                                    onClick={downloadPdf}
                                    disabled={loading}
                                >
                                    {loading ? (
                                        <>
                                            <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                                            Descargando...
                                        </>
                                    ) : 'Descargar PDF'}
                                </button>
                                <button 
                                    className="btn btn-outline-secondary" 
                                    onClick={resetForm}
                                >
                                    Generar otro comprobante
                                </button>
                            </div>
                        </div>
                    )}
                </div>
            </div>
            
            <div className="mt-4">
                <div className="alert alert-info">
                    <h5 className="alert-heading">¿Dónde encuentro el ID de mi reserva?</h5>
                    <p>Puede encontrar el ID de su reserva en:</p>
                    <ul>
                        <li>El listado de reservas en su perfil</li>
                        <li>El correo electrónico de confirmación de su reserva</li>
                        <li>Contactando directamente con servicio al cliente</li>
                    </ul>
                </div>
            </div>
        </div>
    );
};

export default ComprobanteForm;