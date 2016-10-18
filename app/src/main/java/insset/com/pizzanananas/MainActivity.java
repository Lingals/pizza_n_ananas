package insset.com.pizzanananas;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import insset.com.utils.Constant;

public class MainActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        getPizzas();
    }

    private void getPizzas() {
        AsyncHttpClient client = new AsyncHttpClient();
        //RequestParams params = new RequestParams();

        client.addHeader("Authorization", Constant.Authorization);


        client.setMaxRetriesAndTimeout(2, 3000);

        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {


            public void onSuccess(int statusCode,Header[] headers,org.json.JSONArray response) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

            }

            public void onFailure(int statusCode,Header[] headers, Throwable throwable,	org.json.JSONObject response) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }

            public void onFailure(int statusCode,Header[] headers,String result, Throwable throwable) {
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
