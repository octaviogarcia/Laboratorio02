package ar.edu.utn.frsf.dam.isi.laboratorio02;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.CategoriaRest;
import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.MyDatabase;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Categoria;

public class CategoriaActivity extends AppCompatActivity {
    private EditText textoCat;
    private Button btnCrear;
    private Button btnMenu;
    final CategoriaRest categoriaRest = new CategoriaRest();

    private class AsyncCategoriaPOST extends AsyncTask<Categoria,Double,Integer>
    {
        @Override
        protected Integer doInBackground(Categoria... categorias) {
            Categoria categoria = categorias[0];
            try
            {
                categoriaRest.crearCategoria(categoria);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return 1;
        }
        protected void onPostExecute(Integer integer) {
            Toast.makeText(CategoriaActivity.this,"Categoria creada",Toast.LENGTH_LONG).show();
            textoCat.setText("");
        }
    }

    private class AsyncCategoriaInsert extends AsyncTask<Categoria,Double,Integer>
    {
        @Override
        protected Integer doInBackground(Categoria... categorias) {
            Categoria categoria = categorias[0];
            MyDatabase.getInstance(CategoriaActivity.this)
                    .getCategoriaDao()
                    .insert(categoria);
            return 1;
        }
        protected void onPostExecute(Integer integer) {
            Toast.makeText(CategoriaActivity.this,"Categoria creada",Toast.LENGTH_LONG).show();
            textoCat.setText("");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);

        textoCat = (EditText) findViewById(R.id.txtNombreCategoria);
        btnCrear = (Button) findViewById(R.id.btnCrearCategoria);

        btnCrear.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Categoria categoria = new Categoria();
                String categoriaNombre = textoCat.getText().toString();
                if(categoriaNombre.isEmpty()) return;
                categoria.setNombre(categoriaNombre);

                if(MainActivity.useDB) {
                    AsyncCategoriaInsert asyncCategoriaInsert = new AsyncCategoriaInsert();
                    asyncCategoriaInsert.execute(categoria);
                }
                else {
                    AsyncCategoriaPOST asyncCategoriaPOST = new AsyncCategoriaPOST();
                    asyncCategoriaPOST.execute(categoria);
                }

                return;
            }
        });

        btnMenu= (Button) findViewById(R.id.btnCategoriaVolver);
        btnMenu.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CategoriaActivity.this,
                        MainActivity.class);
                startActivity(i);
            }
        });

    }
}
