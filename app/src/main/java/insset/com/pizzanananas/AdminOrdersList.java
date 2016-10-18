package insset.com.pizzanananas;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import insset.com.adapters.OrderAdapter;
import insset.com.models.Order;
import insset.com.models.Pizza;
import insset.com.utils.Constant;

public class AdminOrdersList extends AppCompatActivity {

    ListView list_of_orders;
    ProgressDialog progressDialog;
    Context context;
    List<Order> orderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_list);

        setTitle("Page admin");

        context = this;

        initializeFields();

        getOrders();
    }

    public void initializeFields(){
        list_of_orders = (ListView) findViewById(R.id.list_of_orders);
    }

    private void getOrders() {
        AsyncHttpClient client = new AsyncHttpClient();
        //RequestParams params = new RequestParams();

        client.addHeader("Authorization", Constant.Authorization);


        client.setTimeout(3000);

        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {


            public void onSuccess(int statusCode,Header[] headers,org.json.JSONArray response) {

                JSONObject orderJson;
                for(int i = 0; i < response.length(); i++){
                    Order newOrder = new Order();
                    try {
                        orderJson = response.getJSONObject(i);
                        if(orderJson.has("id")){
                            newOrder.setId(orderJson.getInt("id"));
                        }
                        if(orderJson.has("status")){
                            newOrder.setStatus(orderJson.getString("status"));
                        }
                        if(orderJson.has("pizza")){
                            Pizza newPizza = new Pizza();
                            JSONObject pizzaJson = orderJson.getJSONObject("pizza");
                            if(pizzaJson.has("id")){
                                newPizza.setId(pizzaJson.getInt("id"));
                            }
                            if(pizzaJson.has("price")){
                                newPizza.setPrice(pizzaJson.getInt("price"));
                            }
                            if(pizzaJson.has("name")){
                                newPizza.setName(pizzaJson.getString("name"));
                            }
                            newOrder.setPizza(newPizza);
                        }

                        orderList.add(newOrder);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                list_of_orders.setAdapter(new OrderAdapter(context, orderList, true));


                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                if(orderList.isEmpty()){
                    Toast.makeText(context, "Aucune commande en cours", Toast.LENGTH_LONG).show();
                }

                try{
                    Log.e("Admin Orders", response.toString()+"");
                }catch(Exception e){

                }

            }

            public void onFailure(int statusCode,Header[] headers, Throwable throwable,	org.json.JSONObject response) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                Toast.makeText(context, "Une erreur est survenue", Toast.LENGTH_LONG).show();

                finish();

                try{
                    Log.e("Admin Orders Failure", response.toString()+"");
                }catch(Exception e){

                }
            }

            public void onFailure(int statusCode,Header[] headers,String result, Throwable throwable) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                Toast.makeText(context, "Une erreur est survenue", Toast.LENGTH_LONG).show();

                finish();


                try{
                    Log.e("Admin Orders Failure St", result+"");
                }catch(Exception e){

                }
            }
        };
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Récupération des commandes");
        progressDialog.setCancelable(false);
        progressDialog.show();
        client.get(Constant.host + Constant.getOrders, responseHandler);
    }
}
