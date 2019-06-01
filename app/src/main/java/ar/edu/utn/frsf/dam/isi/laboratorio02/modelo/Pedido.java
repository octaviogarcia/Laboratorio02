package ar.edu.utn.frsf.dam.isi.laboratorio02.modelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Pedido {

    public enum Estado { REALIZADO, ACEPTADO, RECHAZADO,EN_PREPARACION,LISTO,ENTREGADO,CANCELADO}

    private Integer id;
    private Date fecha;
    private List<PedidoDetalle> detalle;
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

    public Pedido(Date fecha, List<PedidoDetalle> detalle, Estado estado, String direccionEnvio, String mailContacto, Boolean retirar) {
        this();
        this.fecha = fecha;
        this.detalle = detalle;
        this.estado = estado;
        this.direccionEnvio = direccionEnvio;
        this.mailContacto = mailContacto;
        this.retirar = retirar;
    }

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

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", fecha=" + fecha +
                ", estado=" + estado +
                ", direccionEnvio='" + direccionEnvio + '\'' +
                ", mailContacto='" + mailContacto + '\'' +
                ", retirar=" + retirar +
                '}';
    }

    public Double total(){
        Double total = 0.0;
        for(PedidoDetalle det: detalle){
            total+=det.getProducto().getPrecio()*det.getCantidad();
        }
        return total;
    }
}