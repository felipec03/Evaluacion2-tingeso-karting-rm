import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8090';
const REPORTE_API_URL = `${API_URL}/api/reportes`;

class ReporteService {
    getIngresosPorTipoReserva(fechaInicio, fechaFin) {
        return axios.get(`${REPORTE_API_URL}/ingresos-por-tipo-reserva`, {
            params: {
                fechaInicio,
                fechaFin
            }
        });
    }

    getIngresosPorNumeroPersonas(fechaInicio, fechaFin) {
        return axios.get(`${REPORTE_API_URL}/ingresos-por-numero-personas`, {
            params: {
                fechaInicio,
                fechaFin
            }
        });
    }
}

export default new ReporteService();