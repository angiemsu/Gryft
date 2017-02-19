package lyft;

import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import org.json.*;
import android.util.Base64;
import android.util.Log;
import org.apache.commons.io.IOUtils;

import android.widget.Toast;

class Lyft {
    String LyftAccessResponse = "";

    protected void GetAuthToken() {
        try {
            // Start HTTP Connection
            URL object = new URL(getAPIURL());
            HttpURLConnection connection = (HttpURLConnection) object.openConnection();
            connection.setReadTimeout(60 * 1000);
            connection.setConnectTimeout(60 * 1000);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Set Up Authorization Message
            String authorization = getClientID() + ":" + getClientSecret();
            byte[] __data = authorization.getBytes("UTF-8");
            String base64 = Base64.encodeToString(__data, Base64.DEFAULT);
            String encodedAuth = "Basic " + base64;
            connection.setRequestProperty("Authorization", encodedAuth);

            String data = "{\"grant_type\": \"client_credentials\", \"scope\": \"public\"}";
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(data);
            out.close();

            connection.setRequestMethod("POST");

            // Grab Response Code
            int responseCode = connection.getResponseCode();
            Log.v("Lyft API Access", "httpStatus :" + responseCode);
            if (responseCode == 200) {
                InputStream inputStr = connection.getInputStream();
                String encoding = connection.getContentEncoding() == null ? "UTF-8"
                        : connection.getContentEncoding();
                LyftAccessResponse = IOUtils.toString(inputStr, encoding);
            } else {
                Log.v("Lyft API Access", connection.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Getters for LyftAPIs
    private String getAccessToken() {
        Log.i("Lyft API Response", LyftAccessResponse);
        try {
            JSONObject jsonObject = new JSONObject(LyftAccessResponse);
            return jsonObject.getString("access_token");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private String getAPIURL() {
        return "";
    }

    private String getClientID() {
        return "";
    }

    private String getClientSecret() {
        return "";
    }

    // Used For Debugging ONLY
    private void showLyftResponseToast(String result) {
        Toast.makeText(this.getApplicationContext(), "Access Token:" + getAccessToken(), Toast.LENGTH_SHORT).show();
    }
}
