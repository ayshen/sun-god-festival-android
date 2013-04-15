package edu.ucsd.sgf.util;


public class Performance implements Comparable<Performance> {

    public final String artist;
    public final String opener;
    public final Time begin;
    public final Time end;


    public Performance(String a, Time b, Time e) {
        artist = a;
        opener = null;
        begin = b;
        end = e;
    }


    public Performance(String a, String o, Time b, Time e) {
        artist = a;
        opener = o;
        begin = b;
        end = e;
    }


    public int compareTo(Performance that) {
        return this.begin.compareTo(that.begin);
    }
}
