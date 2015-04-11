package com.example.marcell.currencyconverter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


// Result/End-Screen
public class ResultActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Get Extra messages from the intent
        Intent intent = getIntent();
        Double rate = intent.getDoubleExtra("rate",0);
        Double amount = intent.getDoubleExtra("amount", 0);
        String currency = intent.getStringExtra("currency");

        // Currency Calculation
        Double result = amount;
        result=result*rate;


        // The result as an Text-View output
        TextView t1 = (TextView)findViewById(R.id.euroAmount);
        t1.setText(amount+" Euro (EUR)");

        TextView t2 = (TextView)findViewById(R.id.foreignAmount);
        t2.setText(result+" "+currency);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
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

    // When the NEW-Button is clicked, this Method brings the user back to the Start-Screen
    public void onButtonReturnToMain(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
