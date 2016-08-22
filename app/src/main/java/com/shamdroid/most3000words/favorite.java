package com.shamdroid.most3000words;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class favorite extends Fragment implements UpdateableFragment {

    LinearLayout linearLayout;

    Context context;
    TextView txtFavNum;
    RecyclerView recyclerView;
    MyRecyclerAdapter myRecyclerAdapter ;

    sqlHelper sqlHelper;
    ArrayList<WordItem> allData;
    Cursor cursor;
    int num;
     UpdateableFragment updateableFragment;


    public favorite(Context context) {
        this.context = context;
    }

    public void setUpdateableFragment(UpdateableFragment updateableFragment) {
        this.updateableFragment = updateableFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        linearLayout = (LinearLayout) inflater.inflate(R.layout.activity_favorite, container, false);
        return linearLayout;

        
    }


    @Override
    public void onResume() {
        super.onResume();
        initialize();
        sql();
    }

    public void initialize() {
        recyclerView = (RecyclerView) linearLayout.findViewById(R.id.favRec);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        txtFavNum = (TextView) linearLayout.findViewById(R.id.txtFavNum);

    }


    public void sql() {
        sqlHelper = new sqlHelper(context);
        allData = new ArrayList<WordItem>();
        allData.clear();
        try {
            sqlHelper.importIfNotExist();
            sqlHelper.openDataBase();

            cursor = sqlHelper.getAllFav();
            if (cursor.moveToFirst()) {
                do {
                    allData.add(new WordItem(cursor.getInt(0),cursor.getString(1),cursor.getString(2), (cursor.getInt(3)) != 0));
                } while (cursor.moveToNext());
            }

            myRecyclerAdapter = new MyRecyclerAdapter(allData,context);
            myRecyclerAdapter.setUpdateableFragment(updateableFragment);

            if(recyclerView != null)
                recyclerView.setAdapter(myRecyclerAdapter);
            num = sqlHelper.getNum();
            if(txtFavNum != null)
                txtFavNum.setText(getResources().getString(R.string.fav_num) + " " + allData.size() + " من " + num);

            sqlHelper.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        sql();
    }

    @Override
    public void update(int n) {
        txtFavNum.setText(getResources().getString(R.string.fav_num) + " " + n + " من " + num);
    }
}
