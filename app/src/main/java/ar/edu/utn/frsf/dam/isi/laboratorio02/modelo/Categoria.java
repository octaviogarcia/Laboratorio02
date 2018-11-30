package ar.edu.utn.frsf.dam.isi.laboratorio02.modelo;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Categoria {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private String nombre;

    @Ignore
    public Categoria(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    @Ignore
    public Categoria(String nombre) {
        this.nombre = nombre;
    }

    public Categoria() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categoria categoria = (Categoria) o;
        return Objects.equals(id, categoria.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Categoria: " +nombre;
    }
}
