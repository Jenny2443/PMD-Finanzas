//package es.upm.etsiinf.pmd_financeapp;
//
//import android.app.Activity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import java.util.List;
//
//public class MyAdapter extends BaseAdapter {
//    private Activity ctx;
//
//    private List<Stock> data;
//
//    public MyAdapter(Activity ctx, List<Stock> data) {
//        this.ctx = ctx;
//        this.data = data;
//    }
//    @Override
//    public int getCount() {
//        return data.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return data.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View view, ViewGroup parent) {
//        if(view == null){
//            //Generar una vista
//            LayoutInflater layoutInflater = ctx.getLayoutInflater();
//            //null (raiz) se pone siempre, ponemos el layout
//            view = layoutInflater.inflate(R.layout.listview_stocks, null); //Pintar
//        }
//        //((ImageView) view.findViewById(R.id.stock_image)).setImageBitmap(data.get(position).getImageBmp());
//        //((TextView) view.findViewById(R.id.stock_item_name_listview)).setText(data.get(position).getName());
//        return null;
//    }
//}
