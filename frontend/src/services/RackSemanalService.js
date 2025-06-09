import axios from 'axios';

const API_URL = '/ms-racksemanal/rack-semanal'; 

class RackSemanalService {
    /**
     * Inicializa el rack semanal en el backend.
     */
    inicializarRack() {
        return axios.post(`${API_URL}/inicializar`);
    }

    /**
     * Actualiza el rack semanal con las reservas existentes.
     */
    actualizarRack() {
        return axios.post(`${API_URL}/actualizar`);
    }

    /**
     * Obtiene la matriz completa del rack semanal.
     * Retorna: Map<String, Map<String, {reservado: boolean, reservaId: number | null}>>
     */
    obtenerMatrizRack() {
        return axios.get(`${API_URL}/matriz`);
    }

    /**
     * Obtiene detalles de una reserva por su ID.
     * @param {number} reservaId
     * Retorna: Reserva (model from ms-racksemanal)
     */
    obtenerDetallesReserva(reservaId) {
        return axios.get(`${API_URL}/detalles-reserva/${reservaId}`);
    }
}

export default new RackSemanalService();