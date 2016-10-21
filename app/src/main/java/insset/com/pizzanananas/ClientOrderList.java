package insset.com.pizzanananas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import insset.com.adapters.OrderAdapter;
import insset.com.models.Order;
import insset.com.models.Pizza;
import insset.com.utils.Constant;
import insset.com.utils.Lilibrato;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

public class ClientOrderList extends AppCompatActivity {

    Context context;
    ListView list_client_order;
    EditText editText_client_order_list;
    Button button_client_order_list;
    ProgressDialog progressDialog;
    List<Order> orderList = new ArrayList<>();
    OrderAdapter orderAdapter;
    SharedPreferences sharedPreferences;

    Lilibrato lilibrato = null;

    long dateStart = 0,
            dateEnd = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_order_list);

        setTitle("Liste des commandes");

        context = this;

        sharedPreferences = getSharedPreferences("ERROR_LOG", MODE_PRIVATE);

        lilibrato = new Lilibrato("PizzaNananas.ClientOrderList");

        initializeFields();

        Intent intent = getIntent();
        String idPizza = null;

        idPizza = intent.getStringExtra("idPizza");

        if (idPizza != null && !idPizza.equals("")) {
            editText_client_order_list.setText(idPizza);

            dateStart = System.currentTimeMillis();

            getOrders(idPizza);
        }

        button_client_order_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editText_client_order_list.getText().toString().isEmpty()) {

                    dateStart = System.currentTimeMillis();
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

                dateEnd = System.currentTimeMillis();

                lilibrato.setTimes(dateStart, dateEnd);
                lilibrato.setStatus(statusCode);

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

                lilibrato.setStatus(statusCode);

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

                DownloadTwitterTask dtt = new DownloadTwitterTask();
                dtt.execute();
            }

            public void onFailure(int statusCode,Header[] headers,String result, Throwable throwable) {

                lilibrato.setStatus(statusCode);

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

                DownloadTwitterTask dtt = new DownloadTwitterTask();
                dtt.execute();
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

    // Uses an AsyncTask to download a Twitter user's timeline
    private class DownloadTwitterTask extends AsyncTask<String, Void, String> {
        final static String CONSUMER_KEY = "MY CONSUMER KEY";
        final static String CONSUMER_SECRET = "MY CONSUMER SECRET";
        final static String TwitterTokenURL = "https://api.twitter.com/oauth2/token";
        final static String TwitterStreamURL = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=";

        @Override
        protected String doInBackground(String... screenNames) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey("tafGT8qOnr3L712G0CrnaBV1V");
            builder.setOAuthConsumerSecret("2SpKtupj5QmayWa1kpwn0Ufi9R9252eitEflrKEfx4eFU5nR0j");
            AccessToken accessToken = new AccessToken("788640120496939008-lRyhIURSbpk1RyltnNzBY94vmYUbgSJ",
                    "TLBvSIEqqLV96Dh4CqMPBevi18o385Fcowf9K8ts40PJz");
            String result= "";
            Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
            ResponseList<twitter4j.Status> responseTw = null;
            try {

                responseTw = twitter.getUserTimeline();
                Log.d("TWITTER", responseTw.toString());
                if (responseTw.size()>0){
                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy à HH:mm:ss");
                    Date tweetDate = responseTw.get(0).getCreatedAt();

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(tweetDate);
                    tweetDate = cal.getTime();

                    result = df.format(tweetDate) + " : ";
                    result += responseTw.get(0).getText();
                }

            } catch (TwitterException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
            return result;
        }

        // onPostExecute convert the JSON results into a Twitter object (which is an Array list of tweets
        @Override
        protected void onPostExecute(String result) {
            Log.d("Twitter", "It Works bitch");

            Intent intent = new Intent(getApplicationContext(), ErrorPage.class);
            Log.e("tweet",result);
            intent.putExtra("tweet", result);
            startActivity(intent);
            finish();
        }
    }

}
