package edu.ucsd.sgf.util;


/** A simple class for handling times throughout the festival day.
*/
public class Time implements Comparable<Time> {

    public final int hr;
    public final int min;


    public Time(int h, int m) {
        if(h < 0 || h > 24)
            throw new IllegalArgumentException(h + " is not a valid hour");
        if(m < 0 || m > 60)
            throw new IllegalArgumentException(m + " is not a valid minute");
        hr = h;
        min = m;
    }


    public int intValue() {
        return hr * 60 + min;
    }


    public int compareTo(Time that) {
        return this.intValue() - that.intValue();
    }


    public String toString() {
        return String.format("%02d:%02d", hr, min);
    }


    public String pretty() {
        char meridianum = 'a';
        int hour = (hr + 11) % 12 + 1;

        if(hr >= 12 && hr != 24)
            meridianum = 'p';

        return String.format("%d:%02d%c", hour, min, meridianum);
    }


    public static Time fromLineupEncoding(String s) {
        if(!s.matches("(2[0-3]|[01][0-9]):[0-5][0-9]"))
            return null;
        return new Time(Integer.parseInt(s.substring(0, 2)),
                Integer.parseInt(s.substring(3, 5)));
    }
}
