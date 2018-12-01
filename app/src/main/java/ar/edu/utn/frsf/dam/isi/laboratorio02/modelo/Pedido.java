package ar.edu.utn.frsf.dam.isi.laboratorio02.modelo;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.EstadoConverter;
import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.FechaConverter;

@Entity
public class Pedido {

    public enum Estado { REALIZADO, ACEPTADO, RECHAZADO,EN_PREPARACION,LISTO,ENTREGADO,CANCELADO}

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @TypeConverters(FechaConverter.class)
    private Date fecha;

    @Ignore
    private List<PedidoDetalle> detalle;

    @TypeConverters(EstadoConverter.class)
    private Estado estado;
    private String direccionEnvio;
    private String mailContacto;
    private Boolean retirar;

    public String getDireccionEnvio() {
        return direccionEnvio;
    }

    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }

    public String getMailContacto() {
        return mailContacto;
    }

    public void setMailContacto(String mailContacto) {
        this.mailContacto = mailContacto;
    }

    public Boolean getRetirar() {
        return retirar;
    }

    public void setRetirar(Boolean retirar) {
        this.retirar = retirar;
    }

    public Pedido() {
        this.detalle =new ArrayList<>();
    }

    @Ignore
    public Pedido(Date fecha, List<PedidoDetalle> detalle, Estado estado, String direccionEnvio, String mailContacto, Boolean retirar) {
        this();
        this.fecha = fecha;
        this.detalle = detalle;
        this.estado = estado;
        this.direccionEnvio = direccionEnvio;
        this.mailContacto = mailContacto;
        this.retirar = retirar;
    }

    @Ignore
    public Pedido(Date fecha, Estado estado) {
        this();
        this.fecha = fecha;
        this.estado = estado;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public List<PedidoDetalle> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<PedidoDetalle> detalle) {
        this.detalle = detalle;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public void agregarDetalle(PedidoDetalle det){
        if(this.detalle == null) this.detalle = new ArrayList<>();
        this.detalle.add(det);
    }

    public void quitarDetalle(PedidoDetalle det){
        if(this.detalle != null) this.detalle.remove(det);
    }

    public Double total(){
        Double total = 0.0;
        for(PedidoDetalle det: detalle){
            total+=det.getProducto().getPrecio()*det.getCantidad();
        }
        return total;
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", fecha=" + fecha +
                ", detalle=" + detalle +
                ", estado=" + estado +
                ", direccionEnvio='" + direccionEnvio + '\'' +
                ", mailContacto='" + mailContacto + '\'' +
                ", retirar=" + retirar +
                '}';
    }
}