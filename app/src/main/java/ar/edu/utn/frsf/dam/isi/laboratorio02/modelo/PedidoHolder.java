package ar.edu.utn.frsf.dam.isi.laboratorio02.modelo;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import ar.edu.utn.frsf.dam.isi.laboratorio02.R;

public class PedidoHolder {
    public TextView tvMailPedido;
    public TextView tvHoraEntrega;
    public TextView tvCantidadItems;
    public TextView tvPrecio;
    public TextView tvEstado;
    public ImageView tipoEntrega;
    public Button btnCancelar;
    public Button btnDetalles;

    PedidoHolder(View fila_historial)
    {
        tvMailPedido = fila_historial.findViewById(R.id.tvMailPedido);
        tvHoraEntrega = fila_historial.findViewById(R.id.tvHoraEntrega);
        tvCantidadItems = fila_historial.findViewById(R.id.tvCantidadItems);
        tvPrecio = fila_historial.findViewById(R.id.tvPrecio);
        tvEstado = fila_historial.findViewById(R.id.tvEstado);
        tipoEntrega = fila_historial.findViewById(R.id.tipoEntrega);
        btnCancelar = fila_historial.findViewById(R.id.btnCancelar);
        btnDetalles = fila_historial.findViewById(R.id.btnDetalles);
    }
}
