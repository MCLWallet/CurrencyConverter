package com.example.marcell.currencyconverter;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
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
public class MainActivity extends ActionBarActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Spinner gets created
        Spinner spinner= (Spinner)findViewById(R.id.spinner);
        ArrayAdapter adapter= ArrayAdapter.createFromResource(this, R.array.currencies, android.R.layout.simple_spinner_dropdown_item);
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


    /*
    public void clearText(View view){
        EditText e1 = (EditText)findViewById(R.id.amount);
        if(e1.getText().equals("Enter the amount")) {
            e1.setText("");
            return;
        }
        return;

    }
    */

    // Sends the asked amount, the currency rate and the name of the currency to the
    // next ActitvityScreen (ResultActivity)
    public void sendMessage(Double rate){
        Double input;
        EditText e1 = (EditText)findViewById(R.id.amount);
        input = Double.parseDouble(e1.getText().toString());
        if (input>0){
            // Intent for next Activity gets prepared
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

        if (input<0){
            Toast.makeText(getBaseContext(),"No negative variables allowed!", Toast.LENGTH_LONG).show();
            return;
        }
        else{
            Toast.makeText(getBaseContext(),"Please just enter a positive variable!", Toast.LENGTH_LONG).show();
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

    private class ReadCurrencyJSONFeedTask extends AsyncTask<String, Void, String> {


        protected String doInBackground(String... urls){
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result){
            Double currentRate=0.0;
            try{
                JSONObject jsonObject = new JSONObject(result);
                JSONObject currencyItems= new JSONObject(jsonObject.getString("rates"));

                Spinner spinner= (Spinner)findViewById(R.id.spinner);
                String cur = (String)spinner.getSelectedItem().toString();

                currentRate=Double.parseDouble(currencyItems.getString(cur));

                sendMessage(currentRate);





            } catch (Exception e){
                      Log.d("ReadCurrencyJSONFeedTask",e.getLocalizedMessage());
            }
        }
    }

    public void btnGetCurrency(View view){
        Spinner spinner= (Spinner)findViewById(R.id.spinner);
        String cur = (String)spinner.getSelectedItem().toString();

        ReadCurrencyJSONFeedTask readCurrencyJSONFeedTask = new ReadCurrencyJSONFeedTask();
        readCurrencyJSONFeedTask.execute("http://api.fixer.io/latest?rates="+cur);


    }
}
