package com.shamdroid.most3000words;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class Words extends Fragment implements UpdateableFragment{

    LinearLayout linearLayout;
    Context context;
    Button btnImportant,btnSearch ,btnFav,btnCancel;
    LinearLayout laySpin,laySearch,layFav;
    Spinner spinImportant;
    EditText etextSearch;
    RadioButton radFav,radAll;
    TextView txtWordsNum;

    ArrayList<WordItem>  allData ;
    InputMethodManager imm;
    sqlHelper sqlHelper;
    ArrayList<WordItem>  selectedData;

    RecyclerView recyclerView;

    MyRecyclerAdapter myRecyclerAdapter;
    private UpdateableFragment updateableFragment;


    public Words(Context context) {
        this.context = context;
    }

    public void setUpdateableFragment(UpdateableFragment updateableFragment) {
        this.updateableFragment = updateableFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        linearLayout = (LinearLayout) inflater.inflate(R.layout.activity_words,container,false);
        
        initialize();
        listener();
        sql();
        
        return linearLayout;
    }

    @Override
    public void onResume() {
        super.onResume();

        sql();
        fillData();

        //lv.setSelection(context.getSharedPreferences("settings", context.MODE_PRIVATE).getInt("last_lv_id",0));
    }

    private void initialize() {
        btnImportant     = (Button)         linearLayout.findViewById(R.id.btnImportant);
        btnSearch        = (Button)         linearLayout.findViewById(R.id.btnSearch);
        btnFav           = (Button)         linearLayout.findViewById(R.id.btnFav);
        btnCancel        = (Button)         linearLayout.findViewById(R.id.btnCancel);

        laySpin          = (LinearLayout)   linearLayout.findViewById(R.id.laySpin);
        laySearch        = (LinearLayout)   linearLayout.findViewById(R.id.laySearch);
        layFav           = (LinearLayout)   linearLayout.findViewById(R.id.layFav);

        spinImportant    = (Spinner)        linearLayout.findViewById(R.id.spinImportant);

        etextSearch      = (EditText)       linearLayout.findViewById(R.id.etextSearch);

        radAll           = (RadioButton)    linearLayout.findViewById(R.id.radAll);
        radFav           = (RadioButton)    linearLayout.findViewById(R.id.radFav);
        radAll.setChecked(true);

        recyclerView = (RecyclerView) linearLayout.findViewById(R.id.wordsRec);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        txtWordsNum      = (TextView)       linearLayout.findViewById(R.id.txtWordsNum);

        imm              = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        allData = new ArrayList<>();
        selectedData= new ArrayList<>();

        sqlHelper = new sqlHelper(context);

        String[] type = {"كل الكلمات","S1","W1","S2","W2","S3","W3"};
        ArrayAdapter spinAdapter = new ArrayAdapter(context,android.R.layout.simple_list_item_1,type);
        spinImportant.setAdapter(spinAdapter);


    }

    public void listener(){
        btnImportant.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(laySpin.getVisibility() == View.GONE)
                            laySpin.setVisibility(View.VISIBLE);
                        else
                            laySpin.setVisibility(View.GONE);

                        laySearch.setVisibility(View.GONE);
                        layFav.setVisibility(View.GONE);

                        imm.hideSoftInputFromWindow(etextSearch.getWindowToken(), 0);

                    }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(laySearch.getVisibility() == View.GONE) {
                            laySearch.setVisibility(View.VISIBLE);
                            etextSearch.requestFocus();
                            imm.showSoftInput(etextSearch, InputMethodManager.SHOW_IMPLICIT);
                        }else {
                            laySearch.setVisibility(View.GONE);
                            etextSearch.clearFocus();
                            imm.hideSoftInputFromWindow(etextSearch.getWindowToken(), 0);
                        }
                        laySpin.setVisibility(View.GONE);
                        layFav.setVisibility(View.GONE);
                    }
        });

        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layFav.getVisibility() == View.GONE)
                    layFav.setVisibility(View.VISIBLE);
                else
                    layFav.setVisibility(View.GONE);

                laySpin.setVisibility(View.GONE);
                laySearch.setVisibility(View.GONE);

                imm.hideSoftInputFromWindow(etextSearch.getWindowToken(), 0);

            }
        });

        etextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fillData();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etextSearch.setText("");
                etextSearch.clearFocus();
                imm.hideSoftInputFromWindow(etextSearch.getWindowToken(), 0);
                fillData();
                laySearch.setVisibility(View.GONE);
            }
        });

        radAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fillData();
            }
        });

        radFav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fillData();
            }
        });

        spinImportant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fillData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




    }


    private void sql() {

        allData.clear();
        try {
            sqlHelper.importIfNotExist();
            sqlHelper.openDataBase();

            Cursor cursor = sqlHelper.getAll();
            cursor.moveToFirst();

            do{
                allData.add(new WordItem(cursor.getInt(0),cursor.getString(1),cursor.getString(2), (cursor.getInt(3)) != 0));
            }while(cursor.moveToNext());

            sqlHelper.close();

            cursor = null;
            System.gc();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fillData(){
        selectedData = new ArrayList<>();
        if(radAll.isChecked() && spinImportant.getSelectedItemId() == 0 && etextSearch.getText().toString().isEmpty()) {
            for (int i = 0; i < allData.size(); i++) {
               selectedData.add(allData.get(i));
            }
        }else if(radAll.isChecked() && spinImportant.getSelectedItemId() == 0 && !(etextSearch.getText().toString().isEmpty())) {
            for (int i = 0; i < allData.size(); i++) {
                if(allData.get(i).getWordEn().contains(etextSearch.getText().toString()) || allData.get(i).getWordAr().contains(etextSearch.getText().toString()) )
                   selectedData.add(allData.get(i));
            }
        }else if(radAll.isChecked() && spinImportant.getSelectedItemId() > 0 && etextSearch.getText().toString().isEmpty()) {
            for (int i = 0; i < allData.size(); i++) {
                if(allData.get(i).getWordEn().contains(spinImportant.getSelectedItem().toString()))
                   selectedData.add(allData.get(i));
            }
        }else if(radAll.isChecked() && spinImportant.getSelectedItemId() > 0 && !(etextSearch.getText().toString().isEmpty())) {
            for (int i = 0; i < allData.size(); i++) {
                if(allData.get(i).getWordEn().contains(spinImportant.getSelectedItem().toString()) && (allData.get(i).getWordEn().contains(etextSearch.getText().toString()) || allData.get(i).getWordAr().contains(etextSearch.getText().toString())))
                   selectedData.add(allData.get(i));
            }
        }else if(radFav.isChecked() && spinImportant.getSelectedItemId() == 0 && etextSearch.getText().toString().isEmpty()) {
            for (int i = 0; i < allData.size(); i++) {
                if(!allData.get(i).isFav())
                   selectedData.add(allData.get(i));
            }
        }else if(radFav.isChecked() && spinImportant.getSelectedItemId() == 0 && !(etextSearch.getText().toString().isEmpty())) {
            for (int i = 0; i < allData.size(); i++) {
                if(!allData.get(i).isFav() && (allData.get(i).getWordEn().contains(etextSearch.getText().toString()) || allData.get(i).getWordAr().contains(etextSearch.getText().toString())))
                   selectedData.add(allData.get(i));
            }
        }else if(radFav.isChecked() && spinImportant.getSelectedItemId() > 0 && etextSearch.getText().toString().isEmpty()) {
            for (int i = 0; i < allData.size(); i++) {
                if(!allData.get(i).isFav() && allData.get(i).getWordEn().contains(spinImportant.getSelectedItem().toString()))
                   selectedData.add(allData.get(i));
            }
        }else if(radFav.isChecked() && spinImportant.getSelectedItemId() > 0 && !(etextSearch.getText().toString().isEmpty())) {
            for (int i = 0; i < allData.size(); i++) {
                if(!allData.get(i).isFav() && allData.get(i).getWordEn().contains(spinImportant.getSelectedItem().toString()) && (allData.get(i).getWordEn().contains(etextSearch.getText().toString()) || allData.get(i).getWordAr().contains(etextSearch.getText().toString())))
                   selectedData.add(allData.get(i));
            }
        }


        myRecyclerAdapter = new MyRecyclerAdapter(selectedData,context);
        myRecyclerAdapter.setUpdateableFragment(updateableFragment);
        recyclerView.setAdapter(myRecyclerAdapter);
        txtWordsNum.setText(getResources().getString(R.string.words_num) + " " + selectedData.size());

    }

    @Override
    public void update() {
        sql();
        fillData();
    }

    @Override
    public void update(int n) {

    }
}
