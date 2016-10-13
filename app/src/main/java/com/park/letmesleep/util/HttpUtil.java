package com.park.letmesleep.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Mr.G on 2016/6/14.
 */
public class HttpUtil {
    public interface HttpCallbackListener { void onFinish(String response); void onError(Exception e); }

    public static void sendHttpRequest(final String address, final String message, final HttpCallbackListener listener) {

        new Thread(new Runnable() {
            @Override
            public void run() {


                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    connection.connect();
                    if(message!=null&&!message.equals("")) {
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                        bw.write(message);
                        bw.flush();
                        bw.close();
                    }

                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    if(listener!=null)
                        listener.onFinish(response.toString());
                } catch (Exception e) {
                    if(listener!=null)
                        listener.onError(e);
                } finally {
                    if (connection != null)
                        connection.disconnect();

                }



            }

        }).start();
    }

}
