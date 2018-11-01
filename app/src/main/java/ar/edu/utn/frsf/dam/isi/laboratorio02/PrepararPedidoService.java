package ar.edu.utn.frsf.dam.isi.laboratorio02;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.PedidoRepository;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Pedido;

public class PrepararPedidoService extends IntentService {

    private final static int segundos = 20;
    public PrepararPedidoService() {
        super("PrepararPedidoService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            Thread.currentThread().sleep(segundos*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PedidoRepository pedidoRepository = new PedidoRepository();
        List<Pedido> listaPedidos = pedidoRepository.getLista();

        Intent broadcastIntent = new Intent(PrepararPedidoService.this,EstadoPedidoReceiver.class);
        broadcastIntent.setAction(EstadoPedidoReceiver.ESTADO_EN_PREPARACION);

        ArrayList<Integer> pedidosRealizados = new ArrayList<>();
        for(Pedido p : listaPedidos)
        {
            if(p.getEstado().equals(Pedido.Estado.ACEPTADO))
            {
                p.setEstado(Pedido.Estado.EN_PREPARACION);
                pedidosRealizados.add(p.getId());
            }
        }
        broadcastIntent.putIntegerArrayListExtra("pedidosRealizados",pedidosRealizados);
        sendBroadcast(broadcastIntent);
    }
}
