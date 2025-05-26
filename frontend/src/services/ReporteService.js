import axios from 'axios';

// Use a specific environment variable for the report API URL.
// Fallback to '/api/reportes' if the environment variable is not set.
const REPORTE_API_BASE_URL = import.meta.env.VITE_REPORTE_API_URL || '/api/reportes';

class ReporteService {
    getIngresosPorTipoReserva(fechaInicio, fechaFin) {
        return axios.get(`${REPORTE_API_BASE_URL}/ingresos-por-tipo-reserva`, {
            params: {
                fechaInicio,
                fechaFin
            }
        });
    }

    getIngresosPorNumeroPersonas(fechaInicio, fechaFin) {
        return axios.get(`${REPORTE_API_BASE_URL}/ingresos-por-numero-personas`, {
            params: {
                fechaInicio,
                fechaFin
            }
        });
    }
}

export default new ReporteService();