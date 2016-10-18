package insset.com.pizzanananas;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ClientPage extends AppCompatActivity {

    private Button client_page_pizzas_b,
            client_page_orders_b;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_page);

        context = this;

        initializeFields();

        client_page_pizzas_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PizzasList.class);
                startActivity(i);
            }
        });

        client_page_orders_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ClientOrderList.class);
                startActivity(i);
            }
        });
    }

    public void initializeFields() {
        client_page_pizzas_b = (Button) findViewById(R.id.client_page_pizzas_b);
        client_page_orders_b = (Button) findViewById(R.id.client_page_orders_b);
    }
}
