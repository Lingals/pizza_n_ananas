package insset.com.pizzanananas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import insset.com.adapters.PizzaAdapter;
import insset.com.models.Pizza;
import insset.com.utils.Constant;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;

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

                /*try{
                    Log.e("Pizza list", response.toString() + "");
                }catch(Exception e){

                }*/
            }

            public void onFailure(int statusCode,Header[] headers, Throwable throwable,	org.json.JSONObject response) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                Toast.makeText(context, "Une erreur serveur est survenue", Toast.LENGTH_LONG).show();

                DownloadTwitterTask dtt = new DownloadTwitterTask();
                dtt.execute();
            }

            public void onFailure(int statusCode,Header[] headers,String result, Throwable throwable) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                Toast.makeText(context, "Une erreur est survenue", Toast.LENGTH_LONG).show();


                DownloadTwitterTask dtt = new DownloadTwitterTask();
                dtt.execute();

            }
        };
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Récupération des pizzas");
        progressDialog.setCancelable(false);
        progressDialog.show();
        client.get(Constant.host + Constant.getPizzas, responseHandler);
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
                    DateFormat date = new SimpleDateFormat("dd/MM/yyyy à HH:mm:ss");
                    Date tweetDate = responseTw.get(0).getCreatedAt();
                    result = date.format(tweetDate)+" "+responseTw.get(0).getText();

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
