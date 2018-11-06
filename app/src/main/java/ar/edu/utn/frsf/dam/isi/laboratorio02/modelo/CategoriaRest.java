package ar.edu.utn.frsf.dam.isi.laboratorio02.modelo;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CategoriaRest {
    public static final String ip = "10.0.2.2";
    public static final String port = "4000";
    public void crearCategoria(Categoria c) throws JSONException, IOException {
        //Variables de conexión y stream de escritura y lectura
        HttpURLConnection urlConnection = null;
        DataOutputStream printout =null;
        InputStream in =null;
        //Crear el objeto json que representa una categoria
        JSONObject categoriaJson = new JSONObject();
        categoriaJson.put("nombre",c.getNombre());
        //Abrir una conexión al servidor para enviar el POST
        String link = String.format("http://%s:%s/categorias",ip,port);
        URL url = new URL(link);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setChunkedStreamingMode(0);
        urlConnection.setRequestProperty("Content-Type","application/json");
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("POST");
        //Obtener el outputStream para escribir el JSON
        printout = new DataOutputStream(urlConnection.getOutputStream());
        String str = categoriaJson.toString();
        byte[] jsonData=str.getBytes("UTF-8");
        printout.write(jsonData);
        printout.flush();
        //Leer la respuesta
        in = new BufferedInputStream(urlConnection.getInputStream());
        InputStreamReader isw = new InputStreamReader(in);
        StringBuilder sb = new StringBuilder();
        int data = isw.read();
        //Analizar el codigo de lar respuesta
        if( urlConnection.getResponseCode() ==200 ||
                urlConnection.getResponseCode()==201){
            while (data != -1) {
                char current = (char) data;
                sb.append(current);
                data = isw.read();
            }
            //analizar los datos recibidos
            Log.d("LAB_04",sb.toString());
        }else{
            Log.d("LAB_04",
                    String.format("Codigo de respuesta %d",urlConnection.getResponseCode()));
        }
        // caputurar todas las excepciones y en el bloque finally
        // cerrar todos los streams y HTTPUrlCOnnection
        if(printout!=null) {
            try { printout.close(); }
            finally {
            }
        }
        if(in!=null) {
            try { in.close(); }
            finally {
            }
        }
        if(urlConnection !=null) {
            urlConnection.disconnect();
        }


    }
}
