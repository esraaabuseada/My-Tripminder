package com.esraa.android.plannertracker.TripDetails;

public class HistoryList {
private String tripName ;
private String startPlace ;
private String endPlace ;
private  String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HistoryList() {
    }

    public HistoryList(String tripName, String startPlace, String endPlace , String id)
    {
        this.tripName = tripName;
        this.startPlace = startPlace;
        this.endPlace = endPlace;
        this.id = id ;
    }



    public String getTripName() {
        return tripName;
    }

    public String getStartPlace() {
        return startPlace;
    }

    public String getEndPlace() {
        return endPlace;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public void setStartPlace(String startPlace) {
        this.startPlace = startPlace;
    }

    public void setEndPlace(String endPlace) {
        this.endPlace = endPlace;
    }


}
