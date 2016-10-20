package insset.com.pizzanananas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ClientOrderList extends AppCompatActivity {

    Context context;
    ListView list_client_order;
    EditText editText_client_order_list;
    Button button_client_order_list;
    ProgressDialog progressDialog;
    List<Order> orderList = new ArrayList<>();
    OrderAdapter orderAdapter;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_order_list);

        setTitle("Liste des commandes");

        context = this;

        sharedPreferences = getSharedPreferences("ERROR_LOG", MODE_PRIVATE);

        initializeFields();

        Intent intent = getIntent();
        String idPizza = null;

        idPizza = intent.getStringExtra("idPizza");

        if (idPizza != null && !idPizza.equals("")) {
            editText_client_order_list.setText(idPizza);

            getOrders(idPizza);
        }

        button_client_order_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editText_client_order_list.getText().toString().isEmpty()) {
                    getOrders(editText_client_order_list.getText().toString());
                }
            }
        });
    }



    public void initializeFields(){
        list_client_order = (ListView)findViewById(R.id.listView_client_order_list);
        editText_client_order_list = (EditText)findViewById(R.id.editText_client_order_list);
        button_client_order_list = (Button)findViewById(R.id.button_client_order_list);

    }

    private void getOrders(String id) {
        AsyncHttpClient client = new AsyncHttpClient();
        //RequestParams params = new RequestParams();

        client.addHeader("Authorization", Constant.Authorization);

        client.setTimeout(3000);

        orderList.clear();

        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {


            public void onSuccess(int statusCode, Header[] headers, org.json.JSONObject orderJson) {

                Order newOrder = new Order();
                try {
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
                orderAdapter = new OrderAdapter(context, orderList, false);
                list_client_order.setAdapter(orderAdapter);
                orderAdapter.notifyDataSetChanged();


                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                if(orderList.isEmpty()){
                    Toast.makeText(context, "Aucun résultat", Toast.LENGTH_LONG).show();
                }

                try{
                    Log.e("Client Orders", orderJson.toString()+"");
                }catch(Exception e){

                }

            }

            public void onFailure(int statusCode,Header[] headers, Throwable throwable,	org.json.JSONObject response) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                try {
                    if(response.has("message")){
                        orderAdapter = new OrderAdapter(context, orderList, false);
                        list_client_order.setAdapter(orderAdapter);
                        orderAdapter.notifyDataSetChanged();

                        if(orderList.isEmpty()){
                            Toast.makeText(context, "Aucun résultat", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        orderAdapter = new OrderAdapter(context, orderList, false);
                        list_client_order.setAdapter(orderAdapter);
                        orderAdapter.notifyDataSetChanged();

                        if(orderList.isEmpty()){
                            Toast.makeText(context, "Une erreur est survenue", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                }catch(Exception e){
                    orderAdapter = new OrderAdapter(context, orderList, false);
                    list_client_order.setAdapter(orderAdapter);
                    orderAdapter.notifyDataSetChanged();

                    if(orderList.isEmpty()){
                        Toast.makeText(context, "Une erreur est survenue", Toast.LENGTH_LONG).show();
                    }
                    finish();
                }


                try{
                    Log.e("Client Orders Failure", response.toString()+"");
                }catch(Exception e){

                }


            }

            public void onFailure(int statusCode,Header[] headers,String result, Throwable throwable) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                orderAdapter = new OrderAdapter(context, orderList, false);
                list_client_order.setAdapter(orderAdapter);
                orderAdapter.notifyDataSetChanged();

                if(orderList.isEmpty()){
                    Toast.makeText(context, "Une erreur est survenue", Toast.LENGTH_LONG).show();
                }

                try{
                    Log.e("Client Order Failure St", result+"");
                }catch(Exception e){

                }

            }
        };
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Récupération des commandes");
        progressDialog.setCancelable(false);
        progressDialog.show();
        if(sharedPreferences.getBoolean("Prod", true)) {
            client.get(Constant.host + Constant.getOrders + "/" + id + "", responseHandler);
        }else{
            client.get(Constant.hostTest + Constant.getOrders + "/" + id + "", responseHandler);
        }
    }

}
