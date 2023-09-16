package com.eventsearch.app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VenueFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class


VenueFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private JSONArray venueDetails;

    private TextView venueName;

    private TextView venueAddress;

    private TextView venueCity;

    private TextView venueContact;

    private TextView openHours;

    private TextView generalRule;

    private TextView childRule;

    private TextView OpenHoursLabel;

    private TextView ChildRUleLabel;

    private TextView GeneralRuleLabel;

    private TextView ContactTag;
    private GoogleMap mMap;

    private LinearLayout generalInfoLayout;

    public VenueFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VenueFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VenueFragment newInstance(String param1, String param2) {
        VenueFragment fragment = new VenueFragment();
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
        return inflater.inflate(R.layout.fragment_venue, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if (getArguments() != null) {
            String venueArray = getArguments().getString("venue_details");
            try {
                venueDetails=new JSONArray(venueArray);
            } catch (JSONException e) {
                Log.e("Error while creating venue details :",e.getMessage());
            }
        }

        venueName = view.findViewById(R.id.venueNameValue);
        venueName.setSelected(true);
        venueAddress = view.findViewById(R.id.venueAddressValue);
        venueAddress.setSelected(true);
        venueCity = view.findViewById(R.id.venueCityValue);
        venueCity.setSelected(true);
        venueContact = view.findViewById(R.id.venueContactValue);
        venueContact.setSelected(true);
        ContactTag = view.findViewById(R.id.venueContactTag);
        openHours = view.findViewById(R.id.OpenHours);
        childRule = view.findViewById(R.id.ChildRule);
        generalRule = view.findViewById(R.id.GeneralRule);

        OpenHoursLabel = view.findViewById(R.id.OH);
        GeneralRuleLabel = view.findViewById(R.id.GR);
        ChildRUleLabel = view.findViewById(R.id.CR);

        generalInfoLayout = view.findViewById(R.id.general_info_layout);
        openHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence fullText = openHours.getTag().toString();
                openHours.setText(fullText);
                openHours.setOnClickListener(null);
            }
        });

        generalRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence fullText = generalRule.getTag().toString();
                generalRule.setText(fullText);
                generalRule.setOnClickListener(null);
            }
        });
        childRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence fullText = childRule.getTag().toString();
                childRule.setText(fullText);
                childRule.setOnClickListener(null);
            }
        });

        SupportMapFragment mapFragment =(SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.mapView);

        if(venueDetails != null && venueDetails.length()>0){
            try {
                String name = venueDetails.getJSONObject(0).optString("name");
                venueName.setText(name);
                JSONObject addressObj = venueDetails.getJSONObject(0).optJSONObject("address");
                if(addressObj != null){
                    String address = addressObj.optString("line1");
                    venueAddress.setText(address);
                }
                String city_state = "";
                JSONObject cityObj = venueDetails.getJSONObject(0).optJSONObject("city");
                if(cityObj != null){
                    city_state = cityObj.optString("name");
                }
                JSONObject stateObj = venueDetails.getJSONObject(0).optJSONObject("state");
                if(stateObj != null){
                    city_state = city_state + ","+ stateObj.optString("name");
                }
                venueCity.setText(city_state);
                JSONObject boxOfficeInfo = venueDetails.getJSONObject(0).optJSONObject("boxOfficeInfo");
                if(boxOfficeInfo != null){
                    String contact = boxOfficeInfo.optString("phoneNumberDetail");
                    if(TextUtils.isEmpty(contact)){
                        ContactTag.setVisibility(View.GONE);
                    }
                    else{
                        venueContact.setText(contact);
                    }
                    String oh = boxOfficeInfo.optString("openHoursDetail");
                    if(TextUtils.isEmpty(oh)){
                        OpenHoursLabel.setVisibility(View.GONE);
                    }
                    else{
                        String openHourShort = oh.substring(0, Math.min(oh.length(), 50)) + "...";
                        openHours.setText(openHourShort);
                        openHours.setTag(oh);
                    }
                }
                else{
                    ContactTag.setVisibility(View.GONE);
                    OpenHoursLabel.setVisibility(View.GONE);
                }


                JSONObject generalruleObj = venueDetails.getJSONObject(0).optJSONObject("generalInfo");
                if(generalruleObj != null){
                    String gr = generalruleObj.optString("generalRule");
                    if(TextUtils.isEmpty(gr)){
                        GeneralRuleLabel.setVisibility(View.GONE);
                    }
                    else{
                        String generalRuleShort = gr.substring(0, Math.min(gr.length(), 50)) + "...";
                        generalRule.setText(generalRuleShort);
                        generalRule.setTag(gr);
                    }

                    String cr = generalruleObj.optString("childRule");
                    if(TextUtils.isEmpty(cr)){
                        ChildRUleLabel.setVisibility(View.GONE);
                    }
                    else{
                        String childRuleShort = cr.substring(0, Math.min(cr.length(), 50)) + "...";
                        childRule.setText(childRuleShort);
                        childRule.setTag(cr);
                    }
                }
                else{
                    ChildRUleLabel.setVisibility(View.GONE);
                    GeneralRuleLabel.setVisibility(View.GONE);
                    if(OpenHoursLabel.getVisibility() == View.GONE){
                        generalInfoLayout.setVisibility(View.GONE);
                    }
                }

            } catch (JSONException e) {
                Log.e("venueFrgament: ", e.getMessage());
            }
        }

        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    double mLatitude = 0.0, mLongitude = 0.0;
                    try {

                        JSONObject locationObject = venueDetails.getJSONObject(0).optJSONObject("location");
                        if(locationObject != null){
                            mLatitude=Double.parseDouble(locationObject.optString("latitude"));
                            mLongitude=Double.parseDouble(locationObject.optString("longitude"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // Create LatLng object after parsing JSON object
                    LatLng location = new LatLng(mLatitude, mLongitude);
                    mMap.addMarker(new MarkerOptions().position(location).title("Location"));
                    // Move camera to the obtained location
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
                }
            });
        }

    }
}