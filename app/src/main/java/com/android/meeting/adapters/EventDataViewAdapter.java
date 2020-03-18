package com.android.meeting.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.meeting.R;
import com.android.meeting.listeners.EventAlarmAddListeners;
import com.android.meeting.models.EventsData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class EventDataViewAdapter extends RecyclerView.Adapter<EventDataViewAdapter.ViewHolder> {
    private ArrayList<EventsData> eventsData;
    private EventAlarmAddListeners myListeners;
    private Context mContext;

    public EventDataViewAdapter(Context context, ArrayList<EventsData> eventsData, EventAlarmAddListeners mListeners) {
        this.eventsData = eventsData;
        this.mContext = context;
        this.myListeners = mListeners;
    }

    @Override
    public EventDataViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.event_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.eTitle.setText(eventsData.get(position).getTitle());

        holder.eStartDate.setText("Time: " + eventsData.get(position).geteTimeFormat());

        if (eventsData.get(position).getDescription() != null && eventsData.get(position).getDescription().length() > 0)
            holder.eDescription.setText("Agenda: " + eventsData.get(position).getDescription());
        else
            holder.eDescription.setText("Agenda: NA");

        int minutes = getAlarmTime(Long.parseLong(eventsData.get(position).getDtStart()));
        if (minutes > 0) {
            if (minutes < 60)
                holder.eAddAlarm.setText("(in " + minutes + " min)");
            else
                holder.eAddAlarm.setText("(in " + (minutes / 60) + " hr.)");
            holder.eAddAlarm.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_alarm_on, 0, 0);
        } else {
            holder.eAddAlarm.setText("");
            holder.eAddAlarm.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_alarm_off, 0, 0);
        }

        holder.eAddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myListeners.addAlarm(holder.getAdapterPosition());
            }
        });
    }


    @Override
    public int getItemCount() {
        return eventsData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView eTitle, eStartDate, eDescription, eAddAlarm;

        public ViewHolder(View mView) {
            super(mView);
            eTitle = mView.findViewById(R.id.e_title);
            eStartDate = mView.findViewById(R.id.e_start_date);
            eDescription = mView.findViewById(R.id.e_description);
            eAddAlarm = mView.findViewById(R.id.e_add_alarm);
        }
    }

    public int getAlarmTime(long milliSeconds) {
        int mins = 0;
        try {
            Calendar calendar = Calendar.getInstance();
            long alarmTime = milliSeconds - 600000;
            if (alarmTime >= calendar.getTimeInMillis()) {
                mins = (int) ((alarmTime- calendar.getTimeInMillis()) / 60000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mins;
    }
}
