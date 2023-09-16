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
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritesFragment extends Fragment implements  Eventlist_Adapter.EventListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private List<Event> favoriteEventList;

    RecyclerView recyclerView;
    TextView no_results;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoritesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoritesFragment newInstance(String param1, String param2) {
        FavoritesFragment fragment = new FavoritesFragment();
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
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favoriteEventList = new ArrayList<>(HomeActivity.favoriteEvents.values());
        Log.d("Current Favorites : ", favoriteEventList.toString());
        recyclerView = view.findViewById(R.id.fvt_list);
        no_results = view.findViewById(R.id.no_fvts);
        no_results.setVisibility(View.GONE);

        if(favoriteEventList.size()>0){
            Eventlist_Adapter adapter = new Eventlist_Adapter(favoriteEventList, true, this);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            Log.d("Favites: ", "Recycle  view displayed");
        }
        else{
            recyclerView.setVisibility(View.GONE);
            no_results.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {

        super.onResume();
        favoriteEventList = new ArrayList<>(HomeActivity.favoriteEvents.values());
        Log.d("Current Favorites : ", favoriteEventList.toString());

        if(recyclerView != null && no_results != null){
            if(favoriteEventList.size()>0){
                Eventlist_Adapter adapter = new Eventlist_Adapter(favoriteEventList, true, this);
                recyclerView.setVisibility(View.VISIBLE);
                no_results.setVisibility(View.GONE);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                Log.d("onResume(): ", "Recycle  view displayed");
            }
            else{
                recyclerView.setVisibility(View.GONE);
                no_results.setVisibility(View.VISIBLE);
            }
        }


    }

    private void updateFavoritesView() {
        favoriteEventList = new ArrayList<>(HomeActivity.favoriteEvents.values());
        if(favoriteEventList.size() > 0) {
            Eventlist_Adapter adapter = new Eventlist_Adapter(favoriteEventList, true, this);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            no_results.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            no_results.setVisibility(View.VISIBLE);
        }
    }

    public  void OnFavoritesRemoved(){
        updateFavoritesView();
    }

}