package ar.edu.utn.frsf.dam.isi.laboratorio02;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import java.util.List;
import java.util.concurrent.ExecutionException;

import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.AsyncCategoriaGET;
import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.ProductoRetrofit;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Categoria;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Producto;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GestionProductoActivity extends AppCompatActivity {

    private Button btnMenu;
    private Button btnGuardar;
    private Spinner comboCategorias;
    private EditText nombreProducto;
    private EditText descProducto;
    private EditText precioProducto;
    private ToggleButton opcionNuevoBusqueda;
    private EditText idProductoBuscar;
    private Button btnBuscar;
    private Button btnBorrar;
    private Boolean flagActualizacion;
    private ArrayAdapter<Categoria> comboAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_producto);

        AsyncCategoriaGET asyncCategoriaGET = new AsyncCategoriaGET();
        asyncCategoriaGET.execute();

        flagActualizacion = false;
        opcionNuevoBusqueda = (ToggleButton) findViewById(R.id.abmProductoAltaNuevo);
        idProductoBuscar = (EditText) findViewById(R.id.abmProductoIdBuscar);
        nombreProducto = (EditText) findViewById(R.id.abmProductoNombre);
        descProducto = (EditText) findViewById(R.id.abmProductoDescripcion);
        precioProducto = (EditText) findViewById(R.id.abmProductoPrecio);
        comboCategorias = (Spinner) findViewById(R.id.abmProductoCategoria);
        btnMenu = (Button) findViewById(R.id.btnAbmProductoVolver);
        btnGuardar = (Button) findViewById(R.id.btnAbmProductoCrear);
        btnBuscar = (Button) findViewById(R.id.btnAbmProductoBuscar);
        btnBorrar = (Button) findViewById(R.id.btnAbmProductoBorrar);

        opcionNuevoBusqueda.setChecked(false);
        btnBuscar.setEnabled(false);
        btnBorrar.setEnabled(false);
        idProductoBuscar.setEnabled(false);

        opcionNuevoBusqueda.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                flagActualizacion = isChecked;
                btnBuscar.setEnabled(isChecked);
                btnBorrar.setEnabled(isChecked);
                idProductoBuscar.setEnabled(isChecked);

                if (isChecked) btnGuardar.setText("Actualizar");
                else btnGuardar.setText("Guardar");
            }
        });

        btnBuscar.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idStr = idProductoBuscar.getText().toString();
                if (idStr.isEmpty()) return;

                Integer id = Integer.valueOf(idStr);
                ProductoRetrofit clienteRest =
                        RestClient.getInstance()
                                .getRetrofit()
                                .create(ProductoRetrofit.class);
                Call<Producto> busquedaCall = clienteRest.buscarProductoPorId(id);

                busquedaCall.enqueue(new CallbackBuscarProducto());
            }
        });
        btnGuardar.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = nombreProducto.getText().toString();
                String descripcion = descProducto.getText().toString();
                String precioStr = precioProducto.getText().toString();
                String idStr = idProductoBuscar.getText().toString();

                if (nombre.isEmpty() || descripcion.isEmpty() || precioStr.isEmpty()) return;
                if (flagActualizacion && idStr.isEmpty()) return;

                nombreProducto.setText("");
                descProducto.setText("");
                precioProducto.setText("");
                idProductoBuscar.setText("");

                Double precio = Double.valueOf(precioStr);

                Categoria categoria = comboAdapter.getItem(comboCategorias.getSelectedItemPosition());
                Producto p = new Producto(nombre, descripcion, precio, categoria);

                ProductoRetrofit clienteRest =
                        RestClient.getInstance()
                                .getRetrofit()
                                .create(ProductoRetrofit.class);

                if (flagActualizacion) {
                    Integer id = Integer.valueOf(idStr);
                    Call<Producto> actualizarCall = clienteRest.actualizarProducto(id, p);
                    actualizarCall.enqueue(new CallbackActualizarProducto());
                } else {
                    Call<Producto> altaCall = clienteRest.crearProducto(p);
                    altaCall.enqueue(new CallbackGuardarProducto());
                }

            }
        });

        btnBorrar.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idStr = idProductoBuscar.getText().toString();
                if (idStr.isEmpty()) return;

                nombreProducto.setText("");
                descProducto.setText("");
                precioProducto.setText("");
                idProductoBuscar.setText("");

                ProductoRetrofit clienteRest =
                        RestClient.getInstance()
                                .getRetrofit()
                                .create(ProductoRetrofit.class);

                Integer id = Integer.valueOf(idStr);
                Call<Producto> borrarCall = clienteRest.borrar(id);
                borrarCall.enqueue(new CallbackBorrarProducto());
            }
        });

        final Intent intentMain = new Intent(this,MainActivity.class);
        btnMenu.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentMain);
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
        if (listaCat == null) return;
        comboAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaCat);
        comboAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comboCategorias.setAdapter(comboAdapter);
    }

    private Integer buscarCategoriaIndice(Integer id) {
        for (int index = 0; index < comboAdapter.getCount(); index++) {
            Categoria c = comboAdapter.getItem(index);
            if (c.getId().equals(id)) return index;
        }
        return -1;
    }

    private class CallbackBuscarProducto implements Callback<Producto> {
        @Override
        public void onResponse(Call<Producto> call, Response<Producto> response) {
            if (response.code() == 200 || response.code() == 201) {
                Producto producto = response.body();

                nombreProducto.setText(producto.getNombre());
                descProducto.setText(producto.getDescripcion());
                precioProducto.setText(producto.getPrecio().toString());

                Integer indice = GestionProductoActivity.this.buscarCategoriaIndice(producto.getCategoria().getId());
                comboCategorias.setSelection(indice);

                return;
            } else {
                String error = String.format("Error respuesta %d", response.code());
                Log.d("GestionProductoActivity", error);
            }
        }

        @Override
        public void onFailure(Call<Producto> call, Throwable t) {
            Log.d("GestionProductoActivity", t.getMessage());
        }
    }

    private class CallbackGuardarProducto implements Callback<Producto> {
        @Override
        public void onResponse(Call<Producto> call, Response<Producto> response) {
            if (response.code() == 200 || response.code() == 201) {
                Producto producto = response.body();

                @SuppressLint("DefaultLocale")
                String toShow = String.format(
                        "Agregado Producto {%s,%s,%f,%s}",
                        producto.getNombre(),
                        producto.getDescripcion(),
                        producto.getPrecio(),
                        producto.getCategoria().toString());

                Log.d("GestionProductoActivity", toShow);
            } else {
                String error = String.format("Error respuesta %d", response.code());
                Log.d("GestionProductoActivity", error);
            }
        }

        @Override
        public void onFailure(Call<Producto> call, Throwable t) {
            Log.d("GestionProductoActivity", t.getMessage());
        }
    }

    private class CallbackActualizarProducto implements Callback<Producto> {
        @Override
        public void onResponse(Call<Producto> call, Response<Producto> response) {
            if (response.code() == 200 || response.code() == 201) {
                Producto producto = response.body();
                @SuppressLint("DefaultLocale")
                String toShow = String.format(
                        "Actualizando Producto {%s,%s,%f,%s}",
                        producto.getNombre(),
                        producto.getDescripcion(),
                        producto.getPrecio(),
                        producto.getCategoria().toString());

                Log.d("GestionProductoActivity", toShow);
            } else {
                String error = String.format("Error respuesta %d", response.code());
                Log.d("GestionProductoActivity", error);
            }
        }

        @Override
        public void onFailure(Call<Producto> call, Throwable t) {
            Log.d("GestionProductoActivity", t.getMessage());
        }
    }

    private class CallbackBorrarProducto implements Callback<Producto> {

        @Override
        public void onResponse(Call<Producto> call, Response<Producto> response) {
            if (response.code() == 200 || response.code() == 201) {
                Producto producto = response.body();
                @SuppressLint("DefaultLocale")
                String toShow = String.format(
                        "Borrado Producto {%s,%s,%f,%s}",
                        producto.getNombre(),
                        producto.getDescripcion(),
                        producto.getPrecio(),
                        producto.getCategoria().toString());

                Log.d("GestionProductoActivity", toShow);
            } else {
                String error = String.format("Error respuesta %d", response.code());
                Log.d("GestionProductoActivity", error);
            }
        }

        @Override
        public void onFailure(Call<Producto> call, Throwable t) {
            Log.d("GestionProductoActivity", t.getMessage());
        }
    }

}
