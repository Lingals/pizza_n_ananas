package insset.com.pizzanananas;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class HomePage extends AppCompatActivity {

    private Button home_page_client_b,
            home_page_admin_b;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        context = this;

        initializeFields();

        home_page_client_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ClientPage.class);
                startActivity(i);
                finish();
            }
        });

        home_page_admin_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent i = new Intent(getApplicationContext(), .class);
        startActivity(i);
        finish();*/
            }
        });
    }

    public void initializeFields() {
        home_page_client_b = (Button) findViewById(R.id.home_page_client_b);
        home_page_admin_b = (Button) findViewById(R.id.home_page_admin_b);
    }
}
