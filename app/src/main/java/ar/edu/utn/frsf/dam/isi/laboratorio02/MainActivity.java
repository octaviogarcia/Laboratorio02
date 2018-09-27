package ar.edu.utn.frsf.dam.isi.laboratorio02;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btnNuevoPedido;
    private Button btnHistorial;
    private Button btnListaProductos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Intent intentListaProductos = new Intent(this, ListaProductosActivity.class);
        final Intent intentNuevoPedido = new Intent(this,NuevoPedidoActivity.class);
        btnNuevoPedido = (Button) findViewById(R.id.btnMainNuevoPedido);
        btnNuevoPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentNuevoPedido);
            }
        });

        btnHistorial = (Button) findViewById(R.id.btnHistorialPedidos);
        btnHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        btnListaProductos = (Button) findViewById(R.id.btnListaProductos);
        btnListaProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentListaProductos.putExtra("VentanaPrincipal",true);
                startActivityForResult(intentListaProductos,CodigosLlamadas.MAIN_A_LISTARPRODUCTOS.ordinal());
            }
        });
    }
    @Override
    protected void onActivityResult(int request,int result, Intent data)
    {//Dejo el esqueleto para acordarme nomas
        if(result == Activity.RESULT_OK)
        {
            if(request == CodigosLlamadas.MAIN_A_LISTARPRODUCTOS.ordinal()) return;//Nunca deberia entrar aca, unreachable
            else{}
        }
        else {
            //Deberia siempre fallar pq no hay forma q retorne con un finish()
        }

    }
}
