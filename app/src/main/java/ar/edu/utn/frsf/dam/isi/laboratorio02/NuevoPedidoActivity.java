package ar.edu.utn.frsf.dam.isi.laboratorio02;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.PedidoRepository;
import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.ProductoRepository;

public class NuevoPedidoActivity extends AppCompatActivity {
    Button btAgregarProducto;
    RadioGroup rbgPedido;
    ProductoRepository productoRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nuevo_pedido);
        btAgregarProducto = findViewById(R.id.btAgregarProducto);
        rbgPedido = findViewById(R.id.rbgPedido);
        productoRepository = new ProductoRepository();
        final Intent intentListaProductos = new Intent(this, ListaProductosActivity.class);
        btAgregarProducto.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(intentListaProductos,CodigosLlamadas.NUEVOPEDIDO_A_LISTARPRODUCTOS.ordinal());
            }
        });
    }
    @Override
    protected void onActivityResult(int request,int result, Intent data)
    {//Manejo el retorno de listar pedidos
        if(result == Activity.RESULT_OK)
        {
            if(request == CodigosLlamadas.NUEVOPEDIDO_A_LISTARPRODUCTOS.ordinal())
            {
                Integer cantidad = data.getIntExtra("cantidad",1);
                Integer id = data.getIntExtra("producto",0);
                RadioButton rb = findViewById(id);
                if(rb == null)
                {
                    rb = new RadioButton(this);
                    rb.setId(id);
                    rbgPedido.addView(rb);
                }
                else
                {
                    Integer cantVieja = (Integer) rb.getTag();
                    cantidad += cantVieja;
                }

                rb.setText(cantidad.toString()+"*"+ productoRepository.buscarPorId(id).getNombre());
                rb.setTag(cantidad);

                return;
            }
            else{}
        }
        else {
            //fallo
        }

    }
}
