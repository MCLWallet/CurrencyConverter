package com.example.marcell.currencyconverter;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;



public class ResultActivity extends ActionBarActivity {


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


        // Read the textView
        TextView t1 = (TextView)findViewById(R.id.textView3);
        t1.setText(amount+" EUR = "+result+" "+currency);


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
}
