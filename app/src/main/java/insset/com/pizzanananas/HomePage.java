package insset.com.pizzanananas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.librato.metrics.BatchResult;
import com.librato.metrics.HttpPoster;
import com.librato.metrics.LibratoBatch;
import com.librato.metrics.PostResult;
import com.librato.metrics.Sanitizer;

import java.util.concurrent.TimeUnit;

import insset.com.librato.DefaultHttpPoster;


public class HomePage extends AppCompatActivity {

    static String email = "p.pavone59@gmail.com";
    static String apiToken = "d77e3da0ff40b4d1949ad99702fdf5675f4a2aeed3c6f8f6acd59ffcb6832e8d";
    static String apiUrl = "https://metrics-api.librato.com/v1/metrics";
    static HttpPoster poster;

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


        /**
         * LIBRATO
         */




            Log.e("ENVOI", apiUrl+" "+email+" "+apiToken);
            poster = new DefaultHttpPoster(apiUrl, email, apiToken);


            int batchSize = 300;
            long timeout = 10L;
            TimeUnit timeoutUnit = TimeUnit.SECONDS;
            Sanitizer sanitizer = Sanitizer.NO_OP;
            LibratoBatch batch = new LibratoBatch(batchSize, sanitizer, timeout, timeoutUnit, null, poster);

            batch.addGaugeMeasurement("apples", 100);
            batch.addCounterMeasurement("bytes-in", (long) 42);


            long epoch = System.currentTimeMillis() / 1000;
            String source = "Android";
            BatchResult result = batch.post(source, epoch);
            if (!result.success()) {
                for (PostResult post : result.getFailedPosts()) {
                    Log.e("Not POST to Librato", post.toString() + "");
                }
            }





    }

    public void initializeFields() {
        home_page_client_b = (Button) findViewById(R.id.home_page_client_b);
        home_page_admin_b = (Button) findViewById(R.id.home_page_admin_b);
        mode_text = (TextView) findViewById(R.id.mode_text);
        change_domain_button = (Button) findViewById(R.id.change_domain_button);
    }
}
