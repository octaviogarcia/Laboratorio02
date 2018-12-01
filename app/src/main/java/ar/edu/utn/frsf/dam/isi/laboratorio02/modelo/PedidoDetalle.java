package ar.edu.utn.frsf.dam.isi.laboratorio02.modelo;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys =
@ForeignKey(entity = Pedido.class,
        parentColumns = "id",
        childColumns = "idPedidoAsignado",
        onDelete = CASCADE))
public class PedidoDetalle {

    private static int ID_DETALLE =1;
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private Integer cantidad;

    public Long getIdPedidoAsignado() {
        return idPedidoAsignado;
    }

    public void setIdPedidoAsignado(Long idPedidoAsignado) {
        this.idPedidoAsignado = idPedidoAsignado;
    }

    private Long idPedidoAsignado;

    @Embedded(prefix = "prod_")
    private Producto producto;

    @Embedded(prefix = "ped_")
    private Pedido pedido;

    @Ignore
    public PedidoDetalle(Integer cantidad, Producto producto) {
        id =ID_DETALLE++;
        this.cantidad = cantidad;
        this.producto = producto;
    }

    public PedidoDetalle(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
//        pedido.agregarDetalle(this);
//        this.idPedidoAsignado = Long.valueOf(pedido.getId());
    }



    @Override
    public String toString() {
        return "PedidoDetalle{" +
                "id=" + id +
                ", cantidad=" + cantidad +
                ", idPedidoAsignado=" + idPedidoAsignado +
                ", producto=" + producto +
                ", pedido=" + pedido +
                '}';
    }
}
