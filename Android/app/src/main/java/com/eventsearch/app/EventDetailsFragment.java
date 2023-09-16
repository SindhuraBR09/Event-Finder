package com.eventsearch.app;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Event selectedEvent;

    public ImageView mImageView;
    public TextView mDate;
    public TextView mTime;
    public TextView mArtists;
    public TextView mVenueName;
    public TextView mGenres;
    public TextView mPriceRanges;
    public TextView mTicketStatus;
    public TextView mBuyAtURL;

    public LinearLayout eventDetailsCard;
    public ProgressBar pb;

    public TextView mDateTag;

    public TextView mTimeTag;

    public TextView mVenuetag;

    public TextView mPriceRangesTag;
    public EventDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventDetailsFragment newInstance(String param1, String param2) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            selectedEvent = getArguments().getParcelable("selected_event");
        }

        eventDetailsCard = view.findViewById(R.id.eventDetailsCard);
        pb = view.findViewById(R.id.progress_bar);
        mVenueName =  view.findViewById(R.id.venueValue);
        mVenueName.setSelected(true);
        mArtists =  view.findViewById(R.id.artistValue);
        mArtists.setSelected(true);
        mDate =  view.findViewById(R.id.dateValue);
        mDate.setSelected(true);
        mTime =  view.findViewById(R.id.timeValue);
        mTime.setSelected(true);
        mPriceRanges =  view.findViewById(R.id.priceValue);
        mPriceRanges.setSelected(true);
        mGenres = view.findViewById(R.id.genreValue);
        mGenres.setSelected(true);
        mTicketStatus = view.findViewById(R.id.ticketStatusValue);
        mBuyAtURL = view.findViewById(R.id.buyAtValue);
        mBuyAtURL.setSelected(true);
        mImageView = view.findViewById(R.id.venueImage);

        mDateTag = view.findViewById(R.id.dateTag);
        mTimeTag = view.findViewById(R.id.timeTag);
        mPriceRangesTag = view.findViewById(R.id.priceTag);
        mVenuetag = view.findViewById(R.id.venueTag);

        JSONArray artists = selectedEvent.getArtistsDetails();
        ArrayList<String> artistNames = new ArrayList<>();
        for (int i = 0; i < artists.length(); i++) {
            JSONObject artistObj = null;
            try {
                artistObj = artists.getJSONObject(i);
            } catch (JSONException e) {
                Log.d("", e.toString());
            }
            String name = artistObj.optString("name","");
            artistNames.add(name);
        }
        String concatenatedNames = TextUtils.join(" | ", artistNames);
        mArtists.setText(concatenatedNames);

        if(TextUtils.isEmpty(selectedEvent.getVenue())){
            mVenuetag.setVisibility(View.GONE);
            mVenueName.setVisibility(View.GONE);
        }
        else{
            mVenueName.setText(selectedEvent.getVenue());
        }

        if(TextUtils.isEmpty(selectedEvent.getDate())){
            mDateTag.setVisibility(View.GONE);
            mDate.setVisibility(View.GONE);
        }
        else{
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            try {
                Date date = inputDateFormat.parse(selectedEvent.getDate());
                String outputDate = outputDateFormat.format(date);
                mDate.setText(outputDate);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }


        if(TextUtils.isEmpty(selectedEvent.getTime())){
            mTimeTag.setVisibility(View.GONE);
            mTime.setVisibility(View.GONE);
        }
        else{
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
                Date time = inputFormat.parse(selectedEvent.getTime());
                SimpleDateFormat outputFormat = new SimpleDateFormat("h:mm a", Locale.US);
                String outputTime = outputFormat.format(time);
                mTime.setText(outputTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        mGenres.setText(String.join(" | ", selectedEvent.getGenres()));

        if( selectedEvent.getPriceRanges() != null && selectedEvent.getPriceRanges().size() > 0){
            String concatanatedPR = String.join(" - ", selectedEvent.getPriceRanges());
            mPriceRanges.setText(concatanatedPR + " USD");
        }

        else{
            mPriceRangesTag.setVisibility(View.GONE);
            mPriceRanges.setVisibility(View.GONE);
        }


        String ts = selectedEvent.getTicketStatus();
        switch (ts) {
            case "onsale":
                mTicketStatus.setText("On Sale");
                mTicketStatus.setBackgroundResource(R.drawable.search_bg);
                break;
            case "offsale":
                mTicketStatus.setText("Off Sale");
                mTicketStatus.setBackgroundResource(R.drawable.red_bg);
                break;
            case "postponed":
                mTicketStatus.setText("Postponed");
                mTicketStatus.setBackgroundResource(R.drawable.orange_bg);
                break;
            case "cancelled":
                mTicketStatus.setText("Cancelled");
                mTicketStatus.setBackgroundResource(R.drawable.black_bg);
                break;
            case "rescheduled":
                mTicketStatus.setText("Rescheduled");
                mTicketStatus.setBackgroundResource(R.drawable.orange_bg);
                break;
            default:
                break;
        }


        mBuyAtURL.setText(selectedEvent.getEventURL());
        mBuyAtURL.setPaintFlags(mBuyAtURL.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        mBuyAtURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(selectedEvent.getEventURL()));
                startActivity(intent);
            }
        });
        Log.d("EventDetailsFragment", ""+selectedEvent.getSeatMap());
        Glide.with(mImageView.getContext())
                .load(selectedEvent.getSeatMap())
                .centerInside()
                .into(mImageView);
        pb.setVisibility(View.GONE);
        eventDetailsCard.setVisibility(View.VISIBLE);

    }

    private void handleTicketStatus(String ts){
        switch (ts) {
            case "onsale":
                mTicketStatus.setText("On Sale");
                mTicketStatus.setBackgroundResource(R.drawable.search_bg);
                break;
            case "offsale":
                mTicketStatus.setText("Off Sale");
                mTicketStatus.setBackgroundResource(R.drawable.red_bg);
                break;
            case "postponed":
                mTicketStatus.setText("Postponed");
                mTicketStatus.setBackgroundResource(R.drawable.orange_bg);
                break;
            case "cancelled":
                mTicketStatus.setText("Cancelled");
                mTicketStatus.setBackgroundResource(R.drawable.black_bg);
                break;
            case "rescheduled":
                mTicketStatus.setText("Rescheduled");
                mTicketStatus.setBackgroundResource(R.drawable.orange_bg);
                break;
            default:
                break;
        }

    }
}