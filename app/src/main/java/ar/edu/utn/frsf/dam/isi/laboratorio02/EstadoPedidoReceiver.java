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

import java.util.Random;

import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.PedidoRepository;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Pedido;

public class EstadoPedidoReceiver extends BroadcastReceiver {
    public static final String paq = "ar.edu.utn.frsf.dam.isi.laboratorio02";
    public static final String ESTADO_ACEPTADO = paq+".ESTADO_ACEPTADO";
    public static final String ESTADO_CANCELADO = paq+".ESTADO_CANCELADO";
    public static final String ESTADO_EN_PREPARACION = paq+".ESTADO_EN_PREPARACION";
    public static final String ESTADO_LISTO = paq+".ESTADO_LISTO";

    PedidoRepository pedidoRepository = new PedidoRepository();

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(ESTADO_ACEPTADO))
        {
            Pedido p = pedidoRepository.buscarPorId(intent.getIntExtra("idPedido",-1));
            if(p != null)
            {
                String contenido = String.format("El costo sera de $%f\n Previsto el envio para %s",p.total(),p.getFecha().toString());

                //TODO: por alguna razon no puedo hacer q habra directamente el pedido, me lo abre vacio, no recipe el id pedido
                Intent destino = new Intent(context,NuevoPedidoActivity.class);
                //Intent destino = new Intent(context,HistorialPedidoActivity.class);
                //destino.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                destino.putExtra("Id_pedido", p.getId());
                System.out.println("Se envio Id pedido = "+p.getId().toString());
                PendingIntent pendingDestino = PendingIntent.getActivity(context, 0,destino,PendingIntent.FLAG_UPDATE_CURRENT);

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
        else throw new UnsupportedOperationException("Not yet implemented");
    }
}
