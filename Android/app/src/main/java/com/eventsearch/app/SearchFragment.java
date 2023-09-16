package com.eventsearch.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.eventsearch.app.Event;
import com.google.android.material.snackbar.Snackbar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText mKeywordEditText;
    private EditText mDistanceEditText;
    private Spinner mCategorySpinner;
    private EditText mLocationEditText;
    private SwitchCompat switchCompat;
    private AutoCompleteTextView mAutocomplete;
    private Button submitButton;
    private Button clearButton;

    private ProgressBar autocomplete_progress;

    public SearchFragment() {
        // Required empty public constructor
    }
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        String[] suggestions = { "Paries,France", "PA,United States","Parana,Brazil",
                "Padua,Italy", "Pasadena,CA,United States"};

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mKeywordEditText = (EditText) view.findViewById(R.id.keyword_edit_text);
        mDistanceEditText =  (EditText)view.findViewById(R.id.distance_edit_text);
        mLocationEditText = (EditText) view.findViewById(R.id.location_edit_text);
        mCategorySpinner = (Spinner) view.findViewById(R.id.category_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.category_values, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mCategorySpinner.setAdapter(adapter);
        mCategorySpinner.setOnItemSelectedListener(this);
        switchCompat = view.findViewById(R.id.simpleSwitch);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mLocationEditText.setVisibility(View.GONE);
                } else {
                    mLocationEditText.setVisibility(View.VISIBLE);
                }
            }
        });

        autocomplete_progress = view.findViewById(R.id.autocomplete_progress);
        mAutocomplete = view.findViewById(R.id.keyword_edit_text);
        ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<String>(getActivity(), R.layout.suggestion_row, suggestions);
        mAutocomplete.setThreshold(1);
        mAutocomplete.setAdapter(autoCompleteAdapter);

        // Set up the TextWatcher
        mAutocomplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Log.d("GetSuggestions ","");
                // Make API call to get suggestions
                autocomplete_progress.setVisibility(View.VISIBLE);
                String textEntered = s.toString();
                String suggestionUrl = "https://reactapp-380904.uc.r.appspot.com/getSuggestions?keyword=" + textEntered;

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, suggestionUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Update adapter with new suggestions
                        try {
                            List<String> suggestionsList = new ArrayList<String>();
                            if(response.has("_embedded") && response.getJSONObject("_embedded").has("attractions")){
                                JSONArray suggestionsArray = response.getJSONObject("_embedded").getJSONArray("attractions");
                                for (int i = 0; i < suggestionsArray.length(); i++) {
                                    suggestionsList.add(suggestionsArray.getJSONObject(i).getString("name"));
                                }
                            }
                            autoCompleteAdapter.clear();
                            autocomplete_progress.setVisibility(View.GONE);
                            autoCompleteAdapter.addAll(suggestionsList);

                            Log.d("Suggestions from API ", suggestionsList.toString());
                        } catch (JSONException e) {
                            Log.e("Error parsing JSON response", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error getting suggestions from API", error.toString());
                    }
                });

                // Add request to Volley queue
                Volley.newRequestQueue(getActivity()).add(jsonObjectRequest);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }


        });


        submitButton = view.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = mKeywordEditText.getText().toString();
                String location = mLocationEditText.getText().toString();
                String category = mCategorySpinner.getSelectedItem().toString();
                String distance = mDistanceEditText.getText().toString();
                Boolean autodetect = switchCompat.isChecked();

                Log.d("SearchFragment", "Keyword: " + keyword);
                Log.d("SearchFragment", "Location: " + location);
                Log.d("SearchFragment", "Category: " + category);
                Log.d("SearchFragment", "Distance: " + distance);
                Log.d("SearchFragment", "AutoDetect: " + autodetect);

                if (TextUtils.isEmpty(keyword) || (!autodetect && TextUtils.isEmpty(location))) {
                    Snackbar snackbar = Snackbar.make(view, "Please fill all fields", 2000);
                    snackbar.getView()
                            .setBackground(ContextCompat.getDrawable(getContext(),R.drawable.snackbar_style));
                    View view = snackbar.getView();
                    TextView tv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
                    tv.setTextColor(Color.BLACK);
                    snackbar.show();
                } else {
                    Intent intent = new Intent(getActivity(), EventListActivity.class);
                    intent.putExtra("keyword", keyword);
                    intent.putExtra("location", location);
                    intent.putExtra("category", category);
                    intent.putExtra("distance", distance);
                    intent.putExtra("autodetect", autodetect);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });

        clearButton = view.findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mKeywordEditText.setText("");
                mKeywordEditText.setHint("Enter the Keyword");
                mLocationEditText.setVisibility(View.VISIBLE);
                mLocationEditText.setText("");
                mLocationEditText.setHint("Enter the Location");
                switchCompat.setChecked(false);
                mDistanceEditText.setText("10");
                mCategorySpinner.setSelection(0);
            }
        });


        return view;

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = adapterView.getItemAtPosition (i).toString();
        ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.white));
        Log.d("SearchFragment", "onCategorySelected: " + text);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }



}

