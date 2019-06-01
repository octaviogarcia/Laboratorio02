package ar.edu.utn.frsf.dam.isi.laboratorio02.dao;

import android.arch.persistence.room.Room;
import android.content.Context;

public class MyDatabase {
    private static MyDatabase instance = null;
    public static MyDatabase getInstance(Context ctx) {
        if(instance == null) instance = new MyDatabase(ctx);
        return instance;
    }

    private DatabaseProyecto db;
    private CategoriaDao categoriaDao;
    private ProductoDao productoDao;


    private PedidoDao pedidoDao;



    private PedidoDetalleDao pedidoDetalleDao;

    private MyDatabase(Context ctx){
        db = Room.databaseBuilder(ctx,DatabaseProyecto.class,"database-proyecto")
                .fallbackToDestructiveMigration()
                .build();

        categoriaDao = db.categoriaDao();
        productoDao = db.productoDao();
        pedidoDao = db.pedidoDao();
        pedidoDetalleDao = db.pedidoDetalleDao();
    }

    public void borrarTodo(){
        this.db.clearAllTables();
    }

    public CategoriaDao getCategoriaDao() {
        return categoriaDao;
    }
    public ProductoDao  getProductoDao()  { return productoDao; }
    public PedidoDao getPedidoDao() { return pedidoDao; }

    public PedidoDetalleDao getPedidoDetalleDao() { return pedidoDetalleDao; }




}
