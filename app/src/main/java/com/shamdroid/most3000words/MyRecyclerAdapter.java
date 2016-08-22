package com.shamdroid.most3000words;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.ArrayList;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {


    ArrayList<WordItem> data;
    Context context;
    sqlHelper mySqlHelper ;

    UpdateableFragment updateableFragment;

    public MyRecyclerAdapter(ArrayList<WordItem> data, Context context) {
        this.data = data;
        this.context = context;
        mySqlHelper = new sqlHelper(context);

    }
    void setUpdateableFragment(UpdateableFragment updateableFragment){
        this.updateableFragment = updateableFragment;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_row, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {


        holder.word.setText(data.get(position).getWordEn());

        holder.isFav.setChecked(data.get(position).isFav());

        holder.isFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToggleButton toggleButton = (ToggleButton) v;
                try {
                    mySqlHelper.importIfNotExist();
                    mySqlHelper.openDataBase();

                    if(toggleButton.isChecked()){
                        mySqlHelper.addToFav(data.get(position).id);
                    }else{
                        mySqlHelper.removeFromFav(data.get(position).id);
                    }
                    mySqlHelper.close();
                    data.get(position).setIsFav(toggleButton.isChecked());

                    if(updateableFragment instanceof Words){
                        data.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position,data.size());
                        updateableFragment.update(data.size());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

        holder.recRowLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int n = data.get(position).getId();

                //context.getSharedPreferences("settings", context.MODE_PRIVATE).edit().putInt("last_lv_id",(int)id).apply();
                Intent intent = new Intent("com.shamdroid.longman3000words.ShowWords");
                intent.putExtra("id", n);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView word;
        ToggleButton isFav;
        LinearLayout recRowLinear;

        public MyViewHolder(View itemView) {
            super(itemView);

            word = (TextView) itemView.findViewById(R.id.txtRecWord);
            isFav = (ToggleButton) itemView.findViewById(R.id.tbtnFavRecycler);
            recRowLinear = (LinearLayout) itemView.findViewById(R.id.recRowLinear);
        }
    }
}
