import React from 'react';
import { Link } from 'react-router-dom';
import '../App.css';

const Header = () => {
    return (
        <nav className="navbar navbar-expand-lg navbar-dark bg-dark">
            <div className="container">
                <Link className="navbar-brand" to="/">Karting RM</Link>
                <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                    <span className="navbar-toggler-icon"></span>
                </button>
                <div className="collapse navbar-collapse" id="navbarNav">
                    <ul className="navbar-nav">
                        <li className="nav-item">
                            <Link className="nav-link" to="/">Inicio</Link>
                        </li>
                        <li className="nav-item">
                            <Link className="nav-link" to="/reservas">Reservas</Link>
                        </li>
                        <li className="nav-item">
                            <Link className="nav-link" to="/rack-semanal">Rack Semanal</Link>
                        </li>
                        <li className="nav-item">
                            <Link className="nav-link" to="/agregar-reserva">Nueva Reserva</Link>
                        </li>
                        <li className="nav-item">
                            <Link className="nav-link fw-bold py-1 px-0" to="/comprobantes">Comprobantes</Link>
                        </li>
                        <li className="nav-item">
                            <Link className="nav-link" to="/reportes">Reportes</Link>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
    );
};

export default Header;