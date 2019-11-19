package com.vovamiller_97.pioneer.db;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Note {

    private static final Random RANDOMIZER = new Random(System.currentTimeMillis());
    private static final int MAX_RAND = 1000000;

    private long id;
    private String title;
    private String text;
    private Date date;
    private String image;

    public Note() {
        id = -1;
        title = "TITLE";
        text = "TEXT";
        date = createDefaultDate();
        image = "";
    }

    public Note(final long id, @NonNull final String title, @NonNull final String text,
                final Date date, final String image) {
        this.id = id;
        this.title = title;
        this.text = text;
        setDate(date);
        setImage(image);
    }

    private Date createDefaultDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2011, 6, 12);
        return calendar.getTime();
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getImage() {
        return image;
    }

    public Date getDate() {
        return date;
    }

    public long getDateSerialized() { return date.getTime(); }

    public void setId(long id) { this.id = id; }

    public void setTitle(@NonNull String title) { this.title = title; }

    public void setText(@NonNull String text) { this.text = text; }

    public void setImage(String image) {
        if (image != null) {
            this.image = image;
        } else {
            this.image = "";
        }
    }

    public void setDate(Date date) {
        if (date == null) {
            this.date = createDefaultDate();
        } else {
            this.date = date;
        }
    }

    public void setDate(long dateSerialized) {
        date = new Date(dateSerialized);
    }

    public void generateTitle() {
        int r = RANDOMIZER.nextInt(MAX_RAND);
        setTitle("Note " + r);
    }
}
