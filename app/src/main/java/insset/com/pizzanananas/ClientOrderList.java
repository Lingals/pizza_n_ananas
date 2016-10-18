package insset.com.pizzanananas;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_order_list);

        context = this;

        initializeFields();

        button_client_order_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editText_client_order_list.getText().toString().isEmpty()){
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

        Log.e("ID ORDER", id + "");

        client.addHeader("Authorization", Constant.Authorization);


        client.setMaxRetriesAndTimeout(2, 3000);

        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {


            public void onSuccess(int statusCode, Header[] headers, org.json.JSONObject orderJson) {
                System.out.println("Success");
                Log.e("Je vois", "La reponse" + orderJson.toString());

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
                list_client_order.setAdapter(new OrderAdapter(context, orderList, false));


                if (progressDialog.isShowing())
                    progressDialog.dismiss();

            }

            public void onFailure(int statusCode,Header[] headers, Throwable throwable,	org.json.JSONObject response) {
                System.out.println("Failure Json");
                Log.e("Je vois", "La reponse" + response.toString());
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }

            public void onFailure(int statusCode,Header[] headers,String result, Throwable throwable) {
                System.out.println("Failure String");
                Log.e("Status Code", statusCode + "");
                Log.e("Je vois", "La reponse" + result.toString());
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

            }
        };
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Récupération des commandes");
        progressDialog.setCancelable(false);
        progressDialog.show();
        client.get(Constant.host + Constant.getOrders+"/"+id+"", responseHandler);
    }

}
