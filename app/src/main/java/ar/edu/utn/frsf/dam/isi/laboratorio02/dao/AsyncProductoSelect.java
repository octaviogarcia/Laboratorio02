package ar.edu.utn.frsf.dam.isi.laboratorio02.dao;

import android.app.Activity;
import android.os.AsyncTask;

import java.util.List;

import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Categoria;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Producto;

public class AsyncProductoSelect extends AsyncTask<Void,Double,List<Producto>> {

    public interface IProductoSelectCallback {
        public void callback(List<Producto> productos);
    }

    private AsyncProductoSelect.IProductoSelectCallback productoSelectCallback= null;
    private Activity context = null;

    public AsyncProductoSelect(Activity ctx, AsyncProductoSelect.IProductoSelectCallback callback) {
        this.productoSelectCallback = callback;
        this.context = ctx;
    }

    public AsyncProductoSelect() {

    }

    @Override
    protected List<Producto> doInBackground(Void... voids) {
        return MyDatabase.getInstance(context).getProductoDao().getAll();
    }

    @Override
    protected void onPostExecute(final List<Producto> productos) {
        if(productoSelectCallback == null || context == null) return;
        productoSelectCallback.callback(productos);
    }
}
