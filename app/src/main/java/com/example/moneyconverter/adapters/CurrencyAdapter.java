package com.example.moneyconverter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyconverter.R;
import com.example.moneyconverter.models.Currency;
import com.squareup.picasso.Picasso;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.ViewHolder> {

    private List<Currency> list;
    private Context context;

    private onClick click;

    public CurrencyAdapter(List<Currency> list, Context context, onClick click) {
        this.list = list;
        this.context = context;
        this.click = click;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var view = LayoutInflater.from(context)
                .inflate(R.layout.currency_card, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        var item = list.get(position);
        holder.currencyName.setText(item.getCurrency());

        int idDrawable = context.getResources()
                .getIdentifier(item.getCurrency().substring(0, 2).toLowerCase(), "drawable", context.getPackageName());
        holder.currencyImage.setImageResource(idDrawable != 0 ? idDrawable : R.drawable.gold);

        holder.itemView.setOnClickListener(x -> click.onClick(item));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<Currency> list){
        if(list != null){
            this.list = list;
            notifyDataSetChanged();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        final TextView currencyName;
        final ImageView currencyImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            currencyName = itemView.findViewById(R.id.currency_name);
            currencyImage = itemView.findViewById(R.id.currency_image);
        }
    }

    public interface onClick{
        void onClick(Currency currency);
    }

}
