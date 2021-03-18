package com.example.metroapp_v1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class HistoryListActivty extends AppCompatActivity {
    ListView historyList;
    UserHistory userHistory;

    String dateDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_list);
        historyList = findViewById(R.id.historyList);

        List<String> allDatesHistory = MetroDatabase.getInstance(this).userDAO().selctAllDatesHistory();

        ArrayAdapter<String> adapterrrrr = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allDatesHistory);
        historyList.setAdapter(adapterrrrr);

        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dateDetails = allDatesHistory.get(position);
                Intent intent = new Intent(HistoryListActivty.this, HistoryDayDetailsActivity.class);
                intent.putExtra("date", dateDetails);
                startActivity(intent);
            }
        });


        historyList.setOnItemLongClickListener((parent, view, position, id) -> {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        dateDetails   = allDatesHistory.get(position);
                        List<UserHistory>userHistories= MetroDatabase.getInstance(this).userDAO().historyDayDetailsList(dateDetails);

                        Log.i("date", dateDetails + "");
                        for (UserHistory history : userHistories) {
                            MetroDatabase.getInstance(this).userDAO().dayHistoryDetails(history);
                        }
                        adapterrrrr.notifyDataSetChanged();
                        Toast.makeText(HistoryListActivty.this, "history Deleted", Toast.LENGTH_LONG).show();
                        allDatesHistory.remove(position);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };
            AlertDialog.Builder ab = new AlertDialog.Builder(HistoryListActivty.this);
            ab.setMessage("Are you sure to delete all history in this day?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();


            return true;
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete, menu);
        return true;
    }



}