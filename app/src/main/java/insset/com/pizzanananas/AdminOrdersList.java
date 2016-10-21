package insset.com.pizzanananas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import insset.com.adapters.OrderAdapter;
import insset.com.models.Order;
import insset.com.models.Pizza;
import insset.com.utils.Constant;
import insset.com.utils.Lilibrato;

public class AdminOrdersList extends AppCompatActivity {

    ListView list_of_orders;
    ProgressDialog progressDialog;
    Context context;
    List<Order> orderList = new ArrayList<>();

    SharedPreferences sharedPreferences;

    Lilibrato lilibrato = null;

    long dateStart = 0,
            dateEnd = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_list);

        setTitle("Page admin");

        context = this;

        sharedPreferences = getSharedPreferences("ERROR_LOG", MODE_PRIVATE);

        lilibrato = new Lilibrato("PizzaNananas.AdminOrdersList");

        initializeFields();

        if(sharedPreferences.getLong("timestamp", 0) == 0 || (System.currentTimeMillis() > sharedPreferences.getLong("timestamp", 0) + 120000)){
            dateStart = System.currentTimeMillis();
            lilibrato.setCircuitBreaker(0);
            getOrders();
        }else{
            lilibrato.setCircuitBreaker(1);
            Type type = new TypeToken<List<Order>>(){}.getType();
            Gson gson = new Gson();
            String jsonOrder = sharedPreferences.getString("listOfOrdersAdmin", "");
            List<Order> ordersList = gson.fromJson(jsonOrder, type);

            if(ordersList.isEmpty()) {
                Toast.makeText(context, "Une erreur est survenue", Toast.LENGTH_LONG).show();
                finish();
            }else{
                list_of_orders.setAdapter(new OrderAdapter(context, ordersList, true));
            }
        }

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

                dateEnd = System.currentTimeMillis();

                lilibrato.setTimes(dateStart, dateEnd);
                lilibrato.setStatus(statusCode);

                sharedPreferences.edit().remove("listOfOrdersAdmin");

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

                        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(orderList);
                        prefsEditor.putString("listOfOrdersAdmin", json);
                        prefsEditor.commit();

                        Type type = new TypeToken<List<Order>>(){}.getType();
                        Gson gson2 = new Gson();
                        String jsonOrder = sharedPreferences.getString("listOfOrdersAdmin", "");
                        List<Order> ordersList = gson2.fromJson(jsonOrder, type);

                        Log.i("LIST", ordersList.toString()+"");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                list_of_orders.setAdapter(new OrderAdapter(context, orderList, true));

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("ErrType", "");
                editor.putString("Activity", "");
                editor.putLong("timestamp", 0);
                editor.commit();


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

                lilibrato.setStatus(statusCode);

                if (progressDialog.isShowing())
                    progressDialog.dismiss();



                // si le shared preference est vide on met le cache sinon on ne touche le cache que si le timestamp est > 1 minute

                if ( (sharedPreferences.getString("ErrType", "").equals("")) || (System.currentTimeMillis() > sharedPreferences.getLong("timestamp", 0) + 60000)) {

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("ErrType", "Timeout");
                    editor.putString("Activity", "AdminOrder");
                    editor.putLong("timestamp", System.currentTimeMillis());
                    editor.commit();
                    Log.d("EDITOR", System.currentTimeMillis() + "");
                } else {
                    Log.d("NOT EDITOR", sharedPreferences.getLong("timestamp", 0) + "");
                }

                Type type = new TypeToken<List<Order>>(){}.getType();
                Gson gson = new Gson();
                String jsonOrder = sharedPreferences.getString("listOfOrdersAdmin", "");
                List<Order> ordersList = gson.fromJson(jsonOrder, type);

                if(ordersList.isEmpty()) {
                    Toast.makeText(context, "Une erreur est survenue", Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    list_of_orders.setAdapter(new OrderAdapter(context, ordersList, true));
                }

                try{
                    Log.e("Admin Orders Failure", response.toString()+"");
                }catch(Exception e){

                }
            }

            public void onFailure(int statusCode,Header[] headers,String result, Throwable throwable) {

                lilibrato.setStatus(statusCode);

                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                Toast.makeText(context, "Une erreur est survenue", Toast.LENGTH_LONG).show();

                if ( (sharedPreferences.getString("ErrType", "").equals("")) || (System.currentTimeMillis() > sharedPreferences.getLong("timestamp", 0) + 60000)) {

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("ErrType", "Timeout");
                    editor.putString("Activity", "AdminOrder");
                    editor.putLong("timestamp", System.currentTimeMillis());
                    editor.commit();
                    Log.d("EDITOR", System.currentTimeMillis() + "");
                } else {
                    Log.d("NOT EDITOR", sharedPreferences.getLong("timestamp", 0) + "");
                }

                Type type = new TypeToken<List<Order>>(){}.getType();
                Gson gson = new Gson();
                String jsonOrder = sharedPreferences.getString("listOfOrdersAdmin", "");
                List<Order> ordersList = gson.fromJson(jsonOrder, type);

                if(ordersList.isEmpty()) {
                    Toast.makeText(context, "Une erreur est survenue", Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    list_of_orders.setAdapter(new OrderAdapter(context, ordersList, true));
                }


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
        if(sharedPreferences.getBoolean("Prod", true)) {
            client.get(Constant.host + Constant.getOrders, responseHandler);
        }else{
            client.get(Constant.hostTest + Constant.getOrders, responseHandler);
        }
    }
}
