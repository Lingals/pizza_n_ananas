package insset.com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import insset.com.models.Order;
import insset.com.pizzanananas.R;

/**
 * Created by pierre on 18/10/2016.
 */
public class OrderAdapter extends BaseAdapter {

    protected Context context;
    private LayoutInflater inflater;
    private List<Order> items = new ArrayList<Order>();
    boolean admin = false;

    public OrderAdapter(Context context, List<Order> liste, boolean admin) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.items = liste;
        this.admin = admin;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Order getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        OrderViewHolder orderView = null;

        Order order = getItem(position);

        if (convertView == null) {
            orderView = new OrderViewHolder();
            convertView = inflater.inflate(R.layout.order_item, null);

            orderView.order_item_pizza_name = (TextView) convertView.findViewById(R.id.order_item_pizza_name);
            orderView.order_item_pizza_price = (TextView) convertView.findViewById(R.id.order_item_pizza_price);
            orderView.order_item_pizza_status = (TextView) convertView.findViewById(R.id.order_item_pizza_status);

            convertView.setTag(orderView);
        } else {
            orderView = (OrderViewHolder) convertView.getTag();
        }

        if(admin){
            orderView.order_item_pizza_name.setText(order.getId()+". "+order.getPizza().getName()+"");
        }else{
            orderView.order_item_pizza_name.setText(order.getPizza().getName()+"");
        }
        orderView.order_item_pizza_price.setText(order.getPizza().getPrice()+"");
        orderView.order_item_pizza_status.setText(order.getStatus()+"");

        return convertView;
    }

    public static class OrderViewHolder {
        TextView order_item_pizza_status, order_item_pizza_name, order_item_pizza_price;
    }
}
