package com.esraa.android.plannertracker.TripDetails;

public class NoteClass {
    private String id;
    private String myNotes ;
    public NoteClass()
    {
    }

    public NoteClass(String id, String myNotes)
    {
        this.id = id;
        this.myNotes = myNotes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getMyNotes() {
        return myNotes;
    }

    public void setMyNotes(String myNotes) {
        this.myNotes = myNotes;
    }

}
