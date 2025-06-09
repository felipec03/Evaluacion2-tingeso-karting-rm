import React, { useState, useEffect } from 'react';
import { Table, Alert, Spinner, Card } from 'react-bootstrap';

import TarifasService from '../services/TarifasService';
import DescuentoPersonaService from '../services/DescuentoPersonaService';
import TarifaDiaEspecialService from '../services/TarifaDiaEspecialService';

const NuestraOferta = () => {
    const [tarifasState, setTarifasState] = useState({ data: [], loading: true, error: null });
    const [descuentosState, setDescuentosState] = useState({ data: [], loading: true, error: null });
    const [especialesState, setEspecialesState] = useState({ data: [], loading: true, error: null });

    useEffect(() => {
        // Fetch Tarifas Generales
        TarifasService.getAllTarifas()
            .then(response => {
                // Filter out inactive tariffs if 'activa' field exists and is relevant
                const activeTarifas = response.data.filter(tarifa => tarifa.activa !== false);
                setTarifasState({ data: activeTarifas, loading: false, error: null });
            })
            .catch(error => {
                console.error("Error fetching tarifas:", error);
                setTarifasState({ data: [], loading: false, error: 'Error al cargar las tarifas generales.' });
            });

        // Fetch Descuentos por Persona
        DescuentoPersonaService.getAllDescuentosPersona()
            .then(response => {
                // Filter out inactive discounts if 'activo' field exists and is relevant
                const activeDescuentos = response.data.filter(descuento => descuento.activo !== false);
                setDescuentosState({ data: activeDescuentos, loading: false, error: null });
            })
            .catch(error => {
                console.error("Error fetching descuentos persona:", error);
                setDescuentosState({ data: [], loading: false, error: 'Error al cargar los descuentos por persona.' });
            });

        // Fetch Tarifas Días Especiales
        TarifaDiaEspecialService.getAllTarifasDiaEspecial()
            .then(response => {
                setEspecialesState({ data: response.data, loading: false, error: null });
            })
            .catch(error => {
                console.error("Error fetching tarifas dias especiales:", error);
                setEspecialesState({ data: [], loading: false, error: 'Error al cargar las tarifas de días especiales.' });
            });
    }, []);


    const renderTable = (title, state, columns, dataKeyMap) => {
        return (
            <Card className="mb-4">
                <Card.Header as="h4">{title}</Card.Header>
                <Card.Body>
                    {state.loading && (
                        <div className="text-center">
                            <Spinner animation="border" role="status">
                                <span className="visually-hidden">Cargando...</span>
                            </Spinner>
                        </div>
                    )}
                    {state.error && <Alert variant="danger">{state.error}</Alert>}
                    {!state.loading && !state.error && state.data.length === 0 && (
                        <Alert variant="info">No hay {title.toLowerCase()} disponibles en este momento.</Alert>
                    )}
                    {!state.loading && !state.error && state.data.length > 0 && (
                        <Table striped bordered hover responsive>
                            <thead>
                                <tr>
                                    {columns.map(col => <th key={col}>{col}</th>)}
                                </tr>
                            </thead>
                            <tbody>
                                {state.data.map((item, index) => (
                                    <tr key={item.id || index}>
                                        {dataKeyMap.map(keyItem => (
                                            <td key={keyItem.key}>
                                                {typeof keyItem.render === 'function' ? keyItem.render(item) : item[keyItem.key]}
                                            </td>
                                        ))}
                                    </tr>
                                ))}
                            </tbody>
                        </Table>
                    )}
                </Card.Body>
            </Card>
        );
    };


     const tarifasColumns = ["Descripción", "Precio Base por Persona", "Activa"];
    const tarifasDataKeyMap = [
        { key: "descripcion" },
        { key: "precioBasePorPersona", render: (item) => `$${Number(item.precioBasePorPersona).toLocaleString('es-CL')}` },
        { key: "activa", render: (item) => item.activa ? 'Sí' : 'No' }
    ];

    const descuentosColumns = ["ID", "Rango de Personas", "Porcentaje Descuento", "Activo"];
    const descuentosDataKeyMap = [
        { key: "id" },
        { 
            key: "rangoPersonas", // Custom key for the combined field
            render: (item) => `${item.personasMin} - ${item.personasMax}` 
        },
        { 
            key: "porcentajeDescuento", 
            render: (item) => `${item.porcentajeDescuento}%` 
        },
        { key: "activo", render: (item) => item.activo ? 'Sí' : 'No' }
    ];

    const especialesColumns = ["Descripción", "Fecha", "Recargo (%)", "Es Feriado"];
    const especialesDataKeyMap = [
        { key: "descripcion" },
        { key: "fecha", render: (item) => item.fecha ? new Date(item.fecha + 'T00:00:00').toLocaleDateString('es-CL') : 'Fin de semana' }, // Handle null date for "Fin de semana"
        { key: "recargoPorcentaje", render: (item) => `${item.recargoPorcentaje}%` },
        { key: "esFeriado", render: (item) => item.esFeriado ? 'Sí' : 'No' }
    ];


    return (
        <div className="container mt-4">
            <h2 className="mb-4 text-center">Nuestras Ofertas y Tarifas</h2>
            
            {renderTable("Tarifas Generales", tarifasState, tarifasColumns, tarifasDataKeyMap)}
            {renderTable("Descuentos por Persona", descuentosState, descuentosColumns, descuentosDataKeyMap)}
            {renderTable("Tarifas de Días Especiales", especialesState, especialesColumns, especialesDataKeyMap)}
        </div>
    );
};

export default NuestraOferta;