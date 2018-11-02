package ar.edu.utn.frsf.dam.isi.laboratorio02;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.PedidoRepository;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Pedido;

public class EstadoPedidoReceiver extends BroadcastReceiver {
    public static final String paq = "ar.edu.utn.frsf.dam.isi.laboratorio02";
    public static final String ESTADO_ACEPTADO = paq+".ESTADO_ACEPTADO";
    public static final String ESTADO_CANCELADO = paq+".ESTADO_CANCELADO";
    public static final String ESTADO_EN_PREPARACION = paq+".ESTADO_EN_PREPARACION";
    public static final String ESTADO_LISTO = paq+".ESTADO_LISTO";
    public static final String id_pedido_extra = "idPedido";
    public static final String id_pedidos_extra = "pedidosRealizados";

    PedidoRepository pedidoRepository = new PedidoRepository();

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(ESTADO_ACEPTADO))
        {
            Pedido p = pedidoRepository.buscarPorId(intent.getIntExtra(id_pedido_extra,-1));
            if(p != null)
            {
                String contenido = String.format("El costo sera de $%f\n Previsto el envio para %s",p.total(),p.getFecha().toString());

                Intent destino = new Intent(context,NuevoPedidoActivity.class);
                destino.putExtra("Id_pedido", p.getId());
                PendingIntent pendingDestino = PendingIntent.getActivity(context, 0,destino,0);

                //@TODO: CANAL01 hardcodeado?
                Notification notification = new NotificationCompat.Builder(context,"CANAL01")
                        .setSmallIcon(R.drawable.envio)
                        .setContentTitle("Tu Pedido fue aceptado")
                        .setContentText(contenido)
                        .setAutoCancel(true)
                        .setContentIntent(pendingDestino)
                        .build();

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(99,notification);

            }
        }
        else if(intent.getAction().equals(ESTADO_EN_PREPARACION))
        {
            ArrayList<Integer> pedidosRealizados = intent.getIntegerArrayListExtra(id_pedidos_extra);
            if(pedidosRealizados != null)
            {
                String contenido = String.format("Se han realizado %d pedidos",pedidosRealizados.size());
                Intent destino = new Intent(context,HistorialPedidoActivity.class);
                PendingIntent pendingDestino = PendingIntent.getActivity(context, 0,destino,0);
                //TODO: CANAL01 hardcodeado
                Notification notification = new NotificationCompat.Builder(context,"CANAL01")
                        .setSmallIcon(R.drawable.retira)
                        .setContentTitle("Pedidos realizados")
                        .setContentText(contenido)
                        .setAutoCancel(true)
                        .setContentIntent(pendingDestino)
                        .build();

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(99,notification);

            }

        }
        else if(intent.getAction().equals(ESTADO_LISTO))
        {
            Integer pedidoListo = intent.getIntExtra(id_pedido_extra,-1);
            if(pedidoListo == -1) return;
            Pedido p = pedidoRepository.buscarPorId(pedidoListo);
            if(p == null) return;


            String contenido = String.format("%d - %s - %s",p.getId(),p.getFecha().toString(),p.getMailContacto());

            Intent destino = new Intent(context,HistorialPedidoActivity.class);
            PendingIntent pendingDestino = PendingIntent.getActivity(context, 0,destino,0);
            Notification notification = new NotificationCompat.Builder(context,"CANAL01")
                    .setSmallIcon(R.drawable.retira)
                    .setContentTitle("Pedido listo")
                    .setContentText(contenido)
                    .setAutoCancel(true)
                    .setContentIntent(pendingDestino)
                    .build();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(99,notification);
        }
        else throw new UnsupportedOperationException("Not yet implemented");
    }
}
