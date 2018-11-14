package ar.edu.utn.frsf.dam.isi.laboratorio02;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.AsyncCategoriaGET;
import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.ProductoRepository;
import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.ProductoRetrofit;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Categoria;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Producto;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaProductosActivity extends AppCompatActivity {
    private Spinner spinnerCatProd;
    private ProductoRepository productoRepository;
    private Button btnAgregar;
    private Intent intent;
    private EditText etCantidad;

    private ListView lvProductos;
    ArrayAdapter<Categoria> adapterCategoria = null;
    ArrayAdapter<Producto> adapterProducto = null;

    private List<Producto> listaProd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AsyncCategoriaGET asyncCategoriaGET = new AsyncCategoriaGET();
        asyncCategoriaGET.execute();

        setContentView(R.layout.lista_productos);

        spinnerCatProd = findViewById(R.id.spinnerCatProd);
        productoRepository = new ProductoRepository();
        btnAgregar = findViewById(R.id.btnAgregar);
        etCantidad = findViewById(R.id.etCantidad);
        lvProductos = findViewById(R.id.lvProductos);
        intent = getIntent();

        Boolean ventanaPrincipal = intent.getBooleanExtra("VentanaPrincipal",false);
        btnAgregar.setEnabled(!ventanaPrincipal);
        etCantidad.setEnabled(!ventanaPrincipal);

        btnAgregar.setOnClickListener( new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                Integer index = lvProductos.getCheckedItemPosition();
                String cantidad = etCantidad.getText().toString();
                if (index == -1 || cantidad.isEmpty())
                {
                    //Nada seleccionado, ignoro
                    return;
                }
                Producto selectedItem = adapterProducto.getItem(index);
                Integer idProducto = selectedItem.getId();
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

        adapterCategoria = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,listaCat);
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCatProd.setAdapter(adapterCategoria);
        listarProductosInicial(listaCat.get(0));
        spinnerCatProd.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                listarProductos(listaCat.get(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    void listarProductosInicial(final Categoria c)
    {//Setea la variable privada listaProd
        ProductoRetrofit clienteRest =
            RestClient.getInstance()
                    .getRetrofit()
                    .create(ProductoRetrofit.class);

        Call<List<Producto> > listaProdCall = clienteRest.listarProductos();

        listaProdCall.enqueue(new Callback<List<Producto>>(){
        @Override
        public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
            if (response.code() == 200 || response.code() == 201) {
                List<Producto> productos = response.body();
                ListaProductosActivity.this.listaProd = productos;
                listarProductos(c);
            } else {
                @SuppressLint("DefaultLocale")
                String error = String.format("Error respuesta %d", response.code());
                Log.d("ListaProductosActivity", error);
            }
        }

        @Override
        public void onFailure(Call<List<Producto>> call, Throwable t) {
            Log.d("ListaProductosActivity", t.getMessage());
        }

    });
    }

    private void listarProductos(final Categoria c)
    {//Reutiliza la variable listaProd
        if(listaProd == null) return;
        //List<Producto> listaProd = productoRepository.buscarPorCategoria(c);
        List<Producto> listaMostrar = new ArrayList<>();

        Comparator<Producto> comp = new Comparator<Producto>()
        {
            @Override
            public int compare(Producto a, Producto b)
            {
                if((a.getId()<b.getId())) return 1;
                else return 0;
            }
        };

        for(Producto p : listaProd)
        {
            if(p.getCategoria().getId().equals(c.getId())) listaMostrar.add(p);
        }

        listaMostrar.sort(comp);

        adapterProducto = new ArrayAdapter<>(this,android.R.layout.simple_list_item_single_choice,listaMostrar);
        lvProductos.setAdapter(adapterProducto);
        lvProductos.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }
}
