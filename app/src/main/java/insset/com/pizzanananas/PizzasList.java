package insset.com.pizzanananas;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import insset.com.adapters.PizzaAdapter;
import insset.com.models.Pizza;
import insset.com.utils.Constant;

public class PizzasList extends AppCompatActivity {

    private Context context;
    private ListView pizzas_list_lv;

    private ProgressDialog progressDialog;

    private PizzaAdapter pizzaAdapter;
    private List<Pizza> listPizzas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pizzas_list);

        setTitle("Liste des pizzas");

        context = this;

        initializeFields();

        getPizzas();
    }

    public void initializeFields() {
        pizzas_list_lv = (ListView) findViewById(R.id.pizzas_list_lv);
    }

    private void getPizzas() {
        AsyncHttpClient client = new AsyncHttpClient();
        //RequestParams params = new RequestParams();

        client.addHeader("Authorization", Constant.Authorization);


        client.setTimeout(3000);

        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {


            public void onSuccess(int statusCode,Header[] headers,org.json.JSONArray response) {

                try {

                    for (int i = 0; i < response.length(); i++) {
                        Pizza pizza = new Pizza();
                        JSONObject openRequest = response.getJSONObject(i);
                        pizza.setId(Integer.parseInt(openRequest.getString("id")));
                        pizza.setName(openRequest.getString("name"));
                        pizza.setPrice(Integer.parseInt(openRequest.getString("price")));

                        listPizzas.add(pizza);
                    }

                    pizzaAdapter = new PizzaAdapter(context, listPizzas);

                    pizzas_list_lv.setAdapter(pizzaAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                if(listPizzas.isEmpty()){
                    Toast.makeText(context, "Aucune pizza disponible", Toast.LENGTH_LONG).show();
                }
            }

            public void onFailure(int statusCode,Header[] headers, Throwable throwable,	org.json.JSONObject response) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                Toast.makeText(context, "Une erreur serveur est survenue", Toast.LENGTH_LONG).show();

                finish();
            }

            public void onFailure(int statusCode,Header[] headers,String result, Throwable throwable) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                Toast.makeText(context, "Une erreur est survenue", Toast.LENGTH_LONG).show();

                finish();

            }
        };
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Récupération des pizzas");
        progressDialog.setCancelable(false);
        progressDialog.show();
        client.get(Constant.host + Constant.getPizzas, responseHandler);
    }
}
