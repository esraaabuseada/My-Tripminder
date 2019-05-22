package com.esraa.android.plannertracker.TripDetails;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.esraa.android.plannertracker.R;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mapbox.geocoder.MapboxGeocoder;
import com.mapbox.geocoder.service.models.GeocoderResponse;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.ViewHolder> {


    private LayoutInflater mInflater;
    private ArrayList<HistoryList> historyLists;
    private HistoryList historyList;
    DatabaseReference databaseReference;
    FirebaseUser fu;
    private Context context;
    StaticMap staticMap ;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String uid;
    View view;

    int flag = 0 ;
    private  double lat1, lat2;
    private  double lon1, lon2;

    private static final String token = "sk.eyJ1IjoibWlsa3lyYW5nZXIiLCJhIjoiY2pzOTBzOXlxMTZ6ZDN6czhiNTJjY2JrdCJ9.TVE3NN-juPXRMYr14hRBFA";


    HistoryRecyclerAdapter(Context context, ArrayList<HistoryList> historyLists) {
        this.mInflater = LayoutInflater.from(context);
        this.historyLists = historyLists;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         view = mInflater.inflate(R.layout.history_layout_single_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        historyList = historyLists.get(position);

        holder.trip.setText(historyList.getTripName());
        holder.startPoint.setText(historyList.getStartPlace());
        holder.endPoint.setText(historyList.getEndPlace());
        Log.v("BOOM",position+"");

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                ////////////////////////////DELETE HERE
                int positionOfItem = holder.getAdapterPosition();
                showDialogForHistory(positionOfItem);


            }
        });



        holder.mapImage.setOnClickListener(new View.OnClickListener() { // try mapImage lw feh moshkla later
            @Override
            public void onClick(View v) {
                int positionOfItem = holder.getAdapterPosition();
                staticMap = new StaticMap();
                getPoints(positionOfItem);



                Log.v("BOOM",historyList.getStartPlace());
                Log.v("BOOM",historyList.getEndPlace());

             //   toStaticMapIntent.putExtra("start",historyList.getStartPlace());
              //  toStaticMapIntent.putExtra("end",historyList.getEndPlace());
            }
        });


    }
    public void getPoints(final int adapterPosition) {


        historyList = historyLists.get(adapterPosition);
        MapboxGeocoder client1 = new MapboxGeocoder.Builder()
                .setAccessToken(token)
                .setLocation(historyList.getStartPlace())
                .setProximity(lat1, lon1)
                .build();
        Log.v("ramadan1", lat1 + "  " + lon1);
        MapboxGeocoder client2 = new MapboxGeocoder.Builder()
                .setAccessToken(token)
                .setLocation(historyList.getEndPlace())
                .setProximity(lat2, lon2)
                .build();
        Log.v("ramadan2", lat2 + "  " + lon2);
        client1.enqueue(new Callback<GeocoderResponse>() {
            @Override
            public void onResponse(Response<GeocoderResponse> response, Retrofit retrofit) {

                lat1 = response.body().getFeatures().get(0).getLatitude();
                lon1 = response.body().getFeatures().get(0).getLongitude();
                staticMap.setLat1(lat1);
                staticMap.setLon1(lon1);
                flag++;

                goToMap();

                Log.v("omar1", lat1 + " " + lon1);


            }

            @Override
            public void onFailure(Throwable t) {

            }


        });

        client2.enqueue(new Callback<GeocoderResponse>() {
            @Override
            public void onResponse(Response<GeocoderResponse> response, Retrofit retrofit) {

                lat2 = response.body().getFeatures().get(0).getLatitude();
                lon2 = response.body().getFeatures().get(0).getLongitude();

               staticMap.setLat2(lat2);
               staticMap.setLon2(lon2);
               flag++;
               goToMap();
                Log.v("omar2", lat2 + " " + lon2);
                Log.v("omar3",staticMap.getLat2()+" " +staticMap.getLon2());
            }//////////////////////////////////sh8al tmam hna

            @Override
            public void onFailure(Throwable t) {

            }
        });


    }
    public void goToMap()
    {
        Intent toStaticMapIntent = new Intent(context, StaticMap.class);
        if(flag==2)
        {
            toStaticMapIntent.putExtra("lat1",lat1);
            toStaticMapIntent.putExtra("lon1",lon1);
            toStaticMapIntent.putExtra("lat2",lat2);
            toStaticMapIntent.putExtra("lon2",lon2);
            toStaticMapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(toStaticMapIntent);

        }



    }

    private void showDialogForHistory(final int adapterpostion) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                removeItem(adapterpostion);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(context, "cancel", Toast.LENGTH_SHORT).show();
             //   HistoryRecyclerAdapter.ViewHolder obj = new HistoryRecyclerAdapter(context,historyLists).new ViewHolder(view);
            //    obj.checkBox.setChecked(false);

            }
        }).setTitle("Remove item").setMessage("Remove item from history ?").create().show();
    }

    private void removeItem(int adapterPosition) {
        String id = historyLists.get(adapterPosition).getId();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        mAuth = FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        uid=mUser.getUid();

        databaseReference.child(uid).child("Trip History").child(id).removeValue();
        Log.i("zzzz", "removeItem: " + id);
        // databaseReference.child(id).removeValue();
        historyLists.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        notifyItemRangeChanged(adapterPosition, historyLists.size());
        Toast.makeText(context, "Removed Successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return historyLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView trip;
        TextView startPoint;
        TextView endPoint;
    public    CheckBox checkBox;

        ImageView mapImage;
        public ViewHolder(View itemView) {
            super(itemView);

            trip = itemView.findViewById(R.id.tripHome);
            mapImage = itemView.findViewById(R.id.mapIconID);
            startPoint = itemView.findViewById(R.id.source);
            endPoint = itemView.findViewById(R.id.destination);
            checkBox = itemView.findViewById(R.id.checkFinished);

        }
    }


}
