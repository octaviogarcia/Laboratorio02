package ar.edu.utn.frsf.dam.isi.laboratorio02.dao;

import android.app.Activity;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Producto;
import retrofit2.Call;
import retrofit2.Response;


//Uso un Thread por que un AsyncTask no funcionaba...
public class AsyncProductoGET extends Thread {
    ProductoRetrofit clienteRest = null;
    Call<List<Producto>> listaProdCall = null;
    Response<List<Producto>> respuesta = null;
    List<Producto> resp = null;
    Activity context = null;


    public interface IProductoGETCallback
    {
        public void callback(List<Producto> productos);
    }

    IProductoGETCallback productoGETCallback = null;

    public AsyncProductoGET(Activity ctx,IProductoGETCallback callback) {
        clienteRest = RestClient.getInstance()
                .getRetrofit()
                .create(ProductoRetrofit.class);

        listaProdCall = clienteRest.listarProductos();
        productoGETCallback = callback;
        context = ctx;
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

        if(productoGETCallback != null && context != null){
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    productoGETCallback.callback(resp);
                }
            });
        }
    }

    public List<Producto> get() {
        return resp;
    }
}