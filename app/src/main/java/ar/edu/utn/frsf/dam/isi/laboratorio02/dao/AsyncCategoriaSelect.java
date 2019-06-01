package ar.edu.utn.frsf.dam.isi.laboratorio02.dao;

import android.app.Activity;
import android.os.AsyncTask;

import java.util.List;

import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Categoria;

public class AsyncCategoriaSelect extends AsyncTask<Void,Double,List<Categoria>> {

    public interface ICategoriaSelectCallback {
        public void callback(List<Categoria> categorias);
    }

    private AsyncCategoriaSelect.ICategoriaSelectCallback categoriaSelectCallback = null;
    private Activity context = null;

    public AsyncCategoriaSelect(Activity ctx, AsyncCategoriaSelect.ICategoriaSelectCallback callback) {
        this.categoriaSelectCallback = callback;
        this.context = ctx;
    }

    public AsyncCategoriaSelect() {

    }

    @Override
    protected List<Categoria> doInBackground(Void... voids) {
        return MyDatabase.getInstance(context).getCategoriaDao().getAll();
    }

    @Override
    protected void onPostExecute(final List<Categoria> categorias) {
        if(categoriaSelectCallback == null || context == null) return;
        categoriaSelectCallback.callback(categorias);
    }
}
