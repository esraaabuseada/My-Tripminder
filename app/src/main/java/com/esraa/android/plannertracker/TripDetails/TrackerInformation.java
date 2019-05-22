package com.esraa.android.plannertracker.TripDetails;

public class TrackerInformation {
    public TripTypeView getTripTypeView() {
        return tripTypeView;
    }

    public void setTripTypeView(TripTypeView tripTypeView) {
        this.tripTypeView = tripTypeView;
    }

    public int getAlarmID() {
        return AlarmID;
    }

    public void setAlarmID(int alarmID) {
        AlarmID = alarmID;
    }

    public enum TripTypeView {
        SINGLE_TRIP,ROUND_TRIP ;
    }
    private TripTypeView tripTypeView;
    private String id;
    private String StartPosition;
    private String destination;
    private String TripName;
    private String time;
    private String date;
    private String tripType;
    private NoteClass notes;
    private int AlarmID;
    //private Map<String,NoteClass> notes;


    public TrackerInformation() {}


    public TrackerInformation(String id, String startPosition, String destination,
                              String tripName, String time, String date,
                              String tripType) {
        this.id = id;
        this.StartPosition = startPosition;
        this.destination = destination;
        TripName = tripName;
        this.time = time;
        this.date = date;
        this.tripType = tripType;
    }


    public TrackerInformation(String id, String startPosition, String destination,
                              String tripName, String time, String date , int AlarmID) {
        this.id = id;
        this.StartPosition = startPosition;
        this.destination = destination;
        TripName = tripName;
        this.time = time;
        this.date = date;
        this.AlarmID = AlarmID;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setStartPosition(String startPosition) {
        StartPosition = startPosition;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getStartPosition() {
        return StartPosition;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getTripName() {
        return TripName;
    }

    public void setTripName(String tripName) {
        TripName = tripName;
    }

    public String getTripType() {
        return tripType;
    }

    public NoteClass getTripNotes() {
        return notes;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }
    public void setTripNotes(NoteClass tripNotes) {
        this.notes= tripNotes;
    }
}
