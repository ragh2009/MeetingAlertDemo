package com.android.meeting;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.meeting.adapters.EventDataViewAdapter;
import com.android.meeting.listeners.EventAlarmAddListeners;
import com.android.meeting.models.EventsData;
import com.android.meeting.utility.Utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class MeetingViewFragment extends Fragment implements EventAlarmAddListeners {
    private final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    private TimePickerDialog timePickerDialog;
    private ArrayList<EventsData> eventsData;
    private RecyclerView eventsViewList;
    private EventDataViewAdapter eventAdapter = null;
    private TextView noDataTxt;
    private SharedPreferences mPref;
    private int addAlarmPos;
    private CountDownTimer countDownTimer;
    final Handler handler = new Handler();
    private Runnable mHandlerTask;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPref = getActivity().getSharedPreferences("MPrefs", MODE_PRIVATE);
        eventsViewList = view.findViewById(R.id.events_view);
        noDataTxt = view.findViewById(R.id.no_events_text);
        initRepeatingTask();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkingCalendarPermission();
        startRepeatingTask();
    }

    @Override
    public void onPause() {
        stopRepeatingTask();
        super.onPause();
    }

    private void checkingCalendarPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALENDAR)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CALENDAR)
                == PackageManager.PERMISSION_GRANTED) {
            bindEventsData();
        } else {
            List<String> listPermissionsNeeded = new ArrayList<>();
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALENDAR)
                    != PackageManager.PERMISSION_GRANTED)
                listPermissionsNeeded.add(Manifest.permission.READ_CALENDAR);

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CALENDAR)
                    != PackageManager.PERMISSION_GRANTED)
                listPermissionsNeeded.add(Manifest.permission.WRITE_CALENDAR);

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray
                        (new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }
    }

    private void bindEventsData() {
        eventsData = new Utility().readCalendarEvent(getActivity());
        Collections.reverse(eventsData);
        if (eventsData == null)
            eventsData = new ArrayList<>();
        if (eventsData.size() == 0)
            noDataTxt.setVisibility(View.VISIBLE);
        else {
            noDataTxt.setVisibility(View.GONE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
            eventsViewList.setLayoutManager(linearLayoutManager);

            eventAdapter = new EventDataViewAdapter(getActivity(), eventsData, this);
            eventsViewList.setAdapter(eventAdapter);
            addAndUpdateReminders();
        }
    }

    private void addAndUpdateReminders() {
        Calendar calendar = Calendar.getInstance();
        try {
            SharedPreferences.Editor editor = mPref.edit();

            for (int i = 0; i < eventsData.size(); i++) {
                Intent intent = new Intent(getActivity(), AlarmReceiver.class);
                long aTime = Long.parseLong(eventsData.get(i).getDtStart());
                if (aTime > 0 && ((aTime - calendar.getTimeInMillis()) > 600000)) {
                    calendar.setTimeInMillis(aTime - 600000); // -10 mins
                    intent.putExtra("MData", eventsData.get(i));
                    if (!mPref.getBoolean(eventsData.get(i).getId() + "", false)) {
                        editor.putBoolean(eventsData.get(i).getId() + "", true);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), eventsData.get(i).getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                        Log.v("AlarmTime:", new Utility().getTime(calendar.getTimeInMillis()));
                        if (alarmManager != null)
                            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    }
                }
            }

            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {
            boolean isPermitted = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (isPermitted)
                    isPermitted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }
            if (!isPermitted)
                checkingCalendarPermission();
        }
    }

    @Override
    public void addAlarm(int position) {
        addAlarmPos = position;
        openTimePickerDialog();
    }

    private void openTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        timePickerDialog = new TimePickerDialog(getActivity(),
                onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), false);
        timePickerDialog.setTitle("Set Alarm Time");
        timePickerDialog.show();
    }

    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();

            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

            if (calSet.compareTo(calNow) <= 0) {
                // Today Set time passed, count to tomorrow
                calSet.add(Calendar.DATE, 1);
            }

            Intent intent = new Intent(getActivity(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    getContext(), eventsData.get(addAlarmPos).getId(), intent, 0);
            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(),
                    pendingIntent);
        }
    };

    public void initRepeatingTask() {
        try {
            mHandlerTask = new Runnable() {
                @Override
                public void run() {
                    try {
                        eventAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        // TODO: handle exception
                    } finally {
                        //also call the same runnable to call it at regular interval
                        handler.postDelayed(this, 60000);
                    }
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startRepeatingTask()
    {
        try {
            mHandlerTask.run();
        } catch (Exception e) {}
    }

    private void stopRepeatingTask()
    {
        try {
            handler.removeCallbacks(mHandlerTask);
        } catch (Exception e) {}
    }

}
