package ar.edu.utn.frsf.dam.isi.laboratorio02.dao;

import android.util.Log;

import java.io.IOException;
import java.util.List;

import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Producto;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.RestClient;
import retrofit2.Call;
import retrofit2.Response;


public class AsyncProductoGET extends Thread {
    ProductoRetrofit clienteRest = null;
    Call<List<Producto>> listaProdCall = null;
    Response<List<Producto>> respuesta = null;
    List<Producto> resp = null;

    public AsyncProductoGET() {
        clienteRest = RestClient.getInstance()
                .getRetrofit()
                .create(ProductoRetrofit.class);

        listaProdCall = clienteRest.listarProductos();
    }

    public void run() {
        try {
            respuesta = listaProdCall.execute();
            if (respuesta.code() == 200 || respuesta.code() == 201) {
                Log.d("AsyncProductoGET", respuesta.body().toString());
                resp = respuesta.body();
            }
            String error = String.format("Error respuesta %d", respuesta.code());
            Log.d("AsyncProductoGET", error);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Producto> get() {
        return resp;
    }
}