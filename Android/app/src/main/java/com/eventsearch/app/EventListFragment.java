package com.eventsearch.app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventListFragment extends Fragment implements Eventlist_Adapter.EventListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String keyword;
    String location;
    String distance;
    String category;

    Boolean autodetect;
    private List<Event> mEvents;

    RecyclerView recyclerView;

    ProgressBar progressBar;

    TextView no_results;
    public EventListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventListFragment newInstance(String param1, String param2) {
        EventListFragment fragment = new EventListFragment();
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

        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        TextView backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> getActivity().finish());

        no_results = view.findViewById(R.id.no_results_textview);
        no_results.setVisibility(View.GONE);

        progressBar = view.findViewById(R.id.progress_bar);
        // Set its visibility to VISIBLE
        progressBar.setVisibility(View.VISIBLE);

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            keyword = getArguments().getString("keyword");
            location = getArguments().getString("location");
            distance = getArguments().getString("distance");
            category = getArguments().getString("category");
            autodetect = getArguments().getBoolean("autodetect");
        }
        // Initialize the RecyclerView and adapter
        recyclerView = view.findViewById(R.id.eventlist_fragment);

        if(autodetect){
            getCoOrdinates();
        }
        else{
            geocodeAddress(location);
        }



    }

    private void geocodeAddress(String location) {
        String myAPIKey = "AIzaSyBpZvMEg8E7OCm6Umc8FX80tXwiCFNCJ2k";
        String geocodingUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(location) + "&key=" + myAPIKey;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, geocodingUrl, null,
                response -> {
                    try {
                        if(response.getString("status").equals("OK")){
                            JSONObject locationObj = response.getJSONArray("results")
                                    .getJSONObject(0)
                                    .getJSONObject("geometry")
                                    .getJSONObject("location");
                            double latitude = locationObj.getDouble("lat");
                            double longitude = locationObj.getDouble("lng");
                            Log.d("geocodeAddress", "Location " + latitude + " "+longitude);
                            getEvents(latitude, longitude);
                        }
                        else{
                            Log.d("geocodeAddress", "Error in geocodeAddress");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("geocodeAddress", "Volley error"));

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }

    private void getCoOrdinates(){
        // Instantiate the RequestQueue.
        String url ="https://ipinfo.io/json?token=2767c4a5860528";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d("Response from IPInfor :" , response.toString());
                        String loc = response.getString("loc");
                        String[] coords = loc.split(",");
                        String lat = coords[0];
                        String lng = coords[1];
                        Log.d("Latitude", lat);
                        Log.d("Longitude", lng);
                        getEvents(Double.parseDouble(lat), Double.parseDouble(lng));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("getCoOrdinates", "Volley error"));

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);

    }

    private void getEvents(double latitude, double longitude) {

        category = category.toLowerCase();
        if(category.equals("all")){
            category = "default";
        }
        Log.d("EventListFragment", "Keyword: " + keyword);
        Log.d("EventListFragment", "Location: " + location);
        Log.d("EventListFragment", "Category: " + category);
        Log.d("EventListFragment", "Distance: " + distance);

        String searchUrl = "https://reactapp-380904.uc.r.appspot.com/searchForEvents/?key=" + keyword +
                "&dist=" + distance +
                "&genre=" + category +
                "&lat=" + latitude +
                "&lng=" + longitude +
                "&loc=" + location;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, searchUrl, null,
                response -> {
                    try {
                        Log.d("getEvents", "Response "+response);
                        if (response.has("_embedded") && response.getJSONObject("_embedded").has("events")) {
                            JSONArray eventsArray = response.getJSONObject("_embedded").getJSONArray("events");
                            displayEvents(eventsArray);
                        } else {
                            Log.d("getEvents", "No results");
                            no_results.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("getEvents", "Volley error"));

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }



    private void displayEvents(JSONArray events){

        try {
                mEvents = new ArrayList<>();
                for (int i = 0; i < events.length(); i++) {
                    JSONObject eventObject = events.getJSONObject(i);
                    Event event = new Event();
                    event.setId(eventObject.getString("id"));
                    event.setName(eventObject.getString("name"));
                    event.setImageUrl(eventObject.optString("url", ""));
                    event.setEventURL(eventObject.optString("url", ""));

                    try {
                        JSONArray classifications = eventObject.getJSONArray("classifications");
                        if (classifications != null && classifications.length() > 0) {
                            JSONObject classification = classifications.getJSONObject(0);
                            if (classification != null) {

                                JSONObject segmentObject = classification.getJSONObject("segment");
                                if (segmentObject != null) {
                                    String segmentName = segmentObject.getString("name");
                                    if (segmentName != null) {
                                        event.addGenre(segmentName);
                                    }
                                }

                                JSONObject genreObject = classification.getJSONObject("genre");
                                if (genreObject != null) {
                                    String genreName = genreObject.getString("name");
                                    if (genreName != null) {
                                        event.addGenre(genreName);
                                    }
                                }

                                JSONObject subGenreObject = classification.getJSONObject("subGenre");
                                if (subGenreObject != null) {
                                    String subGenreName = subGenreObject.getString("name");
                                    if (subGenreName != null) {
                                        event.addGenre(subGenreName);
                                    }
                                }
                                JSONObject typeObject = classification.getJSONObject("type");
                                if (typeObject != null) {
                                    String typeName = typeObject.getString("name");
                                    if (typeName != null) {
                                        event.addGenre(typeName);
                                    }
                                }
                                JSONObject subTypeObject = classification.getJSONObject("subType");
                                if (subTypeObject != null) {
                                    String subtypeName = subTypeObject.getString("name");
                                    if (subtypeName != null) {
                                        event.addGenre(subtypeName);
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        Log.d("GetEvents: ", e.toString());
                        // Handle the case where the keys are not present in the JSON object.
                    }

                    JSONArray imagesArray = eventObject.optJSONArray("images");
                    if (imagesArray != null && imagesArray.length() > 0) {
                        String imageUrl = imagesArray.getJSONObject(0).optString("url", "");
                        event.setImageUrl(imageUrl);
                    }

                    // For setting event date
                    JSONObject datesObject = eventObject.optJSONObject("dates");
                    if (datesObject != null) {
                        JSONObject startObject = datesObject.optJSONObject("start");
                        if (startObject != null) {
                            String date = startObject.optString("localDate", "");
                            event.setDate(date);
                            String time = startObject.optString("localTime", "");
                            event.setTime(time);
                        }
                        JSONObject status = datesObject.optJSONObject("status");
                        if (status != null) {
                            String ts = status.optString("code", "");
                            event.setTicketStatus(ts);
                        }
                    }
                    // For setting event venue
                    JSONObject embeddedObject = eventObject.optJSONObject("_embedded");
                    if (embeddedObject != null) {
                        JSONArray venuesArray = embeddedObject.optJSONArray("venues");
                        JSONArray artistArray = embeddedObject.optJSONArray("attractions");
                        if (venuesArray != null && venuesArray.length() > 0) {
                            event.setVenueDetails(venuesArray);
                            String venue = venuesArray.getJSONObject(0).optString("name", "");
                            event.setVenue(venue);
                        }

                        if(artistArray != null && artistArray.length() > 0){
                            event.setArtistsDetails(artistArray);
                        }
                    }

                    JSONArray prObj = eventObject.optJSONArray("priceRanges");
                    if(prObj != null && prObj.length() > 0){
                        JSONObject priceRanges = prObj.getJSONObject(0);
                        String minValue = priceRanges.optString("min");
                        String maxValue = priceRanges.optString("max");
                        ArrayList<String> pr = new ArrayList<String>();
                        pr.add(minValue);
                        pr.add(maxValue);
                        event.setPriceRanges(pr);
                    }

                    JSONObject seatMapObj = eventObject.optJSONObject("seatmap");
                    if(seatMapObj != null){
                        String seatmap = seatMapObj.optString("staticUrl");
                        Log.d("Setting setmap objected ", seatmap);
                        event.setSeatMap(seatmap);
                    }

                    mEvents.add(event);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        // Define a custom Comparator for Event objects
        Comparator<Event> eventComparator = new Comparator<Event>() {
            @Override
            public int compare(Event event1, Event event2) {
                // First compare the dates of the events
                int dateComparison = event1.getDate().compareTo(event2.getDate());
                if (dateComparison != 0) {
                    return dateComparison;
                }

                // If the events have the same date, compare their times
                String time1 = event1.getTime();
                String time2 = event2.getTime();

                // If one event does not have a time, it should appear first
                if (time1 == null && time2 == null) {
                    return 0;
                } else if (time1 == null) {
                    return -1;
                } else if (time2 == null) {
                    return 1;
                } else {
                    return time1.compareTo(time2);
                }
            }
        };

        Collections.sort(mEvents, eventComparator);
            // Initialize the RecyclerView and adapter
            Eventlist_Adapter adapter = new Eventlist_Adapter(mEvents, false, this);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            progressBar.setVisibility(View.GONE);
            Log.d("DisplayEvents", "Recycle  view displayed");

    }

    @Override
    public void onResume() {
        super.onResume();
        if(mEvents != null && mEvents.size() > 0){
            Eventlist_Adapter adapter = new Eventlist_Adapter(mEvents, false, this);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            progressBar.setVisibility(View.GONE);
        }

    }

    public void OnFavoritesRemoved(){

    }


}