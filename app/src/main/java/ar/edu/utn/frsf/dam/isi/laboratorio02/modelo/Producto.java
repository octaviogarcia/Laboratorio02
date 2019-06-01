package ar.edu.utn.frsf.dam.isi.laboratorio02.modelo;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.util.Log;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import ar.edu.utn.frsf.dam.isi.laboratorio02.dao.AsyncCategoriaGET;

@Entity
@JsonAdapter(Producto.ProductoJsonAdapter.class)
public class Producto {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private String nombre;
    private String descripcion;
    private Double precio;
    @Embedded(prefix = "cat_")
    private Categoria categoria;

    @Ignore
    public Producto(String nombre, String descripcion, Double precio, Categoria categoria) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoria = categoria;
    }

    @Ignore
    public Producto(String nombre, Double precio, Categoria categoria) {
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
    }

    @Ignore
    public Producto(Integer id, String nombre, String descripcion, Double precio, Categoria categoria) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoria = categoria;
    }

    public Producto() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }


    @Override
    public String toString() {
        return nombre+ "( $" + precio +")";

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Producto producto = (Producto) o;
        return Objects.equals(id, producto.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    public class ProductoJsonAdapter extends TypeAdapter<Producto> {

        @Override
        public void write(JsonWriter out, Producto value) throws IOException {
            out.beginObject();
            out.name("id");
            out.value(value.id);
            out.name("nombre");
            out.value(value.nombre);
            out.name("descripcion");
            out.value(value.descripcion);
            out.name("precio");
            out.value(value.precio);
            out.name("categoriaId");
            out.value(value.categoria.getId());
            out.endObject();
        }

        @Override
        public Producto read(JsonReader in) throws IOException {
            AsyncCategoriaGET asyncCategoriaGET = new AsyncCategoriaGET();
            asyncCategoriaGET.execute();
            List<Categoria> categorias = null;

            Producto p = new Producto(null,null,null,null,null);

            in.beginObject();

            try {
                categorias = asyncCategoriaGET.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            while(in.hasNext() && (
                    p.getId() == null ||
                    p.getCategoria() == null ||
                    p.getPrecio() == null ||
                    p.getDescripcion() == null  ||
                    p.getNombre() == null))
            {
                String field = in.nextName();
                if(field.equals("nombre")) p.setNombre(in.nextString());
                else if(field.equals("descripcion")) p.setDescripcion(in.nextString());
                else if(field.equals("precio")) p.setPrecio(in.nextDouble());
                else if(field.equals("id")) p.setId(in.nextInt());
                else if(field.equals("categoriaId")) {
                    Categoria categoria = null;
                    Integer categoriaID = in.nextInt();
                    for(Categoria c : categorias)
                    {
                        if(c.getId().equals(categoriaID))
                        {
                            categoria = c;
                            break;
                        }
                    }
                    p.setCategoria(categoria);
                }
                else in.skipValue();//No matchea ninguna propiedas, skipeo
            }


            in.endObject();

            Log.d("ProductoJsonAdapter",String.format("Producto{%d,%s,%s,%f,%s",
                    p.getId(),p.getNombre(),p.getDescripcion(),p.getPrecio(),p.getCategoria().toString()));

            return p;
        }

    }
}
