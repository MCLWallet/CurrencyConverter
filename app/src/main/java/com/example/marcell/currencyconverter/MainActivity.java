package com.example.marcell.currencyconverter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

// Startscreen
public class MainActivity extends Activity {

    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Spinner gets created
        Spinner spinner= (Spinner)findViewById(R.id.spinner);
        ArrayAdapter adapter= ArrayAdapter.createFromResource(this, R.array.currencies_meaning, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // Sends the asked amount, the currency rate and the name of the currency to the
    // next ActitvityScreen (ResultActivity)
    public void sendMessage(Double rate){
       // User-input gets saved
        Double input;
        editText = (EditText)findViewById(R.id.amount);
        input = Double.parseDouble(editText.getText().toString());

        // If the User-Input is an allowable amount (positve variable) ...
        if (input>0){
            // ... intent for next Activity gets prepared
            Intent intent = new Intent(this, ResultActivity.class);
            Spinner spinner= (Spinner)findViewById(R.id.spinner);
            String currency = spinner.getSelectedItem().toString();
            intent.putExtra("amount", input);
            intent.putExtra("rate", rate);
            intent.putExtra("currency", currency);

            // Go to ResultActivity
            startActivity(intent);
            return;
        }

        // If the User-Input is a negative variable, an error toast pops up
        if (input<0){
            Toast.makeText(getBaseContext(),"No negative variables allowed!", Toast.LENGTH_SHORT).show();
            return;
        }

        // In all other cases another error toast pops up
        else{
            Toast.makeText(getBaseContext(),"Please just enter a positive variable!", Toast.LENGTH_SHORT).show();
            return;
        }



    }


    // opens a HTTP-Connection with an URL and return its content to a String
    public String readJSONFeed(String URL){
        StringBuilder stringBuilder = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        try{
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200){
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line=reader.readLine())!= null){
                    stringBuilder.append(line);
                }
                inputStream.close();
            } else {
                Log.d("JSON", "Failed to download File");
            }
        }
        catch (Exception e) {
            Log.d("readJSONFeed", "Fail");
        }
        return stringBuilder.toString();

    }


    // This class is responsible for reading the JSON-File out of fixer.io/latest
    private class JsonConnection extends AsyncTask<String, Void, String> {

        // Builds the URL-Connection
        protected String doInBackground(String... urls){
            return readJSONFeed(urls[0]);
        }

        // the DoPost-Method which reads the Json Kex/Value-Pair
        protected void onPostExecute(String result){
            Double currentRate=0.0;
            try{
                // The JSON-Objects get created with the Information from the URL
                JSONObject jsonObject = new JSONObject(result);
                JSONObject currencyItems= new JSONObject(jsonObject.getString("rates"));

                // The spinner tells which currency the user askes for
                Spinner spinner= (Spinner)findViewById(R.id.spinner);
                int pos = spinner.getSelectedItemPosition();

                // gets the StringArray with only the ISO-Codes of the currencies
                Resources res = getResources();
                String []cur = res.getStringArray(R.array.currencies);

                // the rate from the JSON-File is saved in this variable
                // and gets sent to the next Activity
                currentRate=Double.parseDouble(currencyItems.getString(cur[pos]));
                sendMessage(currentRate);



                // If an error occurs, an exceptions gets caught
            } catch (Exception e){
                Log.d("JsonConnectionFail",e.getLocalizedMessage());
            }
        }
    }

    // initiates the calculation-process
    public void onClickCalculate(View view){
        ConnectivityManager connManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo myWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        editText = (EditText)findViewById(R.id.amount);

        // Checks if device has network connection
        if(!myWifi.isConnected()) {
            Toast.makeText(getBaseContext(),"Network Connectivity Problems",Toast.LENGTH_SHORT).show();
            return;
        }

        // Checks if an amount was given
        if (editText.getText().toString().isEmpty()){
            Toast.makeText(getBaseContext(),"Please enter an amount!",Toast.LENGTH_SHORT).show();
            return;
        }

        // If the amount is a normal Double-value, calculation-process gets initiated
        else{

            // The asked currency gets read out of the spinner ...
            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            int pos = spinner.getSelectedItemPosition();

            // gets the StringArray with only the ISO-Codes of the currencies
            Resources res = getResources();
            String[] cur = res.getStringArray(R.array.currencies);

            // ... and transmitted to the URL
            JsonConnection readCurrencyJSONFeedTask = new JsonConnection();
            readCurrencyJSONFeedTask.execute("http://api.fixer.io/latest?rates=" + cur[pos]);
        }


    }
}
