package insset.com.pizzanananas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class HomePage extends AppCompatActivity {

    private Button home_page_client_b,
            home_page_admin_b, change_domain_button;
    private TextView mode_text;
    private SharedPreferences sharedPreferences;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        setTitle("Accueil");

        context = this;

        sharedPreferences = getSharedPreferences("ERROR_LOG", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Prod", true);
        editor.commit();

        initializeFields();

        home_page_client_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ClientPage.class);
                startActivity(i);
            }
        });

        home_page_admin_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AdminOrdersList.class);
                startActivity(i);
            }
        });

        change_domain_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedPreferences.getBoolean("Prod", true)){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("Prod", false);
                    editor.commit();
                    mode_text.setText("dev");
                }else{
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("Prod", true);
                    editor.commit();
                    mode_text.setText("prod");
                }
            }
        });
    }

    public void initializeFields() {
        home_page_client_b = (Button) findViewById(R.id.home_page_client_b);
        home_page_admin_b = (Button) findViewById(R.id.home_page_admin_b);
        mode_text = (TextView) findViewById(R.id.mode_text);
        change_domain_button = (Button) findViewById(R.id.change_domain_button);
    }

}
