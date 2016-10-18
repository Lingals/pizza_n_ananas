package insset.com.pizzanananas;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import insset.com.models.Pizza;
import insset.com.utils.Constant;

public class PizzasList extends AppCompatActivity {

    private Context context;
    private ListView pizzas_list_lv;

    private ProgressDialog progressDialog;

    //private PizzaAdapter pizzaAdapter;
    private List<Pizza> listPizzas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pizzas_list);

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


        client.setMaxRetriesAndTimeout(2, 3000);

        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {


            public void onSuccess(int statusCode,Header[] headers,org.json.JSONArray response) {
                System.out.println("Success");
                Log.e("Je vois", "La reponse" + response.toString());
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                try {

                    for (int i = 0; i < response.length(); i++) {
                        Pizza pizza = new Pizza();
                        JSONObject openRequest = response.getJSONObject(i);
                        pizza.setId(Integer.parseInt(openRequest.getString("id")));
                        pizza.setName(openRequest.getString("name"));
                        pizza.setPrice(Integer.parseInt(openRequest.getString("price")));

                        listPizzas.add(pizza);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("SIZE" , listPizzas.size() + "");

            }

            public void onFailure(int statusCode,Header[] headers, Throwable throwable,	org.json.JSONObject response) {
                System.out.println("Failure Json");
                Log.e("Je vois", "La reponse" + response.toString());
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }

            public void onFailure(int statusCode,Header[] headers,String result, Throwable throwable) {
                System.out.println("Failure String");
                Log.e("Status Code", statusCode+"");
                Log.e("Je vois", "La reponse" + result.toString());
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

            }
        };
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Récupération des pizzas");
        progressDialog.setCancelable(false);
        progressDialog.show();
        client.get(Constant.host + Constant.getPizzas, responseHandler);
    }
}
