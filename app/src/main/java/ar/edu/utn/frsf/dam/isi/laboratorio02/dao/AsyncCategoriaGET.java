package ar.edu.utn.frsf.dam.isi.laboratorio02.dao;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Categoria;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.CategoriaRest;

public class AsyncCategoriaGET extends AsyncTask<Void,Double,List<Categoria>>
{
    CategoriaRest categoriaRest = new CategoriaRest();
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
}