package insset.com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import insset.com.models.Pizza;
import insset.com.pizzanananas.R;

/**
 * Created by quentin on 18/10/16.
 */
public class PizzaAdapter extends BaseAdapter {

    protected Context context;
    private LayoutInflater inflater;
    private List<Pizza> items = new ArrayList<Pizza>();

    public PizzaAdapter(Context context, List<Pizza> liste) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.items = liste;
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

        Pizza pizza = getItem(position);

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
                
            }
        });

        return convertView;
    }

    public static class PizzaViewHolder {
        TextView pizza_item_name_tv, pizza_item_price_tv;
    }
}
