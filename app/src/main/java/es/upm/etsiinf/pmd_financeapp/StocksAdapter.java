package es.upm.etsiinf.pmd_financeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;
import es.upm.etsiinf.pmd_financeapp.Stock;

public class StocksAdapter extends ArrayAdapter<Stock> {

    public StocksAdapter(@NonNull Context context, @NonNull List<Stock> stocksList) {
        super(context, 0, stocksList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_stocks, parent, false);
        }

        Stock currentStock = getItem(position);

        TextView symbolTextView = listItemView.findViewById(R.id.symbolTextView);
        symbolTextView.setText(currentStock.getSymbol());

        TextView nameTextView = listItemView.findViewById(R.id.nameTextView);
        nameTextView.setText(currentStock.getName());

        TextView priceTextView = listItemView.findViewById(R.id.priceTextView);
        priceTextView.setText(String.valueOf(currentStock.getPrice()));

        return listItemView;
    }
}
