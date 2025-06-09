import axios from 'axios';

const API_URL = '/api/rack-semanal'; 

class RackSemanalService {
    /**
     * Obtiene todas las reservas activas a través de ms-racksemanal.
     * Retorna: Promise<AxiosResponse<Array<Reserva>>>
     * Donde Reserva es el modelo que coincide con la ReservaEntity del backend.
     */
    obtenerTodasLasReservas() {
        return axios.get(`${API_URL}/reservas`);
    }

    // Removed: inicializarRack, actualizarRack, obtenerMatrizRack, obtenerDetallesReserva
    // as these functionalities are no longer part of the simplified ms-racksemanal backend.
}

export default new RackSemanalService();