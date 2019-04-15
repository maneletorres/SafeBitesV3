package com.maneletorres.safebites.utils;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.maneletorres.safebites.entities.Product;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JsonTask extends AsyncTask<String, Integer, Product> {
    public AsyncResponse delegate = null;

    @Override
    protected Product doInBackground(String... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuilder buffer = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
                Log.d("Response: ", "> " + line);
            }

            if (!TextUtils.isEmpty(buffer.toString())) {
                Product product = null;
                try {
                    JSONObject baseJsonResponse = new JSONObject(buffer.toString());
                    JSONObject currentProduct = baseJsonResponse.getJSONObject("product");
                    String currentProductCode = baseJsonResponse.getString("code");

                    if (currentProductCode != null && currentProductCode.length() > 0) {
                        if (currentProduct != null && currentProduct.length() > 0) {
                            JSONObject currentNutriments = currentProduct.getJSONObject("nutriments");

                            product = Utils.extractJSONNutrients(currentProduct, currentNutriments);
                        }
                    }
                } catch (JSONException e) {
                    Log.e("Utils", "Problem parsing the product JSON results", e);
                }

                return product;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Product result) {
        super.onPostExecute(result);
        delegate.processFinish(result);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values[0]);
    }
}