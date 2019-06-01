package ar.edu.utn.frsf.dam.isi.laboratorio02;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.MyDatabase;
import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.PedidoDao;
import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.PedidoRepository;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Pedido;

public class RestoMessagingService extends FirebaseMessagingService {
    PedidoRepository pedidoRepository = null;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(getClass().getSimpleName(), "Received firebase message");
        Map<String, String> messageData = remoteMessage.getData();
        if (pedidoRepository == null) pedidoRepository = new PedidoRepository();

        String id_pedido_str = null;
        String nuevo_estado_str = null;

        if (messageData.containsKey("ID_PEDIDO")) id_pedido_str = messageData.get("ID_PEDIDO");
        else Log.d(getClass().getSimpleName(), "Recibio msg sin ID_PEDIDO");
        if (messageData.containsKey("NUEVO_ESTADO"))
            nuevo_estado_str = messageData.get("NUEVO_ESTADO");
        else Log.d(getClass().getSimpleName(), "Recibio msg sin NUEVO_ESTADO");

        if (id_pedido_str == null || nuevo_estado_str == null) return;

        {
            String toprint = "";
            for (String key : messageData.keySet()) {
                toprint += " [" + key + " : " + messageData.get(key) + "] ";
            }
            Log.d(getClass().getSimpleName(), toprint);
        }


        Pedido.Estado nuevo_estado = null;
        Integer id_pedido = null;

        try{
            id_pedido = Integer.parseInt(id_pedido_str);
            nuevo_estado = Pedido.Estado.valueOf(nuevo_estado_str);
        }
        catch(Exception e) {
            String error = String.format("id pedido = %s nuevo_estado = %s",id_pedido_str,nuevo_estado_str);
            Log.d(getClass().getSimpleName(),"Recibio msg invalido "+error);
            return;
        }

        if(!nuevo_estado.equals(Pedido.Estado.LISTO))
        {
            Log.d(getClass().getSimpleName(),"Recibio msg con estado no LISTO ("+nuevo_estado_str+")");
            return;
        }

        Pedido pedido = null;
        if(MainActivity.useDB) pedido = MainActivity.getPedidoById(getBaseContext(),id_pedido);
        else pedido = pedidoRepository.buscarPorId(id_pedido);

        if(pedido == null)
        {
            Log.d(getClass().getSimpleName(),"Pedido "+id_pedido_str+" no encontrado");
            return;
        }

        if(pedido.getEstado().equals(Pedido.Estado.EN_PREPARACION))
        {
            pedido.setEstado(Pedido.Estado.LISTO);
            if(MainActivity.useDB) {
                PedidoDao pdao = MyDatabase.getInstance(getBaseContext()).getPedidoDao();
                pdao.update(pedido);
            }
        }
        else
        {
            Log.d(getClass().getSimpleName(),"Pedido "+id_pedido_str+" no estaba en preparacion");
            return;
        }

        Intent broadcastIntent = new Intent(this,EstadoPedidoReceiver.class);
        broadcastIntent.setAction(EstadoPedidoReceiver.ESTADO_LISTO);
        broadcastIntent.putExtra(EstadoPedidoReceiver.id_pedido_extra,id_pedido);
        sendBroadcast(broadcastIntent);
    }
}
