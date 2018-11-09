package ar.edu.utn.frsf.dam.isi.laboratorio02;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.ProductoRepository;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Categoria;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.CategoriaRest;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Producto;

import java.util.Comparator;
import java.util.concurrent.ExecutionException;

public class ListaProductosActivity extends AppCompatActivity {
    private Spinner spinnerCatProd;
    private ProductoRepository productoRepository;
    private RadioGroup radioGroup;
    private Button btnAgregar;
    private Intent intent;
    private EditText etCantidad;

    final CategoriaRest categoriaRest = new CategoriaRest();

    private class AsyncCategoriaGET extends AsyncTask<Void,Double,List<Categoria>>
    {
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_productos);

        spinnerCatProd = findViewById(R.id.spinnerCatProd);
        radioGroup = findViewById(R.id.radioGroup);
        productoRepository = new ProductoRepository();
        btnAgregar = findViewById(R.id.btnAgregar);
        etCantidad = findViewById(R.id.etCantidad);
        intent = getIntent();

        AsyncCategoriaGET asyncCategoriaGET = new AsyncCategoriaGET();
        asyncCategoriaGET.execute();

        Boolean ventanaPrincipal = intent.getBooleanExtra("VentanaPrincipal",false);
        btnAgregar.setEnabled(!ventanaPrincipal);
        etCantidad.setEnabled(!ventanaPrincipal);

        btnAgregar.setOnClickListener( new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                RadioButton rb = findViewById(radioGroup.getCheckedRadioButtonId());
                String cantidad = etCantidad.getText().toString();
                if (rb == null || cantidad.isEmpty())
                {
                    //Nada seleccionado, ignoro
                    return;
                }
                Integer idProducto = rb.getId();
                Intent intentResultado = new Intent();

                intentResultado.putExtra("cantidad",Integer.valueOf(cantidad));
                intentResultado.putExtra("producto",idProducto);

                setResult(Activity.RESULT_OK,intentResultado);
                finish();
            }
        });

        try {
            setearCategorias(asyncCategoriaGET.get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    private void setearCategorias(final List<Categoria> listaCat) {
        if(listaCat == null) return;

        List<String> listaCatString = new ArrayList<>();
        for (Categoria c: listaCat) {
            listaCatString.add(c.getNombre());
        }
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter<String> aa = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,listaCatString);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinnerCatProd.setAdapter(aa);
        Integer catID = spinnerCatProd.getSelectedItemPosition();
        Categoria cat = listaCat.get(catID);
        //listarProductos(cat);
        spinnerCatProd.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                listarProductos(listaCat.get(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    void listarProductos(Categoria c)
    {
        List<Producto> listaProd = productoRepository.buscarPorCategoria(c);
        Comparator<Producto> comp = new Comparator<Producto>()
        {
            @Override
            public int compare(Producto a, Producto b)
            {
                if((a.getId()<b.getId())) return 1;
                else return 0;
            }
        };

        listaProd.sort(comp);
        radioGroup.removeAllViews();
        for(Producto p : listaProd){
            RadioButton rb = new RadioButton(this);
            rb.setId(p.getId());
            rb.setText(p.getNombre());
            radioGroup.addView(rb);
        }
    }
}
