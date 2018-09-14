package ar.edu.utn.frsf.dam.isi.laboratorio02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.ProductoRepository;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Categoria;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Producto;

public class ListaProductosActivity extends AppCompatActivity {
    private Spinner spinnerCatProd;
    private ProductoRepository productoRepository;
    private RadioGroup radioGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_productos);

        spinnerCatProd = findViewById(R.id.spinnerCatProd);
        radioGroup = findViewById(R.id.radioGroup);
        productoRepository = new ProductoRepository();
        List<Categoria> listaCat = productoRepository.getCategorias();

        List<String> listaCatString = new ArrayList<String>();
        for (Categoria c: listaCat) {
            listaCatString.add(c.getNombre());
        }

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,listaCatString);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinnerCatProd.setAdapter(aa);

        List<Producto> listaProd = productoRepository.getLista();
        for(Producto p : listaProd){
            RadioButton rb = new RadioButton(this);
            rb.setId(p.getId());
            rb.setText(p.getNombre());
            radioGroup.addView(rb);
        }
    }
}
