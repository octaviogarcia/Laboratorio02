package ar.edu.utn.frsf.dam.isi.laboratorio02.dao;

import android.arch.persistence.room.TypeConverter;

import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Pedido;

public class EstadoConverter {
    @TypeConverter
    public static Pedido.Estado toEstado(String status) {
        return Pedido.Estado.valueOf(status);
    }
    @TypeConverter
    public static String toString(Pedido.Estado status) {
        return status.toString();
    }
}
