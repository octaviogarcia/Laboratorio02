package ar.edu.utn.frsf.dam.isi.laboratorio02.modelo;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ar.edu.utn.frsf.dam.isi.laboratorio02.NuevoPedidoActivity;
import ar.edu.utn.frsf.dam.isi.laboratorio02.R;

public class PedidoAdapter extends ArrayAdapter<Pedido> {
    private Context ctx;
    private List<Pedido> datos;

    public PedidoAdapter(@NonNull Context context, @NonNull List<Pedido> objects) {
        super(context, 0, objects);
        this.ctx = context;
        this.datos = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        View fila_historial = convertView;
        if(fila_historial == null){
            fila_historial = inflater.inflate(R.layout.fila_historial, parent, false);
        }

        PedidoHolder pediHolder = (PedidoHolder) fila_historial.getTag();
        if(pediHolder == null)
        {
            pediHolder = new PedidoHolder(fila_historial);
            fila_historial.setTag(pediHolder);
        }


        Pedido pedido = (Pedido) super.getItem(position);
        pediHolder.tvMailPedido.setText("Contacto: "+pedido.getMailContacto());
        pediHolder.tvHoraEntrega.setText("Fecha de Entrega: "+pedido.getFecha().toString());
        pediHolder.tvCantidadItems.setText("Items: "+new Integer(pedido.getDetalle().size()).toString());
        pediHolder.tvPrecio.setText("A pagar $: "+pedido.total().toString());

        //Ponemos esta imagen por que no encontramos la pedida
        pediHolder.tipoEntrega.setImageResource(R.drawable.retira);

        switch(pedido.getEstado())
        {
            case LISTO:
                pediHolder.tvEstado.setTextColor(Color.DKGRAY);
                break;
            case ACEPTADO:
                pediHolder.tvEstado.setTextColor(Color.GREEN);
                break;
            case ENTREGADO:
                pediHolder.tvEstado.setTextColor(Color.BLUE);
                break;
            case REALIZADO:
                pediHolder.tvEstado.setTextColor(Color.BLUE);
                break;
            case CANCELADO:
            case RECHAZADO:
                pediHolder.tvEstado.setTextColor(Color.RED);
                break;
            case EN_PREPARACION:
                pediHolder.tvEstado.setTextColor(Color.MAGENTA);
                break;
            default:
                throw new RuntimeException("UNREACHABLE");
        }
        pediHolder.tvEstado.setText(pedido.getEstado().toString());
        pediHolder.btnCancelar.setTag(pedido);
        pediHolder.btnDetalles.setTag(pedido);

        pediHolder.btnCancelar.setOnClickListener(
            new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Pedido p = (Pedido) view.getTag();
                    if(     p.getEstado().equals(Pedido.Estado.REALIZADO)||
                            p.getEstado().equals(Pedido.Estado.ACEPTADO) ||
                            p.getEstado().equals(Pedido.Estado.EN_PREPARACION))
                    {
                        p.setEstado(Pedido.Estado.CANCELADO);
                        PedidoAdapter.this.notifyDataSetChanged();
                        return;
                    }
                }
            }
        );
        final Intent intentNuevoPedido = new Intent(this.ctx, NuevoPedidoActivity.class);
        pediHolder.btnDetalles.setOnClickListener(
        new Button.OnClickListener() {
              @Override
              public void onClick(View view) {
                  intentNuevoPedido.putExtra("Id pedido", ((Pedido)view.getTag()).getId());
                  ctx.startActivity(intentNuevoPedido);
              }
          }
        );
        return fila_historial;
    }
}
