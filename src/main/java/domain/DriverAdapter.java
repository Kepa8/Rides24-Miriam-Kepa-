package domain;

import javax.swing.table.AbstractTableModel;
import domain.Driver;
import domain.Ride;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import domain.Ride;

public class DriverAdapter extends AbstractTableModel {
    private Driver driver;
    private List<Ride> rides;

    public DriverAdapter(Driver driver) {
        this.driver = driver;
        this.rides = driver.getCreatedRides(); // Supongo que el driver tiene una lista de sus rides
    }

    // Número de filas: el número de viajes del conductor
    @Override
    public int getRowCount() {
        return rides.size();
    }

    // Número de columnas: supongo que quieres mostrar origen, destino, fecha, plazas y precio
    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Origen";
            case 1:
                return "Destino";
            case 2:
                return "Fecha";
            case 3:
                return "Plazas";
            case 4:
                return "Precio";
            default:
                return "";
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Ride ride = rides.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return ride.getFrom();
            case 1:
                return ride.getTo();
            case 2:
                return ride.getDate();
            case 3:
                return ride.getnPlaces();
            case 4:
                return ride.getPrice();
            default:
                return null;
        }
    }
}