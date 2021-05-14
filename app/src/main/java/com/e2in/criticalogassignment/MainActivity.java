package com.e2in.criticalogassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    int enteredValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText etx_enteredValue = (EditText)findViewById(R.id.etx_input);
        findViewById(R.id.btn_go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String resultValue  = "";
                TextView output = findViewById(R.id.txt_result);
                if (etx_enteredValue.getText().toString().isEmpty()){
                    output.setText("Please enter any numerical value");
                }else {
                    enteredValue = Integer.parseInt(etx_enteredValue.getText().toString());
                    Log.e("Activity","Entered Value: "+enteredValue);
                    for (int i=0; i<=enteredValue; i++){
                        resultValue = resultValue +" "+i;
                        Log.e("Activity","Added Values: "+resultValue);
                    }
                    output.setText(String.valueOf(resultValue));
                }

            }
        });
    }


}