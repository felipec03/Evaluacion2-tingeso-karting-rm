import React from 'react';
import { Link } from 'react-router-dom';

const HomePage = () => {
    return (
        <div className="container-fluid mt-5">
            <div className="jumbotron text-center">
                <h1 className="display-4">¡Bienvenido a Karting RM!</h1>
                <p className="lead">
                    Sistema de gestión de reservas para pistas de karting. 
                    Administre fácilmente sus reservas, pilotos y karts.
                </p>
                <hr className="my-4" />
                <p>Seleccione una de las opciones a continuación para comenzar.</p>
                
                <div className="row mt-5">
                    <div className="col-md-4 mb-4">
                        <div className="card h-100">
                            <div className="card-body text-center">
                                <h5 className="card-title">Gestión de Reservas</h5>
                                <p className="card-text">Ver, crear, editar y eliminar reservas.</p>
                                <Link to="/reservas" className="btn btn-primary">
                                    Ver Reservas
                                </Link>
                            </div>
                        </div>
                    </div>
                    
                    <div className="col-md-4 mb-4">
                        <div className="card h-100">
                            <div className="card-body text-center">
                                <h5 className="card-title">Rack Semanal</h5> {/* Updated title */}
                                <p className="card-text">Visualización semanal de disponibilidad.</p> {/* Updated text */}
                                <Link to="/rack-semanal" className="btn btn-success">
                                    Ver Rack Semanal
                                </Link> {/* Updated link */}
                            </div>
                        </div>
                    </div>
                    
                    <div className="col-md-4 mb-4">
                        <div className="card h-100">
                            <div className="card-body text-center">
                                <h5 className="card-title">Nueva Reserva</h5>
                                <p className="card-text">Crear una nueva reserva en el sistema.</p>
                                <Link to="/agregar-reserva" className="btn btn-warning">
                                    Crear Reserva
                                </Link>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default HomePage;