package ar.edu.utn.frsf.dam.isi.laboratorio02;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
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
import java.util.GregorianCalendar;
import java.util.List;

import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.AsyncProductoGET;
import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.AsyncProductoSelect;
import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.MyDatabase;
import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.PedidoRepository;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Pedido;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.PedidoConDetalles;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.PedidoDetalle;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Producto;

public class NuevoPedidoActivity extends AppCompatActivity {
    public static final String extraIdPedido  = "Id_pedido";
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
    List<Producto> listaProductos = null;
    PedidoRepository pedidoRepository;
    Pedido pedido;
    TextView tvTotalPedido;
    Button btHacerPedido;
    EditText etHoraSeleccionada;
    Double costoActual = 0.0;

    static final int segundos = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.nuevo_pedido);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        etCorreoElectronico = findViewById(R.id.etCorreoElectronico);
        String defaultCorreo = sharedPreferences.getString("etCorreoElectronico","");
        etCorreoElectronico.setText(defaultCorreo);

        btAgregarProducto = findViewById(R.id.btAgregarProducto);
        //lo desahibilito y lo habilito cuando reciba los productos
        btAgregarProducto.setEnabled(false);
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

        if(sharedPreferences.getBoolean("cbRetirar",true))
        {
            rbLocal.performClick();
        }
        else rbDomicilio.performClick();

        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_multiple_choice,android.R.id.text1,pedido.getDetalle());
        lvPedido.setAdapter(adapter);
        lvPedido.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        btHacerPedido.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.currentThread().sleep(segundos*1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Intent broadcastIntent = new Intent(NuevoPedidoActivity.this,EstadoPedidoReceiver.class);
                        broadcastIntent.setAction(EstadoPedidoReceiver.ESTADO_ACEPTADO);
                        MyDatabase db = MyDatabase.getInstance(NuevoPedidoActivity.this);
                        // buscar pedidos no aceptados y aceptarlos utomáticamente
                        List<Pedido> lista = null;
                        if(MainActivity.useDB){
                            lista = db.getPedidoDao().getAll();
                        }
                        else lista = pedidoRepository.getLista();

                        for(Pedido p: lista){
                            if(p.getEstado().equals(Pedido.Estado.REALIZADO))
                            {
                                broadcastIntent.putExtra("idPedido",p.getId());
                                p.setEstado(Pedido.Estado.ACEPTADO);

                                if(MainActivity.useDB) db.getPedidoDao().update(p);

                                sendBroadcast(broadcastIntent);
                            }
                        }
                    }

                };
                Thread unHilo = new Thread(r);
                unHilo.start();

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
                    if(ingresado.length>1)
                    {
                        minutos = Integer.valueOf(ingresado[1]);
                    }
                    else minutos = 0;//Si solo ingreso un numero lo considero como hora

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

                if(MainActivity.useDB){
                    final MyDatabase db = MyDatabase.getInstance(NuevoPedidoActivity.this);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final long id = db.getPedidoDao().insert(pedido);
                            for(PedidoDetalle pd : pedido.getDetalle()){
                                pd.setIdPedidoAsignado(id);
                                db.getPedidoDetalleDao().insert(pd);
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pedido = new Pedido();//Reinicio.
                                    adapter = new ArrayAdapter<>(NuevoPedidoActivity.this,android.R.layout.simple_list_item_1,android.R.id.text1,pedido.getDetalle());
                                    lvPedido.setAdapter(adapter);
                                    startActivity(intentHistorial);
                                }
                            });
                        }
                    }).start();
                }
                else{
                    pedidoRepository.guardarPedido(pedido);
                    pedido = new Pedido();//Reinicio.
                    adapter = new ArrayAdapter<>(NuevoPedidoActivity.this,android.R.layout.simple_list_item_1,android.R.id.text1,pedido.getDetalle());
                    lvPedido.setAdapter(adapter);
                    startActivity(intentHistorial);
                }
            }
        });

        btQuitarProducto.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                SparseBooleanArray checked = lvPedido.getCheckedItemPositions();
                List<PedidoDetalle> toRemove= new ArrayList<>();
                for(int i = 0;i < adapter.getCount();i++)
                {
                    if(checked.get(i))
                    {
                        PedidoDetalle pd =  adapter.getItem(i);
                        toRemove.add(pd);
                        //Le saco el check al view
                        lvPedido.setItemChecked(i,false);
                    }
                }
                for(PedidoDetalle pd : toRemove) adapter.remove(pd);

                adapter.notifyDataSetChanged();
            }
        });


        Intent intent = getIntent();
        final Integer id = intent.getIntExtra(extraIdPedido,-1);

        if(MainActivity.useDB){
            AsyncProductoSelect asyncProductoSelect = new AsyncProductoSelect(this, new AsyncProductoSelect.IProductoSelectCallback() {
                @Override
                public void callback(List<Producto> productos) {
                    if(productos!=null){
                        listaProductos = productos;
                        if(id == -1) btAgregarProducto.setEnabled(true);
                    }
                }
            });
            asyncProductoSelect.execute();
        }
        else {
            AsyncProductoGET asyncProductoGET = new AsyncProductoGET(this, new AsyncProductoGET.IProductoGETCallback() {
                @Override
                public void callback(List<Producto> productos) {
                    if (productos != null) {
                        listaProductos = productos;
                        if (id == -1) btAgregarProducto.setEnabled(true);
                    }
                }
            });
            asyncProductoGET.start();
        }

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

            lvPedido.setEnabled(false);

            btVolver.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(intentHistorial);
                }
            });

            if(MainActivity.useDB){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Pedido pedido = MainActivity.getPedidoById(NuevoPedidoActivity.this,id);
                        Log.d("VIENDODETALLE",pedido.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                terminarOnCreate(pedido);
                            }
                        });
                    }
                }).start();
            }
            else {
                terminarOnCreate(pedidoRepository.buscarPorId(id));
            }
        }
    }

    void terminarOnCreate(Pedido pedido){
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

    private Producto buscarProductoPorId(Integer id){
        if(listaProductos == null) return null;

        for(Producto p : listaProductos){
            if(p.getId().equals(id)) return p;
        }

        return null;
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
                //PedidoDetalle pd = new PedidoDetalle(cantidad,productoRepository.buscarPorId(id));
                PedidoDetalle pd = new PedidoDetalle(cantidad,buscarProductoPorId(id));
                pedido.agregarDetalle(pd);
                costoActual+= pd.getProducto().getPrecio()*cantidad;
                tvTotalPedido.setText(String.format("Total del pedido: $%f",costoActual));
                adapter.notifyDataSetChanged();
            }
        }
    }
}
