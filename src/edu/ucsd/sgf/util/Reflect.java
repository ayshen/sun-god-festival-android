package edu.ucsd.sgf.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import android.content.res.Resources;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import edu.ucsd.sgf.R;


public class Reflect {

    /** Parse a single lineup XML file into an array of Performances.
    @param res {@link android.content.res.Resources} object supplied by the
    caller to
    {@link #loadLineupsFromStringArray(android.content.res.Resources, int)}.
    Used to resolve strings from <code>R</code> that will define how this
    method expects the XML file to be structured.
    @param parser {@link org.xmlpull.v1.XmlPullParser} object that is ready
    to stream XML parse events for the lineup XML.
    @return An array of {@link edu.ucsd.sgf.util.Performance}s, or
    <code>null</code> if an error occurred.
    */
    public static Performance[] loadLineupFromXml(Resources res,
            XmlPullParser parser) {

        String tag = "edu.ucsd.sgf.util.Reflect::loadLineupFromXml";
        int event = XmlPullParser.END_DOCUMENT;
        ArrayList<Performance> lineup = new ArrayList<Performance>();
        Performance[] result = null;

        String PERFORMANCE_TAG = null,
                ARTIST_ATTR = null,
                OPENER_ATTR = null,
                BEGIN_ATTR = null,
                END_ATTR = null;

        // Load the strings that define how we will be parsing the XML.
        try {
            PERFORMANCE_TAG = res.getString(
                    R.string.lineup_xml_tag_performance);
            ARTIST_ATTR = res.getString(
                    R.string.lineup_xml_attr_artist);
            OPENER_ATTR = res.getString(
                    R.string.lineup_xml_attr_opener);
            BEGIN_ATTR = res.getString(
                    R.string.lineup_xml_attr_begin);
            END_ATTR = res.getString(
                    R.string.lineup_xml_attr_end);
        }
        catch(Resources.NotFoundException rnfe) {
            Log.e(tag, "can't resolve an XML parser item: " + rnfe.toString());
            return null;
        }

        // Get the first parser event.
        try { event = parser.getEventType(); }
        catch(XmlPullParserException xppe) {
            Log.e(tag, xppe.toString());
            return null;
        }

        while(event != XmlPullParser.END_DOCUMENT) {

            // Read the next performance tag.
            if(event == XmlPullParser.START_TAG &&
                    parser.getName().equals(PERFORMANCE_TAG)) {

                String artist = null;
                String opener = null;
                Time begin = null;
                Time end = null;

                // Collect the attributes of the performance tag.
                for(int i = 0; i < parser.getAttributeCount(); ++i) {
                    if(parser.getAttributeName(i).equals(ARTIST_ATTR))
                        artist = parser.getAttributeValue(i);
                    else if(parser.getAttributeName(i).equals(OPENER_ATTR))
                        opener = parser.getAttributeValue(i);
                    else if(parser.getAttributeName(i).equals(BEGIN_ATTR))
                        begin = Time.fromLineupEncoding(
                                parser.getAttributeValue(i));
                    else if(parser.getAttributeName(i).equals(END_ATTR))
                        end = Time.fromLineupEncoding(
                                parser.getAttributeValue(i));
                }

                // If the required attributes have been collected, go ahead
                // and make a Performance object for it.
                if(artist != null && begin != null && end != null)
                    lineup.add(new Performance(artist, opener, begin, end));
            }

            // Get the next parser event.
            try { event = parser.next(); }
            catch(XmlPullParserException xppe) {
                Log.e(tag, xppe.toString());
                return null;
            }
            catch(IOException ioe) {
                Log.e(tag, ioe.toString());
                return null;
            }
        }

        // Convert the variable-size storage into the return type.
        if(lineup.size() == 0) return null;
        result = new Performance[lineup.size()];
        return lineup.toArray(result);
    }


    /**
    */
    public static Performance[] loadLineupFromXmlFile(Resources res,
            String filename) {

        String tag = "edu.ucsd.sgf.util.Reflect::loadLineupFromXmlFile";
        Field f = null;
        Integer id = null;

        // Resolve the filename into a field of R.xml, which represents the
        // resource ID for the XML file.
        try { f = R.xml.class.getField(filename); }
        catch(NoSuchFieldException nsfe) {
            Log.e(tag, "no field R.xml." + filename);
            return null;
        }
        catch(NullPointerException npe) {
            Log.e(tag, "can't resolve R.xml.<null>");
            return null;
        }
        catch(SecurityException se) {
            Log.e(tag, "not allowed to reflect on R.xml");
            return null;
        }

        // Resolve the field of R.xml into the XML resource ID.
        try { id = f.getInt(null); }
        catch(IllegalAccessException iaxe) {
            Log.e(tag, "R.xml." + filename + " is not accessible");
            return null;
        }
        catch(IllegalArgumentException iae) {
            Log.e(tag, iae.toString());
            return null;
        }
        catch(NullPointerException npe) {
            Log.wtf(tag, "needed an instance of R.xml");
            return null;
        }
        catch(ExceptionInInitializerError eiie) {
            Log.wtf(tag, "tried to instantiate R.xml");
            return null;
        }

        // Resolve the XML resource ID and parse the XML file.
        return loadLineupFromXml(res, res.getXml(id.intValue()));
    }


    /**
    */
    public static Performance[][] loadLineupsFromStringArray(Resources res,
            int arrayId) {
        String tag = "edu.ucsd.sgf.util.Reflect::loadLineupsFromStringArray";
        String[] names = null;
        Performance[][] lineups = null;

        // Resolve the array ID into the array of XML filenames.
        try { names = res.getStringArray(arrayId); }
        catch(Resources.NotFoundException rnfe) {
            Log.e(tag, "no string array with id=" +
                    Integer.toHexString(arrayId));
            return null;
        }

        // Load the lineup for each filename.
        lineups = new Performance[names.length][];
        for(int i = 0; i < names.length; ++i)
            lineups[i] = loadLineupFromXmlFile(res, names[i]);

        return lineups;
    }
}
