import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import ComprobanteForm from './ComprobanteForm';
import ReporteService from '../services/ReporteService'; // Import the new service
import { Bar } from 'react-chartjs-2'; // Import Bar chart
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    BarElement,
    Title,
    Tooltip,
    Legend,
} from 'chart.js';

// Register Chart.js components
ChartJS.register(
    CategoryScale,
    LinearScale,
    BarElement,
    Title,
    Tooltip,
    Legend
);

const ReportView = () => {
    const [searchParams] = useSearchParams();
    const [activeTab, setActiveTab] = useState('comprobantes');
    const [reservaId, setReservaId] = useState('');

    // State for reports
    const [reportData, setReportData] = useState(null);
    const [loadingReport, setLoadingReport] = useState(false);
    const [reportError, setReportError] = useState(null);
    const [fechaInicio, setFechaInicio] = useState('2023-01-01');
    const [fechaFin, setFechaFin] = useState(new Date().toISOString().split('T')[0]); // Default to today
    const [currentReportType, setCurrentReportType] = useState('');


    useEffect(() => {
        const urlReservaId = searchParams.get('reservaId');
        if (urlReservaId) {
            setReservaId(urlReservaId);
            setActiveTab('comprobantes');
        }
    }, [searchParams]);

    const handleFetchReport = async (reportType) => {
        setLoadingReport(true);
        setReportError(null);
        setReportData(null);
        setCurrentReportType(reportType);

        try {
            let response;
            if (reportType === 'ingresos-por-tipo-reserva') {
                response = await ReporteService.getIngresosPorTipoReserva(fechaInicio, fechaFin);
            } else if (reportType === 'ingresos-por-numero-personas') {
                response = await ReporteService.getIngresosPorNumeroPersonas(fechaInicio, fechaFin);
            }
            setReportData(response.data);
        } catch (error) {
            console.error(`Error fetching ${reportType}:`, error);
            setReportError("No se pudo cargar los datos"); // Updated error message
        } finally {
            setLoadingReport(false);
        }
    };

    const getChartData = () => {
        if (!reportData || reportData.length === 0) {
            return { labels: [], datasets: [] };
        }

        const allMonths = new Set();
        reportData.forEach(item => {
            Object.keys(item.ingresosPorMes).forEach(month => allMonths.add(month));
        });
        const sortedMonths = Array.from(allMonths).sort();

        const datasets = reportData.map((item, index) => {
            const colors = [
                'rgba(75, 192, 192, 0.6)',
                'rgba(255, 99, 132, 0.6)',
                'rgba(54, 162, 235, 0.6)',
                'rgba(255, 206, 86, 0.6)',
                'rgba(153, 102, 255, 0.6)',
                'rgba(255, 159, 64, 0.6)'
            ];
            return {
                label: item.categoria,
                data: sortedMonths.map(month => item.ingresosPorMes[month] || 0),
                backgroundColor: colors[index % colors.length],
                borderColor: colors[index % colors.length].replace('0.6', '1'),
                borderWidth: 1,
            };
        });

        return {
            labels: sortedMonths,
            datasets: datasets,
        };
    };

    const chartOptions = {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: {
                position: 'top',
            },
            title: {
                display: true,
                text: `Reporte de Ingresos: ${currentReportType.replace(/-/g, ' ')}`,
                font: {
                    size: 16
                }
            },
            tooltip: {
                callbacks: {
                    label: function(context) {
                        let label = context.dataset.label || '';
                        if (label) {
                            label += ': ';
                        }
                        if (context.parsed.y !== null) {
                            label += new Intl.NumberFormat('es-CL', { style: 'currency', currency: 'CLP' }).format(context.parsed.y);
                        }
                        return label;
                    }
                }
            }
        },
        scales: {
            y: {
                beginAtZero: true,
                ticks: {
                    callback: function(value) {
                        return new Intl.NumberFormat('es-CL', { style: 'currency', currency: 'CLP', minimumFractionDigits: 0 }).format(value);
                    }
                }
            },
        },
    };
    
    const renderReportTable = () => {
        if (!reportData || reportData.length === 0) {
            return <p className="text-muted">No hay datos para mostrar.</p>;
        }

        const allMonths = new Set();
        reportData.forEach(item => {
            Object.keys(item.ingresosPorMes).forEach(month => allMonths.add(month));
        });
        const sortedMonths = Array.from(allMonths).sort();

        return (
            <div className="table-responsive mt-4">
                <table className="table table-striped table-hover table-bordered">
                    <thead className="table-dark">
                        <tr>
                            <th>Categoría</th>
                            {sortedMonths.map(month => <th key={month} className="text-end">{month}</th>)}
                            <th className="text-end">Total Categoría</th>
                        </tr>
                    </thead>
                    <tbody>
                        {reportData.map((item, index) => {
                            const totalCategoria = sortedMonths.reduce((sum, month) => sum + (item.ingresosPorMes[month] || 0), 0);
                            return (
                                <tr key={index}>
                                    <td>{item.categoria}</td>
                                    {sortedMonths.map(month => (
                                        <td key={month} className="text-end">
                                            {new Intl.NumberFormat('es-CL', { style: 'currency', currency: 'CLP' }).format(item.ingresosPorMes[month] || 0)}
                                        </td>
                                    ))}
                                    <td className="text-end fw-bold">
                                        {new Intl.NumberFormat('es-CL', { style: 'currency', currency: 'CLP' }).format(totalCategoria)}
                                    </td>
                                </tr>
                            );
                        })}
                    </tbody>
                    <tfoot className="table-light">
                        <tr>
                            <th className="fw-bold">Total Mensual</th>
                            {sortedMonths.map(month => {
                                const totalMes = reportData.reduce((sum, item) => sum + (item.ingresosPorMes[month] || 0), 0);
                                return (
                                    <th key={`total-${month}`} className="text-end fw-bold">
                                        {new Intl.NumberFormat('es-CL', { style: 'currency', currency: 'CLP' }).format(totalMes)}
                                    </th>
                                );
                            })}
                            <th className="text-end fw-bolder">
                                {new Intl.NumberFormat('es-CL', { style: 'currency', currency: 'CLP' }).format(
                                    reportData.reduce((sum, item) => sum + Object.values(item.ingresosPorMes).reduce((s, v) => s + (v || 0), 0), 0)
                                )}
                            </th>
                        </tr>
                    </tfoot>
                </table>
            </div>
        );
    };


    return (
        <div className="container mt-4">
            <h2>Reportes y Documentos</h2>
            
            <ul className="nav nav-tabs mb-4">
                <li className="nav-item">
                    <button 
                        className={`nav-link ${activeTab === 'comprobantes' ? 'active' : ''}`}
                        onClick={() => { setActiveTab('comprobantes'); setReportData(null); setReportError(null);}}
                    >
                        Generación de Comprobantes
                    </button>
                </li>
                <li className="nav-item">
                    <button 
                        className={`nav-link ${activeTab === 'reports' ? 'active' : ''}`}
                        onClick={() => { setActiveTab('reports'); setReportData(null); setReportError(null);}}
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
                        
                        <div className="row g-3 align-items-end mb-4 p-3 border rounded bg-light">
                            <div className="col-md-3">
                                <label htmlFor="fechaInicio" className="form-label">Fecha Inicio:</label>
                                <input 
                                    type="date" 
                                    id="fechaInicio"
                                    className="form-control" 
                                    value={fechaInicio} 
                                    onChange={(e) => setFechaInicio(e.target.value)} 
                                />
                            </div>
                            <div className="col-md-3">
                                <label htmlFor="fechaFin" className="form-label">Fecha Fin:</label>
                                <input 
                                    type="date" 
                                    id="fechaFin"
                                    className="form-control" 
                                    value={fechaFin} 
                                    onChange={(e) => setFechaFin(e.target.value)} 
                                />
                            </div>
                            <div className="col-md-3">
                                <button 
                                    className="btn btn-primary w-100" 
                                    onClick={() => handleFetchReport('ingresos-por-tipo-reserva')}
                                    disabled={loadingReport}
                                >
                                    {loadingReport && currentReportType === 'ingresos-por-tipo-reserva' ? <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span> : null}
                                    Ingresos por Tipo Reserva
                                </button>
                            </div>
                            <div className="col-md-3">
                                <button 
                                    className="btn btn-success w-100" 
                                    onClick={() => handleFetchReport('ingresos-por-numero-personas')}
                                    disabled={loadingReport}
                                >
                                    {loadingReport && currentReportType === 'ingresos-por-numero-personas' ? <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span> : null}
                                    Ingresos por Nº Personas
                                </button>
                            </div>
                        </div>

                        {loadingReport && (
                            <div className="d-flex justify-content-center my-4">
                                <div className="spinner-border text-primary" role="status">
                                    <span className="visually-hidden">Cargando reporte...</span>
                                </div>
                                <p className="ms-2">Cargando reporte, por favor espere...</p>
                            </div>
                        )}
                        {reportError && <div className="alert alert-danger mt-3">{reportError}</div>}
                        
                        {reportData && !loadingReport && !reportError && (
                            <>
                                <div className="mt-4 p-3 border rounded">
                                    <h4>Gráfico de Ingresos: {currentReportType.replace(/-/g, ' ')}</h4>
                                    <div style={{ height: '400px', width: '100%' }}>
                                        <Bar data={getChartData()} options={chartOptions} />
                                    </div>
                                </div>
                                <div className="mt-4 p-3 border rounded">
                                     <h4>Tabla de Datos: {currentReportType.replace(/-/g, ' ')}</h4>
                                    {renderReportTable()}
                                </div>
                            </>
                        )}
                         {!reportData && !loadingReport && !reportError && (
                            <div className="alert alert-info mt-3">
                                Seleccione un rango de fechas y un tipo de reporte para visualizar los datos.
                            </div>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
};

export default ReportView;