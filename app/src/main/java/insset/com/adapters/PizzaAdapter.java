package insset.com.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import insset.com.models.Pizza;
import insset.com.pizzanananas.ClientOrderList;
import insset.com.pizzanananas.R;
import insset.com.utils.Constant;

/**
 * Created by quentin on 18/10/16.
 */
public class PizzaAdapter extends BaseAdapter {

    protected Context context;
    private LayoutInflater inflater;
    private List<Pizza> items = new ArrayList<Pizza>();
    private ProgressDialog progressDialog;
    boolean prod = true;

    public PizzaAdapter(Context context, List<Pizza> liste, boolean prod) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.items = liste;
        this.prod = prod;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Pizza getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        PizzaViewHolder pizzaView = null;

        final Pizza pizza = getItem(position);

        if (convertView == null) {
            pizzaView = new PizzaViewHolder();
            convertView = inflater.inflate(R.layout.pizza_item, null);

            pizzaView.pizza_item_name_tv = (TextView) convertView.findViewById(R.id.pizza_item_name_tv);
            pizzaView.pizza_item_price_tv = (TextView) convertView.findViewById(R.id.pizza_item_price_tv);

            convertView.setTag(pizzaView);
        } else {
            pizzaView = (PizzaViewHolder) convertView.getTag();
        }

        pizzaView.pizza_item_name_tv.setText(pizza.getName()+"");
        pizzaView.pizza_item_price_tv.setText(pizza.getPrice()+"");

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOrder(pizza.getId());
            }
        });

        return convertView;
    }

    public static class PizzaViewHolder {
        TextView pizza_item_name_tv, pizza_item_price_tv;
    }

    private void sendOrder(int idPizza) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        JSONObject jsonPizza = new JSONObject();

        try {
            jsonPizza.put("id", idPizza);

            params.add("data", jsonPizza.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringEntity entity = new StringEntity(jsonPizza.toString(), "UTF-8");

        client.addHeader("Authorization", Constant.Authorization);

        client.setMaxRetriesAndTimeout(2, 3000);

        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {

            public void onSuccess(int statusCode,Header[] headers,org.json.JSONObject response) {

                try {
                    Toast.makeText(context, "Votre numéro de commande est le " + response.getString("id"), Toast.LENGTH_LONG).show();

                    Intent i = new Intent(context, ClientOrderList.class);
                    i.putExtra("idPizza", response.getString("id"));
                    context.startActivity(i);
                    ((Activity) context).finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
        progressDialog.setMessage("Traitement de la commande");
        progressDialog.setCancelable(false);
        progressDialog.show();
        if(prod){
            client.post(context, Constant.host + Constant.postOrders, entity, "application/json", responseHandler);
        }else {
            client.post(context, Constant.hostTest + Constant.postOrders, entity, "application/json", responseHandler);
        }
    }
}
