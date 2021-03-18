package com.example.metroapp_v1;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface userDAO {

    @Insert
    long insert(UserHistory userHistory);

    @Query("SELECT * FROM userHistory WHERE start=:start AND `end` =:end AND date =:date")
    UserHistory selectUserByfrom(String start, String end, String date);


    @Query("SELECT DISTINCT date FROM userHistory")
    List<String> selctAllDatesHistory();

    @Query(" SELECT * FROM userHistory where date=:date")
    List<UserHistory> historyDayDetailsList(String date);

   @Delete
    int dayHistoryDetails(UserHistory userHistory);
}
