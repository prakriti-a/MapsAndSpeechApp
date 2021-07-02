package com.prakriti.mapsandspeechapp.model;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class CountryDataSource {

    public static final String COUNTRY_KEY = "country";
    public static final float MIN_CONFIDENCE_LEVEL = 0.5f;
    public static final String DEFAULT_COUNTRY_NAME = "India";
    public static final double DEFAULT_COUNTRY_LATITUDE = 22.713139694029493;
    public static final double DEFAULT_COUNTRY_LONGITUDE = 79.9174752374013;
    public static final String DEFAULT_MESSAGE = "Welcome";

    private Hashtable<String, String> countriesAndMessages; // key-value

    public CountryDataSource(Hashtable<String, String> countriesAndMessages) {
        this.countriesAndMessages = countriesAndMessages; // init hash table
    }

    public String matchWordsWithMinConfidLevel(ArrayList<String> userWords, float[] confidLevels) {
        if(userWords == null || confidLevels == null) {
            return DEFAULT_COUNTRY_NAME;
        }
        int numOfWords = userWords.size();
        // Enumeration interface -> generates a series of elements (similar to Iterator interface)
        Enumeration<String> countries;
        for(int index = 0; index < numOfWords && index < confidLevels.length; index++) {
            if(confidLevels[index] < MIN_CONFIDENCE_LEVEL) {
                // cannot accept values below min confid level
                break;
            }
            String acceptedWord = userWords.get(index); // get the word that passes min check
            countries = countriesAndMessages.keys();

            while (countries.hasMoreElements()) { // till its not empty
                String selectedCountry = countries.nextElement();
                if(acceptedWord.equalsIgnoreCase(selectedCountry)) {
                    return acceptedWord;
                }
            }
        }
        return DEFAULT_COUNTRY_NAME; // in case all conditions fail
    }

    public String getCountryInfo(String country) {
        return countriesAndMessages.get(country); // returns value
    }

}
