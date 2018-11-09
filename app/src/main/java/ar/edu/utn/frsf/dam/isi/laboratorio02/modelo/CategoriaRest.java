package ar.edu.utn.frsf.dam.isi.laboratorio02.modelo;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CategoriaRest {
    public static final String ip = "10.0.2.2";
    public static final String port = "4000";

    public void crearCategoria(Categoria c) throws JSONException, IOException {
        JSONObject categoriaJson = new JSONObject();
        categoriaJson.put("nombre",c.getNombre());
        doHTTPrequest("POST","categorias",categoriaJson);
    }

    public List<Categoria> listarTodas() throws IOException, JSONException {
        List<Categoria> resultado = new ArrayList<>();
        String resultadoStr = doHTTPrequest("GET","categorias",null);

        JSONTokener tokener = new JSONTokener(resultadoStr);
        JSONArray listaCategorias = (JSONArray) tokener.nextValue();
        for(int i =0;i<listaCategorias.length();i++)
        {
            JSONObject jcat = listaCategorias.getJSONObject(i);
            Categoria cat = new Categoria(  jcat.getInt("id"),
                    jcat.getString("nombre"));
            resultado.add(cat);
        }
        return resultado;
    }


    private String doHTTPrequest(String httpMethod,String resource, JSONObject sendingJson) throws IOException, UnsupportedOperationException {
        HttpURLConnection urlConnection = null;
        DataOutputStream printout = null;
        InputStream in = null;

        String link = String.format("http://%s:%s/%s", ip, port,resource);
        URL url = new URL(link);
        urlConnection = (HttpURLConnection) url.openConnection();

        if(httpMethod.equals("POST"))
        {
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);
        }
        else if (httpMethod.equals("GET")) urlConnection.setRequestProperty("Accept-Type", "application/json");
        else{ throw new UnsupportedOperationException(String.format("Unsupported operation %s",httpMethod)); }

        urlConnection.setRequestMethod(httpMethod);

        if(sendingJson != null)
        {
            printout = new DataOutputStream(urlConnection.getOutputStream());
            String strJson = sendingJson.toString();
            byte[] jsonData = strJson.getBytes("UTF-8");
            printout.write(jsonData);
            printout.flush();
        }

        in = new BufferedInputStream(urlConnection.getInputStream());
        InputStreamReader isw = new InputStreamReader(in);
        StringBuilder sb = new StringBuilder();
        int data = isw.read();

        String toReturn = null;
        if (urlConnection.getResponseCode() == 200 ||
                urlConnection.getResponseCode() == 201) {
            while (data != -1) {
                char current = (char) data;
                sb.append(current);
                data = isw.read();
            }
            toReturn = sb.toString();
            Log.d("LAB_04", toReturn);
        }
        else
        {
            Log.d("LAB_04",
                    String.format("Codigo de respuesta %d", urlConnection.getResponseCode()));
        }

        if(printout != null) printout.close();
        if(in != null) in.close();
        if(urlConnection != null) urlConnection.disconnect();

        return toReturn;
    }
}

