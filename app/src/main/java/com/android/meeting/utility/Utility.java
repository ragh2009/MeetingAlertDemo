package com.android.meeting.utility;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import com.android.meeting.models.EventsData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Utility {
    public ArrayList<EventsData> readCalendarEvent(Context context) {
        ArrayList<EventsData> values = new ArrayList<>();
        // All events query
        // "calendar_id", "title", "description", "dtstart", "dtend", "eventLocation"

        Calendar startTime = Calendar.getInstance();

        startTime.set(Calendar.HOUR_OF_DAY, 0);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);

        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.DATE, 1);

        String selection = "(( " + CalendarContract.Events.DTSTART + " >= " + startTime.getTimeInMillis() + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + endTime.getTimeInMillis() + " ) AND ( deleted != 1 ))";

        Cursor cursor = context.getContentResolver()
                .query(
                        Uri.parse("content://com.android.calendar/events"),
                        new String[]{CalendarContract.Events._ID, CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION,
                                CalendarContract.Events.DTSTART, CalendarContract.Events.DURATION, CalendarContract.Events.EVENT_LOCATION}, selection,
                        null, null);
        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            EventsData value = new EventsData();
            value.setId(cursor.getInt(0));
            value.setTitle(cursor.getString(1));
            value.setDescription(cursor.getString(2));

            if (cursor.getString(3) != null)
                value.setDtStart(cursor.getString(3));
            else
                value.setDtStart("");
            if (cursor.getString(4) != null)
                value.setDtEnd(cursor.getString(4));
            else
                value.setDtEnd("");
            value.setEventLocation(cursor.getString(5));
            value.seteTimeFormat(getTimeFormat(Long.parseLong(value.getDtStart()), value.getDtEnd()));
            values.add(value);
            cursor.moveToNext();
        }

        return values;
    }

    public String getTimeFormat(long milliSeconds, String dtEnd) {
        String value = "";
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "hh:mm a");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliSeconds);
            String startTime = formatter.format(calendar.getTime());
            String[] startTimeFormat = startTime.replace(" ", "-").split("-");

            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeInMillis(milliSeconds + getEndTime(dtEnd));
            String endTime = formatter.format(calendar1.getTime());
            String[] endTimeFormat = endTime.replace(" ", "-").split("-");
            if (startTimeFormat[1].equalsIgnoreCase(endTimeFormat[1]))
                value = startTimeFormat[0] + " - " + endTimeFormat[0] + " " + startTimeFormat[1];
            else
                value = startTime + " - " + endTime;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    private long getEndTime(String dtEnd) {
        long value = 0;
        try {
            value = Long.valueOf(dtEnd.replace("P", "").replace("S", "")) * 1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
}