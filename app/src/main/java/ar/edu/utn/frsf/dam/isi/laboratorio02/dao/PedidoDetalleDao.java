package ar.edu.utn.frsf.dam.isi.laboratorio02.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.PedidoDetalle;

@Dao
public interface PedidoDetalleDao {
    @Query("SELECT * from PedidoDetalle")
    List<PedidoDetalle> getAll();

    @Insert
    long insert(PedidoDetalle pedidoDetalle);

    @Update
    int update(PedidoDetalle pedidoDetalle);

    @Delete
    void delete(PedidoDetalle pedidoDetalle);
}
