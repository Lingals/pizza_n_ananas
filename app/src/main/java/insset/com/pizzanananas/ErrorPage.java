package insset.com.pizzanananas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import twitter4j.ResponseList;
import twitter4j.Status;

public class ErrorPage extends AppCompatActivity {

    TextView viewError;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_page);
        viewError = (TextView) findViewById(R.id.view_error_page);
        Intent intent = getIntent();
        String responseTw = intent.getStringExtra("tweet");

        if (responseTw.contains("[!info!]")){
            responseTw = responseTw.replace("[!info!]","");
            viewError.setText(responseTw);
        }else{
            viewError.setText("Nous rencontrons actuellement des problèmes, nous nous excusons pour la gène occasionné et vous remercions pour votre compréhension ");
        }


    }
}



