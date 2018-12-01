package ar.edu.utn.frsf.dam.isi.laboratorio02.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import java.util.List;

import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Pedido;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.PedidoConDetalles;

@Dao
public interface PedidoDao {
    @Query("SELECT * FROM Pedido")
    List<Pedido> getAll();

    @Insert
    long insert(Pedido pedido);

    @Update
    int update(Pedido pedido);

    @Delete
    void delete(Pedido pedido);

    @Transaction
    @Query("SELECT * from Pedido WHERE id = :id")
    List<PedidoConDetalles> buscarPorIdConDetalles(Integer id);
}
