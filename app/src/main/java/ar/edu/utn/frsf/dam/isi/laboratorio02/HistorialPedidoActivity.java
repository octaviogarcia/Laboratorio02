package ar.edu.utn.frsf.dam.isi.laboratorio02;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.PedidoRepository;
import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.ProductoRepository;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Pedido;


public class HistorialPedidoActivity extends AppCompatActivity {
    private Button btnHistorialNuevo;
    private Button btnHistorialMenu;
    private TextView txtPedidos;
    private ListView lstHistorialPedidos;
    private PedidoRepository pedidoRepository;
    private ProductoRepository productoRepository;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historial_pedido);

        btnHistorialMenu = findViewById(R.id.btnHistorialMenu);
        btnHistorialNuevo = findViewById(R.id.btnHistorialNuevo);
        txtPedidos = findViewById(R.id.txtPedidos);
        lstHistorialPedidos = findViewById(R.id.lstHistorialPedidos);

        pedidoRepository = new PedidoRepository();

        final List<Pedido> lstPedido = pedidoRepository.getLista();

        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,android.R.id.text1,pedido.getDetalle());
        lstHistorialPedidos.setAdapter(adapter);


    }
}
