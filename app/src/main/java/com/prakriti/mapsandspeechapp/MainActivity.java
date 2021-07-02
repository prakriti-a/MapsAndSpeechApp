package com.prakriti.mapsandspeechapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.prakriti.mapsandspeechapp.model.CountryDataSource;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

// works better on physical device
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
// speech recognition framework, internet conn req

    private static final int SPEAK_REQ = 11;
    private TextView txtValue;

    public static CountryDataSource countryDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtValue = findViewById(R.id.txtValue);
        findViewById(R.id.btnVoiceIntent).setOnClickListener(this);
        findViewById(R.id.btnOpenMap).setOnClickListener(this);

        Hashtable<String, String> myCountriesAndMssgs = new Hashtable<>();
        myCountriesAndMssgs.put("India", "Welcome to India");
        myCountriesAndMssgs.put("South Korea", "Welcome to South Korea");
        myCountriesAndMssgs.put("Brazil", "Welcome to Brazil");
        myCountriesAndMssgs.put("Nepal", "Welcome to Nepal");
        myCountriesAndMssgs.put("Thailand", "Welcome to Thailand");
        myCountriesAndMssgs.put("Singapore", "Welcome to Singapore");
        myCountriesAndMssgs.put("Vietnam", "Welcome to Vietnam");

        countryDataSource = new CountryDataSource(myCountriesAndMssgs);

        // check if device supports speech recog functionality
        PackageManager packageManager = this.getPackageManager();
        List<ResolveInfo> listOfInfo = packageManager.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        // access speech recog via intent
        if(listOfInfo.size() > 0) {
            Toast.makeText(this, R.string.speech_supported, Toast.LENGTH_SHORT).show();
            listenToUsersVoice();
        }
        else {
            Toast.makeText(this, R.string.speech_not_supported, Toast.LENGTH_SHORT).show();
        }
    }

    private void listenToUsersVoice() {
        Intent voiceIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Talk to me!"); // prompt user to talk
        voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM); // can talk in any language
        voiceIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10); // max results to be wanted
        startActivityForResult(voiceIntent, SPEAK_REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SPEAK_REQ && resultCode == RESULT_OK) {
            ArrayList<String> voiceToWords = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS); // assign words to arraylist
            // get precision of user's words -> Confid levels
            float[] confidLevels = data.getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);

            // for speech recog test
//            int index = 0;
//            for(String word : voiceToWords) {
//                if(confidLevels != null && index < confidLevels.length) {
//                    String oldTxtValue = txtValue.getText().toString() + "\n";
//                    txtValue.setText(oldTxtValue + word + " - " + confidLevels[index]);
//                }
//            }
            // work with map
            String matchedCountry = countryDataSource.matchWordsWithMinConfidLevel(voiceToWords, confidLevels);
            // pass it to map
            Intent mapIntent = new Intent(this, MapsActivity.class);
            mapIntent.putExtra(CountryDataSource.COUNTRY_KEY, matchedCountry);
            startActivity(mapIntent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnVoiceIntent:
                listenToUsersVoice();
                break;
            case R.id.btnOpenMap:
                startActivity(new Intent(this, MapsActivity.class));
                break;
        }
    }
}