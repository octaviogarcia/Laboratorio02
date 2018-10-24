package ar.edu.utn.frsf.dam.isi.laboratorio02;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

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
                String text = String.format("Pedido para %s ha cambiado de estado a %s",p.getMailContacto(),p.getEstado().toString());
                Toast.makeText(context, text,Toast.LENGTH_LONG).show();
            }
        }
        else throw new UnsupportedOperationException("Not yet implemented");
    }
}
