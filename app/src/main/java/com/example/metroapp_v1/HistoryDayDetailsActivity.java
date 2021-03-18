package com.example.metroapp_v1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class HistoryDayDetailsActivity extends AppCompatActivity {
    TextView cl_start, cl_end, cl_cals, cl_route,cl_time, onDayDetailsDateTv;

    ListView historyDayDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_day_details);
        historyDayDetails = findViewById(R.id.historyDayDetail);
        onDayDetailsDateTv = findViewById(R.id.onDayDetailsDateTv);
        String date = getIntent().getStringExtra("date");
        onDayDetailsDateTv.setText("All Trips on: " + date);
//        Toast.makeText(this, "Trips on: " + date, Toast.LENGTH_SHORT).show();
        List<UserHistory> userHistories = MetroDatabase.getInstance(this).userDAO().historyDayDetailsList(date);
        CustomAdapter adapter = new CustomAdapter(this, userHistories);
        historyDayDetails.setAdapter(adapter);

    }

    class CustomAdapter extends ArrayAdapter<UserHistory> {
        public CustomAdapter(@NonNull Context context, List<UserHistory> userHistories) {
            super(context, 0, userHistories);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null)
                convertView = getLayoutInflater().inflate(R.layout.cl_history_bydate, parent, false);

            cl_start = convertView.findViewById(R.id.cl_start);
            cl_end = convertView.findViewById(R.id.cl_end);
            cl_cals = convertView.findViewById(R.id.cl_cals);
            cl_route = convertView.findViewById(R.id.cl_rout);
            cl_time = convertView.findViewById(R.id.cl_time);

            cl_start.setText(getItem(position).start);
            cl_end.setText(getItem(position).end);
            cl_cals.setText(getItem(position).cals);
            cl_route.setText(getItem(position).route);
            cl_time.setText(getItem(position).time);


            return convertView;


        }
    }
}