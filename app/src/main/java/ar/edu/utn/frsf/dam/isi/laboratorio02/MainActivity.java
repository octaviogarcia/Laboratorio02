package ar.edu.utn.frsf.dam.isi.laboratorio02;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btnNuevoPedido;
    private Button btnHistorial;
    private Button btnListaProductos;
    private Button btnPrepararPedidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        final Intent intentListaProductos = new Intent(this, ListaProductosActivity.class);
        final Intent intentNuevoPedido = new Intent(this,NuevoPedidoActivity.class);
        final Intent intentHistorial = new Intent(this,HistorialPedidoActivity.class);
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
                startActivity(intentHistorial);
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

        btnPrepararPedidos = (Button) findViewById(R.id.btnPrepararPedidos);
        btnPrepararPedidos.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,PrepararPedidoService.class);
                startService(intent);
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

    private void createNotificationChannel() {
        // Crear el canal de notificaciones pero solo para API 26 io superior
        // dado que NotificationChannel es una clase nueva que no está incluida
        // en las librerías de soporte qeu brindan compatibilidad hacía atrás
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.canal_estado_nombre);
            String description = getString(R.string.canal_estado_descr);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CANAL01", name, importance);
            channel.setDescription(description);
            // Registrar el canal en el sistema
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
