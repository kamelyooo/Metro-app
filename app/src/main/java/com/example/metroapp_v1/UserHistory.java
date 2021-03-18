package com.example.metroapp_v1;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "userHistory")
public class UserHistory {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String start, end, route, cals, date, time;
}
