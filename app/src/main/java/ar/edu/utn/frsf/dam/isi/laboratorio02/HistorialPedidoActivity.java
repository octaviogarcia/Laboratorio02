package ar.edu.utn.frsf.dam.isi.laboratorio02;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.MyDatabase;
import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.PedidoDao;
import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.PedidoRepository;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Pedido;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.PedidoAdapter;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.PedidoConDetalles;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.PedidoDetalle;


public class HistorialPedidoActivity extends AppCompatActivity {
    private Button btnHistorialNuevo;
    private Button btnHistorialMenu;
    private TextView txtPedidos;
    private ListView lstHistorialPedidos;
    private PedidoRepository pedidoRepository;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historial_pedido);
        final Intent intentNuevoPedido = new Intent(this, NuevoPedidoActivity.class);
        final Intent intentMenuPrincipal = new Intent(this, MainActivity.class);

        btnHistorialMenu = findViewById(R.id.btnHistorialMenu);
        btnHistorialNuevo = findViewById(R.id.btnHistorialNuevo);
        txtPedidos = findViewById(R.id.txtPedidos);
        lstHistorialPedidos = findViewById(R.id.lstHistorialPedidos);

        pedidoRepository = new PedidoRepository();


        if(MainActivity.useDB){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MyDatabase db = MyDatabase.getInstance(HistorialPedidoActivity.this);
                    PedidoDao pdao = db.getPedidoDao();
                    final List<Pedido> pedidos = pdao.getAll();
                    for(Pedido p : pedidos){
                        List<PedidoConDetalles> pcd = pdao.buscarPorIdConDetalles(p.getId());
                        List<PedidoDetalle> pedidoDetalles = pcd.get(0).detalle;
                        p.setDetalle(pedidoDetalles);
                        for(PedidoDetalle pd : pedidoDetalles){
                            pd.setPedido(p);
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new PedidoAdapter(HistorialPedidoActivity.this,pedidos);
                            lstHistorialPedidos.setAdapter((ListAdapter) adapter);
                        }
                    });
                }
            }).start();

        }
        else {
            final List<Pedido> lstPedido = pedidoRepository.getLista();
            adapter = new PedidoAdapter(this,lstPedido);
            lstHistorialPedidos.setAdapter((ListAdapter) adapter);
        }

        btnHistorialNuevo.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(intentNuevoPedido);
            }
        });

        btnHistorialMenu.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(intentMenuPrincipal);
            }
        });

        lstHistorialPedidos.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Pedido p = (Pedido) adapterView.getItemAtPosition(i);
                intentNuevoPedido.putExtra(NuevoPedidoActivity.extraIdPedido, p.getId());
                startActivity(intentNuevoPedido);
                return false;
            }
        });
    }
}
