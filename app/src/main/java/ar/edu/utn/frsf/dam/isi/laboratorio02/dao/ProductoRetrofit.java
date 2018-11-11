package ar.edu.utn.frsf.dam.isi.laboratorio02.dao;

import java.util.List;

import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Producto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProductoRetrofit {

    @GET("productos/")
    public Call<List<Producto>> listarProductos();
    @GET("productos/{id}/")
    public Call<Producto> buscarProductoPorId(@Path("id") int idProducto);
    @POST("productos/")
    public Call<Producto> crearProducto(@Body Producto p);
    @PUT("productos/{id}/")
    public Call<Producto> actualizarProducto(@Path("id") int idProducto,
                                      @Body       Producto p);
    @DELETE("productos/{id}/")
    public Call<Producto> borrar(@Path("id") int idProducto);
}

