package ar.edu.utn.frsf.dam.isi.laboratorio02;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.PedidoRepository;
import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.ProductoRepository;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Pedido;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.PedidoDetalle;

public class NuevoPedidoActivity extends AppCompatActivity {
    EditText etCorreoElectronico;
    Button btAgregarProducto;
    Button btQuitarProducto;
    Button btVolver;
    ListView lvPedido;
    ArrayAdapter<PedidoDetalle> adapter;
    RadioButton rbLocal;
    RadioButton rbDomicilio;
    RadioGroup rbgModoDeEntrega;
    EditText etDireccion;
    ProductoRepository productoRepository;
    PedidoRepository pedidoRepository;
    Pedido pedido;
    TextView tvTotalPedido;
    Button btHacerPedido;
    EditText etHoraSeleccionada;
    Double costoActual = 0.0;

    Integer selectedIndex = -1;
    View selectedView = null;

    int defaultBackColor = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nuevo_pedido);
        etCorreoElectronico = findViewById(R.id.etCorreoElectronico);
        btAgregarProducto = findViewById(R.id.btAgregarProducto);
        btQuitarProducto = findViewById(R.id.btQuitarProducto);
        lvPedido = findViewById(R.id.lvPedido);
        rbLocal = findViewById(R.id.rbLocal);
        rbDomicilio = findViewById(R.id.rbDomicilio);
        etDireccion = findViewById(R.id.etDireccion);
        tvTotalPedido = findViewById(R.id.tvTotalPedido);
        btHacerPedido = findViewById(R.id.btHacerPedido);
        etHoraSeleccionada = findViewById(R.id.etHoraSolicitada);
        rbgModoDeEntrega = findViewById(R.id.rbgModoDeEntrega);
        btVolver = findViewById(R.id.btVolver);

        final Intent intentHistorial = new Intent(this, HistorialPedidoActivity.class);
        final Intent intentMain = new Intent(this,MainActivity.class);
        Intent intent = getIntent();

        productoRepository = new ProductoRepository();
        pedidoRepository = new PedidoRepository();
        pedido = new Pedido();

        final Intent intentListaProductos = new Intent(this, ListaProductosActivity.class);
        btAgregarProducto.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(intentListaProductos,CodigosLlamadas.NUEVOPEDIDO_A_LISTARPRODUCTOS.ordinal());
            }
        });

        rbLocal.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etDireccion.setEnabled(!isChecked);
            }
        });

        btVolver.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentMain);
            }
        });
        rbLocal.performClick();

        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,android.R.id.text1,pedido.getDetalle());
        lvPedido.setAdapter(adapter);
        lvPedido.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);


        btHacerPedido.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> errores = new ArrayList<>();
                String correo = etCorreoElectronico.getText().toString();
                if(correo.isEmpty())
                {
                    errores.add("Se necesita un correo electronico");
                }

                if(rbDomicilio.isChecked())
                {
                    String dir = etDireccion.getText().toString();
                    if(dir.isEmpty())
                    {
                        errores.add("Se necesita de una direccion");
                    }
                }

                String textohora = etHoraSeleccionada.getText().toString();
                Integer hora = 0;
                Integer minutos = 0;
                if(textohora.isEmpty())
                {
                    errores.add("Es necesario ingresar una hora solicitada");
                }
                else
                {
                    String[] ingresado = textohora.split(":");
                    hora = Integer.valueOf(ingresado[0]);
                    minutos = Integer.valueOf(ingresado[1]);
                    if(hora<0 || hora>23)
                    {
                        errores.add("La hora ingresada ("+hora+") es incorrecta");
                    }
                    if(minutos<0 || minutos>59)
                    {
                        errores.add("Los minutos ("+minutos+") son incorrectos");
                    }
                }

                if(pedido.getDetalle().size()==0)
                {
                    errores.add("No hay productos seleccionados");
                }
                if(errores.size()>0)
                {
                    StringBuilder concat = new StringBuilder();
                    for(String e : errores)
                    {
                        concat.append(e).append("\n");
                    }
                    Toast.makeText(NuevoPedidoActivity.this, concat.toString(),Toast.LENGTH_LONG).show();
                    return;
                }
                GregorianCalendar calendario = new GregorianCalendar();
                calendario.set(GregorianCalendar.HOUR_OF_DAY,hora);
                calendario.set(GregorianCalendar.MINUTE,minutos);
                calendario.set(GregorianCalendar.SECOND,0);
                pedido.setFecha(calendario.getTime());

                pedido.setMailContacto(correo);

                if(rbLocal.isChecked()) pedido.setRetirar(true);
                else if(rbDomicilio.isChecked())
                {
                    pedido.setDireccionEnvio(etDireccion.getText().toString());
                    pedido.setRetirar(false);
                }
                else
                {
                    throw new RuntimeException("UNREACHABLE");
                }

                pedido.setEstado(Pedido.Estado.REALIZADO);
                pedidoRepository.guardarPedido(pedido);

                pedido = new Pedido();//Reinicio.
                adapter = new ArrayAdapter<>(NuevoPedidoActivity.this,android.R.layout.simple_list_item_1,android.R.id.text1,pedido.getDetalle());
                lvPedido.setAdapter(adapter);

                startActivity(intentHistorial);
            }
        });

        lvPedido.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(selectedIndex == i)
                {//Si estaba seleccionado, lo deselecciono
                    view.setBackgroundColor(defaultBackColor);

                    selectedIndex = -1;
                    selectedView = null;
                }
                else if(selectedIndex == -1)
                {//Si no habia nada seleccionado, lo selecciono
                    selectedIndex = i;
                    selectedView = view;

                    defaultBackColor = view.getSolidColor();

                    view.setBackgroundColor(Color.YELLOW);
                }
                else
                {//Ya habia algo seleccionado, cambio la seleccion
                    selectedView.setBackgroundColor(defaultBackColor);

                    selectedIndex = i;
                    selectedView = view;
                    defaultBackColor = view.getSolidColor();

                    view.setBackgroundColor(Color.YELLOW);
                }
                return false;
            }
        });

        btQuitarProducto.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedIndex != -1)
                {
                    //restauro el color al item por que se va a reusar para el que "viene" despues
                    selectedView.setBackgroundColor(defaultBackColor);

                    PedidoDetalle p = adapter.getItem(selectedIndex);
                    adapter.remove(p);
                    selectedIndex = -1;
                    selectedView = null;
                    adapter.notifyDataSetChanged();
                }
            }
        });



        Integer id = intent.getIntExtra("Id pedido",-1);
        if(id != -1){
            etCorreoElectronico.setEnabled(false);
            btAgregarProducto.setEnabled(false);
            rbgModoDeEntrega.setEnabled(false);
            rbDomicilio.setEnabled(false);
            rbLocal.setEnabled(false);
            etDireccion.setEnabled(false);
            etHoraSeleccionada.setEnabled(false);

            btHacerPedido.setEnabled(false);
            btQuitarProducto.setEnabled(false);

            btVolver.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(intentHistorial);
                }
            });

            Pedido pedido = pedidoRepository.buscarPorId(id);

            etCorreoElectronico.setText(pedido.getMailContacto());
            etHoraSeleccionada.setText(pedido.getFecha().toString());
            if(pedido.getRetirar()){
                rbLocal.setChecked(true);
            }
            else{
                rbDomicilio.setChecked(true);
                etDireccion.setText(pedido.getDireccionEnvio());
                etDireccion.setEnabled(false);
            }

            adapter.addAll(pedido.getDetalle());
            adapter.notifyDataSetChanged();
        }
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
                for(PedidoDetalle pd : pedido.getDetalle())
                {
                    if(id.equals(pd.getProducto().getId()))
                    {
                        pd.setCantidad(pd.getCantidad()+cantidad);
                        costoActual+=cantidad*pd.getProducto().getPrecio();
                        tvTotalPedido.setText(String.format("Total del pedido: $%f",costoActual));
                        adapter.notifyDataSetChanged();
                        return;
                    }
                }
                PedidoDetalle pd = new PedidoDetalle(cantidad,productoRepository.buscarPorId(id));
                pedido.agregarDetalle(pd);
                costoActual+= pd.getProducto().getPrecio()*cantidad;
                tvTotalPedido.setText(String.format("Total del pedido: $%f",costoActual));
                adapter.notifyDataSetChanged();
            }
        }
    }
}
