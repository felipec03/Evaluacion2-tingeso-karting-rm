import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css';

import Header from './components/Header';
import HomePage from './components/HomePage';
import ReserveList from './components/ReserveList';
import ReserveForm from './components/ReserveForm';
import DriverList from './components/DriverList';
import KartList from './components/KartList';
import ReportView from './components/ReportView';
import RackSemanal from './components/RackSemanal';

function App() {
  return (
    <Router>
      <div className="App">
        <Header />
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/reservas" element={<ReserveList />} />
          <Route path="/rack-semanal" element={<RackSemanal />} />
          <Route path="/agregar-reserva" element={<ReserveForm />} />
          <Route path="/pilotos" element={<DriverList />} />
          <Route path="/karts" element={<KartList />} />
          <Route path="/reportes" element={<ReportView />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;