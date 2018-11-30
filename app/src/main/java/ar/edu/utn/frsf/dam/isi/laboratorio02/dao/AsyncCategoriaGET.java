package ar.edu.utn.frsf.dam.isi.laboratorio02.dao;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Categoria;

public class AsyncCategoriaGET extends AsyncTask<Void,Double,List<Categoria>>
{
    CategoriaRest categoriaRest = new CategoriaRest();

    public interface ICategoriaGETCallback {
        public void callback(List<Categoria> categorias);
    }

    private ICategoriaGETCallback categoriaGETCallback = null;

    public AsyncCategoriaGET(ICategoriaGETCallback callback) {
        this.categoriaGETCallback = callback;
    }

    public AsyncCategoriaGET() {}

    @Override
    protected List<Categoria> doInBackground(Void... voids) {
        try
        {
            List<Categoria> list = categoriaRest.listarTodas();
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Categoria> categorias) {
        if(categoriaGETCallback == null) return;
        categoriaGETCallback.callback(categorias);
    }
}