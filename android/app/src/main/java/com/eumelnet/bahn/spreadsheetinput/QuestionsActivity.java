package com.eumelnet.bahn.spreadsheetinput;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.content.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

class Keys {
    static final String KEY_CONTACTS;

    static {
        KEY_CONTACTS = "Tabellenblatt1";
    }
}

public class QuestionsActivity extends AppCompatActivity {

    private DatePicker feld1DayInputField;
    private TimePicker feld1TimeInputField;
    private RadioButton feld2InputField;
    private RatingBar feld4InputField;
    private RatingBar feld5InputField;
    private RatingBar feld6InputField;
    private CheckBox feld7InputField;
    private TextView feldwInputField;
    private ListView listView;
    private static ArrayList<MyDataModel> list;
    private MyArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setContentView(R.layout.layout_row_view);
        setContentView(R.layout.questions_activity);

        // von MainActivity


    Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://docs.google.com/forms/u/0/d/e/")
                .build();
        final QuestionsSpreadsheetWebService spreadsheetWebService = retrofit.create(QuestionsSpreadsheetWebService.class);

        feld1DayInputField = (DatePicker) findViewById(R.id.question_feld1d_input);
        feld1TimeInputField = (TimePicker) findViewById(R.id.question_feld1t_input);
        feld2InputField = (RadioButton) findViewById(R.id.question_feld2_input);
        feld4InputField = (RatingBar) findViewById(R.id.question_feld4_input);
        feld5InputField = (RatingBar) findViewById(R.id.question_feld5_input);
        feld6InputField = (RatingBar) findViewById(R.id.question_feld6_input);
        feld7InputField = (CheckBox) findViewById(R.id.question_feld7_input);
        feldwInputField = (TextView) findViewById(R.id.textVieww);
        feld1TimeInputField.setIs24HourView(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(@NonNull View view) {
                setContentView(R.layout.layout_row_view);
                // Fresco.initialize(QuestionsActivity.this);
                list = new ArrayList<>();
                adapter = new MyArrayAdapter(QuestionsActivity.this, list);
                listView = (ListView) findViewById(R.id.listView);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    // private ListView selectabfahrt;

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String text = list.get(position).getDatum() + " " + list.get(position).getAbfahrt() ;
                        // setContentView(R.layout.questions_activity);
                        startActivity(new Intent(QuestionsActivity.this,QuestionsActivity.class));

                        TextView textView = (TextView) findViewById(R.id.textVieww);
                        textView.setText((CharSequence) text);

                        // startActivity(new Intent(QuestionsActivity.this,QuestionsActivity.class));

                        // Snackbar.make(findViewById(R.id.parent), list.get(position).getDatum() + " => " + list.get(position).getZug() + list.get(position).getAbfahrt() + list.get(position).getZiel(), Snackbar.LENGTH_LONG).show();
                    }
                });

                // Toast toast = Toast.makeText(getApplicationContext(), "Klicken Sie den FloatingActionButton zum Laden des Fahrplans", Toast.LENGTH_LONG);
                // toast.setGravity(Gravity.CENTER, 0, 0);
                // toast.show();

                // end MainActicity

                new GetDataTask().execute();
            }
        });

        findViewById(R.id.questions_submit_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Snackbar.make(v, "Ihr Feedback ist erfasst", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

//                        String feld1Input = String.format("%02d.%02d.%04d %02d:%02d",feld1DayInputField.getDayOfMonth(),feld1DayInputField.getMonth() + 1,feld1DayInputField.getYear(),feld1TimeInputField.getHour(),feld1TimeInputField.getMinute());
                        String feld1Input = feldwInputField.getText().toString();
                        Boolean feld2Input = feld2InputField.isChecked();
                        Float feld4Input = feld4InputField.getRating();
                        Float feld5Input = feld5InputField.getRating();
                        Float feld6Input = feld6InputField.getRating();
                        Boolean feld7Input = feld7InputField.isChecked();
                        Call<Void> completeQuestionnaireCall = spreadsheetWebService.completeQuestionnaire(feld1Input, feld2Input, feld4Input, feld5Input, feld6Input, feld7Input);
                        completeQuestionnaireCall.enqueue(callCallback);
                    }
                }
        );
    }
    @SuppressLint("StaticFieldLeak")
    class GetDataTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(QuestionsActivity.this);
        int jIndex;
        int x;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            x=list.size();
            if(x==0)
                jIndex=0;
            else
                jIndex=x;
            // dialog.setTitle("Bitte warten..."+x);
            dialog.setTitle("Bitte warten...");
            dialog.setMessage("Wir laden den Fahrplan");
            dialog.show();
        }

        @Nullable
        @Override
        protected Void doInBackground(Void... params) {

            JSONObject jsonObject = JSONParser.getDataFromWeb();

            try {
                if (jsonObject != null) {
                    if(jsonObject.length() > 0) {
                        JSONArray array = jsonObject.getJSONArray(Keys.KEY_CONTACTS);
                        int lenArray = array.length();
                        if(lenArray > 0) {

                            for( ; jIndex < lenArray; jIndex++) {

                                MyDataModel model = new MyDataModel();
                                JSONObject innerObject = array.getJSONObject(jIndex);
                                // Iterator keys = innerObject.keys();
                                JSONArray innerArray =  innerObject.names();

                                String datum = innerObject.getString(innerArray.get(0).toString());
                                String zug = innerObject.getString(innerArray.get(1).toString());
                                String abfahrt = innerObject.getString(innerArray.get(2).toString());
                                String ziel = innerObject.getString(innerArray.get(3).toString());

                                model.setDatum(datum);
                                model.setZug(zug);
                                model.setAbfahrt(abfahrt);
                                model.setZiel(ziel);

                                list.add(model);
                            }
                        }
                    }
                }
            } catch (JSONException je) {
                Log.i(JSONParser.TAG, "" + je.getLocalizedMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();

            if(list.size() > 0) {
                adapter.notifyDataSetChanged();
            } else {
                Snackbar.make(findViewById(R.id.parentLayout), "No Data Found", Snackbar.LENGTH_LONG).show();
            }
        }
    }
    private final Callback<Void> callCallback = new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {

            Log.d("XXX", "Submitted. " + response);
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            Log.e("XXX", "Failed", t);
        }
    };
}
