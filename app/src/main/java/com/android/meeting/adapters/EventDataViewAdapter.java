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
}
