package ar.edu.utn.frsf.dam.isi.laboratorio02.dao;

import android.os.Build;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Categoria;
import ar.edu.utn.frsf.dam.isi.laboratorio02.modelo.Producto;

public class ProductoRepository {

    private static List<Producto> LISTA_PRODUCTOS = new ArrayList<>();
    private static List<Categoria> CATEGORIAS_PRODUCTOS = new ArrayList<>();
    private static boolean FLAG_INICIALIZADO = false;

    private static void inicializar(){
        int id = 0;
        Random rand = new Random();
            CATEGORIAS_PRODUCTOS.add(new Categoria(1,"Entrada"));
            CATEGORIAS_PRODUCTOS.add(new Categoria(2,"Plato Principal"));
            CATEGORIAS_PRODUCTOS.add(new Categoria(3,"Postre"));
            CATEGORIAS_PRODUCTOS.add(new Categoria(4,"Bebida"));
        for(Categoria cat: CATEGORIAS_PRODUCTOS){
            for(int i=0;i<25;i++){
                LISTA_PRODUCTOS.add(new Producto(id++,cat.getNombre()+" 1"+i,"descripcion "+(i*id)+rand.nextInt(100),rand.nextDouble()*500,cat));
            }
        }
        FLAG_INICIALIZADO=true;
    }

    public ProductoRepository(){
        if(!FLAG_INICIALIZADO) inicializar();
    }

    public List<Producto> getLista(){
        return LISTA_PRODUCTOS;
    }

    public List<Categoria> getCategorias(){
        return CATEGORIAS_PRODUCTOS;
    }

    public Producto buscarPorId(Integer id){
        for(Producto p: LISTA_PRODUCTOS){
            if(p.getId().equals(id)) return p;
        }
        return null;
    }

    public List<Producto> buscarPorCategoria(Categoria cat){
        List<Producto> resultado = new ArrayList<>();
        for(Producto p:LISTA_PRODUCTOS){
            if(p.getCategoria().getId().equals(cat.getId())) resultado.add(p);
        }
        return resultado;
    }
}
