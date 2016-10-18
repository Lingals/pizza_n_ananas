package insset.com.pizzanananas;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import insset.com.utils.Constant;

public class OrdersList extends AppCompatActivity {

    ListView list_of_orders;
    ProgressDialog progressDialog;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_list);

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


        client.setMaxRetriesAndTimeout(2, 3000);

        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {


            public void onSuccess(int statusCode,Header[] headers,org.json.JSONArray response) {
                System.out.println("Success");
                Log.e("Je vois", "La reponse" + response.toString());
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
