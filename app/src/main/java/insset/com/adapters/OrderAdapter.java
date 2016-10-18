package insset.com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import insset.com.models.Order;

/**
 * Created by pierre on 18/10/2016.
 */
public class OrderAdapter extends BaseAdapter {

    protected Context context;
    private LayoutInflater inflater;
    private List<Order> items = new ArrayList<Order>();

    public OrderAdapter(Context context, List<Order> liste) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.items = liste;
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

        return convertView;
    }
}
