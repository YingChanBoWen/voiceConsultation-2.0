package com.MM.voiceconsultation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class HTTP {


    //private static String serverURL = "";
    private static String getStringFromOutput(Map<String,String> map) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for(Map.Entry<String,String> entirty:map.entrySet()){
            if(isFirst)
                isFirst = false;
            else
                sb.append("&");

            sb.append(URLEncoder.encode(entirty.getKey(),"UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(entirty.getValue(),"UTF-8"));
        }
        return sb.toString();
    }

    public static String getContextByHttp(String urlStr, Map<String,String> parms,String method){

        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setReadTimeout(50000);
            connection.setConnectTimeout(50000);

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(true);

            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
            bufferedWriter.write(getStringFromOutput(parms));

            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String temp;
                while((temp = reader.readLine()) != null){
                    stringBuilder.append(temp);
                }
                reader.close();
            }else{
                return "connection error:" + connection.getResponseCode();
            }

            connection.disconnect();
        }catch (Exception e){
            return e.toString();
        }
        return stringBuilder.toString();

    }




}
