import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import ComprobanteForm from './ComprobanteForm';

const ReportView = () => {
    const [searchParams] = useSearchParams();
    const [activeTab, setActiveTab] = useState('comprobantes');
    const [reservaId, setReservaId] = useState('');
    
    useEffect(() => {
        // Check if there's a reservaId in the URL
        const urlReservaId = searchParams.get('reservaId');
        if (urlReservaId) {
            setReservaId(urlReservaId);
            setActiveTab('comprobantes');
        }
    }, [searchParams]);

    return (
        <div className="container mt-4">
            <h2>Reportes y Documentos</h2>
            
            <ul className="nav nav-tabs mb-4">
                <li className="nav-item">
                    <button 
                        className={`nav-link ${activeTab === 'comprobantes' ? 'active' : ''}`}
                        onClick={() => setActiveTab('comprobantes')}
                    >
                        Generación de Comprobantes
                    </button>
                </li>
                <li className="nav-item">
                    <button 
                        className={`nav-link ${activeTab === 'reports' ? 'active' : ''}`}
                        onClick={() => setActiveTab('reports')}
                    >
                        Reportes Estadísticos
                    </button>
                </li>
            </ul>
            
            {activeTab === 'comprobantes' ? (
                <ComprobanteForm initialReservaId={reservaId} />
            ) : (
                <div className="card">
                    <div className="card-body">
                        <h3>Reportes Estadísticos</h3>
                        <p>Esta sección está en desarrollo.</p>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ReportView;