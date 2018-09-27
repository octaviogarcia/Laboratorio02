package ar.edu.utn.frsf.dam.isi.laboratorio02;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.ProductoRepository;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Categoria;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Producto;

import java.util.Comparator;

public class ListaProductosActivity extends AppCompatActivity {
    private Spinner spinnerCatProd;
    private ProductoRepository productoRepository;
    private RadioGroup radioGroup;
    private Button btnAgregar;
    private Intent intent;
    private EditText etCantidad;
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

        final List<Categoria> listaCat = productoRepository.getCategorias();

        List<String> listaCatString = new ArrayList<String>();
        for (Categoria c: listaCat) {
            listaCatString.add(c.getNombre());
        }

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,listaCatString);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinnerCatProd.setAdapter(aa);

        Integer catID = spinnerCatProd.getSelectedItemPosition();
        Categoria cat = listaCat.get(catID);
        listarProductos(cat);
        spinnerCatProd.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                listarProductos(listaCat.get(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        Boolean ventanaPrincipal = intent.getBooleanExtra("VentanaPrincipal",false);
        btnAgregar.setEnabled(!ventanaPrincipal);
        etCantidad.setEnabled(!ventanaPrincipal);
        btnAgregar.setOnClickListener( new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                RadioButton rb = findViewById(radioGroup.getCheckedRadioButtonId());
                if (rb == null)
                {
                    //Nada seleccionado, ignoro
                    return;
                }
                Integer idProducto = rb.getId();
                String cantidad = etCantidad.getText().toString();
                Intent intentResultado = new Intent();
                //si no puse cantidad, tomo como 1
                //Quality of life!
                if(cantidad.isEmpty()) intentResultado.putExtra("cantidad",1);
                else intentResultado.putExtra("cantidad",Integer.valueOf(cantidad));
                intentResultado.putExtra("producto",idProducto);
                setResult(Activity.RESULT_OK,intentResultado);
                finish();
            }
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
