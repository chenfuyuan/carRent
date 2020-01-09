package com.example.carrent.utils;

import android.widget.DatePicker;
import android.widget.TimePicker;

public class GetTimeUtils {

    public static String getTime(DatePicker start_date_picker, TimePicker start_time_picker) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(start_date_picker.getYear() + "-");
        stringBuilder.append(start_date_picker.getMonth()+1+ "-");
        stringBuilder.append(start_date_picker.getDayOfMonth()+" ");
        stringBuilder.append(start_time_picker.getCurrentHour() + ":");
        stringBuilder.append(start_time_picker.getCurrentMinute() + ":00");


        return stringBuilder.toString();
    }

}
