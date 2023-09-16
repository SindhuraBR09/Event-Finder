package com.eventsearch.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventDetailsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private Event selectedEvent;

    private JSONArray artists;
    private JSONArray artistDetails;

    private TextView eventName;

    private ImageButton fvtButton;

    private ImageView facebookIcon;

    private CircleImageView twitterIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        selectedEvent = getIntent().getParcelableExtra("selected_event");
        TextView backButton = findViewById(R.id.back_to_eventlist);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        eventName = findViewById(R.id.event_name_textview);
        eventName.setText(selectedEvent.getName());
        eventName.setSelected(true);

        Log.d("Favorites from Homescreen: ", HomeActivity.favoriteEvents.toString());

        fvtButton = findViewById(R.id.favorite_button);
        if (HomeActivity.favoriteEvents.containsKey(selectedEvent.getId())) {
            fvtButton.setImageResource(R.drawable.heart_filled);
        } else {
            fvtButton.setImageResource(R.drawable.heart_outline);
        }

        fvtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(HomeActivity.favoriteEvents.containsKey(selectedEvent.getId())){
                    HomeActivity.favoriteEvents.remove(selectedEvent.getId());
                    fvtButton.setImageResource(R.drawable.heart_outline);
                    HomeActivity.saveFavoriteEvents(view.getContext());
                    Log.d("Favorites after removing :", HomeActivity.favoriteEvents.toString());

                    Snackbar snackbar = Snackbar.make(view, selectedEvent.getName()+" removed from Favorites", 2000);
                    snackbar.getView()
                            .setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.snackbar_style));
                    View snackView = snackbar.getView();
                    TextView tv = (TextView) snackView.findViewById(com.google.android.material.R.id.snackbar_text);
                    tv.setTextColor(Color.BLACK);
                    snackbar.show();

                } else {
                    HomeActivity.favoriteEvents.put(selectedEvent.getId(), selectedEvent);
                    fvtButton.setImageResource(R.drawable.heart_filled);
                    HomeActivity.saveFavoriteEvents(view.getContext());
                    Log.d("Favorites after adding:", HomeActivity.favoriteEvents.toString());

                    Snackbar snackbar = Snackbar.make(view, selectedEvent.getName()+" added to Favorites", 2000);
                    snackbar.getView()
                            .setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.snackbar_style));
                    View snackView = snackbar.getView();
                    TextView tv = (TextView) snackView.findViewById(com.google.android.material.R.id.snackbar_text);
                    tv.setTextColor(Color.BLACK);
                    snackbar.show();
                }
            }
        });

        facebookIcon = findViewById(R.id.facebook_icon);
        facebookIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareURL = "https://www.facebook.com/sharer.php?u=" + Uri.encode(selectedEvent.getEventURL());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(shareURL));
                startActivity(intent);
            }
        });

        twitterIcon = findViewById(R.id.twitter_icon);
        twitterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tweetUrl = "https://twitter.com/intent/tweet?text=" +
                        Uri.encode("Check out " + selectedEvent.getName() + "\n") +
                        "&url=" +
                        Uri.encode(selectedEvent.getEventURL());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
                startActivity(intent);
            }
        });

        tabLayout = findViewById(R.id.tabLayout3);
        viewPager = findViewById(R.id.viewPager3);
        artistDetails = new JSONArray();
        if(selectedEvent.getGenres().contains("Music")){
            artists = selectedEvent.getArtistsDetails();
            try {
                for(int i=0; i< artists.length();i++){
                    JSONObject artistObj = artists.getJSONObject(i);
                    Log.d("ArtistFragment: ",artistObj.getString("name"));
                    String url = "https://reactapp-380904.uc.r.appspot.com/getArtistAlbums?name=" + artistObj.getString("name");
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Artist Details ", response.toString());
                            artistDetails.put(response);

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("VolleyError while fetching artists", "");
                        }
                    });
                    Volley.newRequestQueue(this).add(jsonObjectRequest);
                }
            }
            catch (JSONException e) {
                throw new RuntimeException(e);
            } ;
        }
        Log.d("Artists details from Spotify :", artistDetails.toString());
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                // Return the fragment for the corresponding tab.
                Bundle bundle = new Bundle();
                switch (position) {
                    case 0:
                        EventDetailsFragment eventDetailsFragment  = new EventDetailsFragment();
                        bundle.putParcelable("selected_event", selectedEvent);
                        eventDetailsFragment.setArguments(bundle);
                        return  eventDetailsFragment;
                    case 1:
                        Log.d("EvenDetailsActivity: Current Artists ",artistDetails.toString());
                        ArtistFragment artistsDetailsFragment  = new ArtistFragment();
                        bundle.putString("artist_details", artistDetails.toString());
                        artistsDetailsFragment.setArguments(bundle);
                        return artistsDetailsFragment;
                    case 2:
                        VenueFragment venueFragment = new VenueFragment();
                        bundle.putString("venue_details", selectedEvent.getVenueDetailsDetails().toString());
                        venueFragment.setArguments(bundle);
                        return venueFragment;
                    default:
                        return null;
                }
            }

            @Override
            public int getItemCount() {
                return 3;
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // Set the text for the tabs.
            View customView = LayoutInflater.from(viewPager.getContext()).inflate(R.layout.custom_tab, null);
            ImageView iconImageView = customView.findViewById(R.id.tab_icon);
            TextView textTextView = customView.findViewById(R.id.tab_text);

            switch (position) {
                case 0:
//                    iconImageView.setImageResource(R.drawable.info_icon);
//                    textTextView.setText("DETAILS");
                    tab.setIcon(R.drawable.info_icon);
                    tab.setText("DETAILS");
                    break;
                case 1:
//                    iconImageView.setImageResource(R.drawable.artist_icon);
//                    textTextView.setText("ARTIST(S)");
                    tab.setIcon(R.drawable.artist_icon);
                    tab.setText("ARTIST(S)");
                    break;
                case 2:
//                    iconImageView.setImageResource(R.drawable.venue_icon);
//                    textTextView.setText("VENUE");
                    tab.setIcon(R.drawable.venue_icon);
                    tab.setText("VENUE");
                    break;
            }
//            tab.setCustomView(customView);
        }).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("EventDetailsActivity", "onTabSelected: " + tab.getPosition());
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


}