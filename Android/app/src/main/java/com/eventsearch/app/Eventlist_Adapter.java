package com.eventsearch.app;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

public class Eventlist_Adapter extends RecyclerView.Adapter<Eventlist_Adapter.EventViewHolder> {

    private List<Event> mEvents;

    private boolean isFavoritePage;

    EventListener listener;


    public interface EventListener {
        void OnFavoritesRemoved();
    }

    public Eventlist_Adapter(List<Event> events, boolean isFavoritePage, EventListener el) {
        this.mEvents = events;
        this.isFavoritePage = isFavoritePage;
        this.listener = el;
    }


    public List<Event> getEvents(){
        return mEvents;
    }

    // This method is called when a new ViewHolder is needed
    @Override
    public Eventlist_Adapter.EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.eventlist_row, parent, false);

        // Create a new ViewHolder
        EventViewHolder viewHolder = new EventViewHolder(v, this.isFavoritePage);

        return viewHolder;
    }

    // This method is called to display the data in each view
    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Event event = mEvents.get(position);


        // Set the data for the views
        holder.mEventName.setText(event.getName());
        try {

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(event.getDate());
            String outputDate = outputFormat.format(date);
            holder.mDate.setText(outputDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
            Date time = inputFormat.parse(event.getTime());
            SimpleDateFormat outputFormat = new SimpleDateFormat("h:mm a", Locale.US);
            String outputTime = outputFormat.format(time);
            holder.mTime.setText(outputTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.mVenueName.setText(event.getVenue());
        holder.mGenres.setText(event.getGenres().get(0));
        Glide.with(holder.mImageView.getContext())
                .load(event.getImageUrl())
                .into(holder.mImageView);

        if(HomeActivity.favoriteEvents.containsKey(event.getId())){
            holder.mFvtButton.setImageResource(R.drawable.heart_filled);
        }
        else{
            holder.mFvtButton.setImageResource(R.drawable.heart_outline);
        }
    }

    // This method returns the number of items in the list
    @Override
    public int getItemCount() {

        if(mEvents != null) {
            return mEvents.size();
        }
        return 0;
    }

    // This class is the ViewHolder that holds the views for each item
    public class EventViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mEventName;
        public TextView mDate;
        public TextView mTime;
        public TextView mVenueName;
        public TextView mGenres;
        public ImageButton mFvtButton;
        public ConstraintLayout mRowLayout;

        public EventViewHolder(View v, boolean isFavoritesPage) {
            super(v);
            mImageView = v.findViewById(R.id.imageView);
            mEventName = v.findViewById(R.id.nameTextView);
            mEventName.setSelected(true);
            mDate = v.findViewById(R.id.dateTextView);
            mTime = v.findViewById(R.id.timeTextView);
            mVenueName = v.findViewById(R.id.venueTextView);
            mVenueName.setSelected(true);
            mGenres = v.findViewById(R.id.genreTextView);
            mFvtButton = v.findViewById(R.id.fvtButton);

            mRowLayout = v.findViewById(R.id.event_row);
            mRowLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Log.d("EventViewHolder", "Event Clicked "+position);
                    if (position != RecyclerView.NO_POSITION) {
                        Event clickedEvent = mEvents.get(position);
                        Intent intent = new Intent(view.getContext(), EventDetailsActivity.class);
                        intent.putExtra("selected_event", clickedEvent);
                        view.getContext().startActivity(intent);
                    }
                }
            });

            mFvtButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("EventListAdapter: ", "Fvt clicked");
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Event clickedEvent = mEvents.get(position);
                        Log.d("EventListAdapter  Adding to Favts: ",clickedEvent.getName());
//                        HomeActivity.favoriteEvents.put(clickedEvent.getId(), clickedEvent);
                        if(HomeActivity.favoriteEvents.containsKey(clickedEvent.getId())){
                            HomeActivity.favoriteEvents.remove(clickedEvent.getId());
                            mFvtButton.setImageResource(R.drawable.heart_outline);
                            HomeActivity.saveFavoriteEvents(view.getContext());

                            Snackbar snackbar = Snackbar.make(view, clickedEvent.getName()+" removed from Favorites", 2000);
                            snackbar.getView()
                                    .setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.snackbar_style));
                            View snackView = snackbar.getView();
                            TextView tv = (TextView) snackView.findViewById(com.google.android.material.R.id.snackbar_text);
                            tv.setTextColor(Color.BLACK);
                            snackbar.show();

                            if (isFavoritesPage) {
                                mEvents.remove(position);
                                notifyItemRemoved(position);
                                notifyDataSetChanged();
                                listener.OnFavoritesRemoved();
                            }

                        } else {
                            HomeActivity.favoriteEvents.put(clickedEvent.getId(), clickedEvent);
                            mFvtButton.setImageResource(R.drawable.heart_filled);
                            HomeActivity.saveFavoriteEvents(view.getContext());

                            Snackbar snackbar = Snackbar.make(view, clickedEvent.getName()+" added to Favorites", 2000);
                            snackbar.getView()
                                    .setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.snackbar_style));
                            View snackView = snackbar.getView();
                            TextView tv = (TextView) snackView.findViewById(com.google.android.material.R.id.snackbar_text);
                            tv.setTextColor(Color.BLACK);
                            snackbar.show();
                        }
                    }
                }
            });
        }
    }
}
