package ar.edu.utn.frsf.dam.isi.laboratorio02;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.ArrayList;

import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.MyDatabase;
import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.PedidoRepository;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Pedido;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.PedidoConDetalles;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.PedidoDetalle;

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
    public void onReceive(final Context context, final Intent intent) {
        if(intent.getAction().equals(ESTADO_ACEPTADO)) {
            final Integer pedidoid = intent.getIntExtra(id_pedido_extra,-1);
            if(MainActivity.useDB){
                ESTADO_ACEPTADO_DB temp = new ESTADO_ACEPTADO_DB(context,intent,pedidoid);
                temp.execute();
            }
            else{
                handle_ESTADO_ACEPTADO(context,intent,pedidoRepository.buscarPorId(pedidoid));
            }
        }
        else if(intent.getAction().equals(ESTADO_EN_PREPARACION)) {
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
                notificationManager.notify(33,notification);
            }
        }
        else if(intent.getAction().equals(ESTADO_LISTO)) {
            final Integer pedidoListo = intent.getIntExtra(id_pedido_extra,-1);
            if(pedidoListo == -1) return;
            if(MainActivity.useDB){
                new ESTADO_LISTO_DB(context,intent,pedidoListo).execute();
            }
            else{
                handle_ESTADO_LISTO(context,intent,pedidoRepository.buscarPorId(pedidoListo));
            }
        }
        else throw new UnsupportedOperationException("Not yet implemented");
    }

    void handle_ESTADO_ACEPTADO(Context context,Intent intent,Pedido p){
        if(p != null)
        {
            Log.d("BROADCASTRECEIVER",p.toString());
            String contenido = String.format("El costo sera de $%f\n Previsto el envio para %s",p.total(),p.getFecha().toString());

            Intent destino = new Intent(context,NuevoPedidoActivity.class);
            destino.putExtra(NuevoPedidoActivity.extraIdPedido, p.getId());
            PendingIntent pendingDestino = PendingIntent.getActivity(context, 0,destino,0);

            //@TODO: CANAL01 hardcodeado?
            Notification notification = new NotificationCompat.Builder(context,"CANAL01")
                    .setSmallIcon(R.drawable.envio)
                    .setContentTitle("Tu Pedido fue aceptado")
                    .setContentText(contenido)
                    .setAutoCancel(true)
                    .setContentIntent(pendingDestino)
                    .build();

            Log.d("BROADCASTRECEIVER",String.valueOf(destino.getIntExtra(NuevoPedidoActivity.extraIdPedido,-1)));

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(14,notification);
        }
    }
    void handle_ESTADO_LISTO(Context context,Intent intent,Pedido p){
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
        notificationManager.notify(17,notification);
    }

    private class ESTADO_LISTO_DB extends AsyncTask<Void, Void, Pedido> {
        Integer id = null;
        Context context = null;
        Intent intent = null;

        public ESTADO_LISTO_DB(Context _context,Intent _intent,Integer _id){
            context = _context;
            intent = _intent;
            id = _id;
        }

        @Override
        protected Pedido doInBackground(Void... voids) {
            return MainActivity.getPedidoById(context,id);
        }

        @Override
        protected void onPostExecute(Pedido pedido) {
            handle_ESTADO_LISTO(context,intent,pedido);
        }
    };

    private class ESTADO_ACEPTADO_DB extends AsyncTask<Void, Void, Pedido> {
        Integer id = null;
        Context context = null;
        Intent intent = null;

        public ESTADO_ACEPTADO_DB(Context _context,Intent _intent,Integer _id){
            context = _context;
            intent = _intent;
            id = _id;
        }

        @Override
        protected Pedido doInBackground(Void... voids) {
            return MainActivity.getPedidoById(context,id);
        }

        @Override
        protected void onPostExecute(Pedido pedido) {
            handle_ESTADO_ACEPTADO(context,intent,pedido);
        }
    }

}
