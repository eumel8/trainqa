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
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.content.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

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

    private RatingBar feld4InputField;
    private RatingBar feld5InputField;
    private RatingBar feld6InputField;
    private CheckBox feld7InputField;
    private TextView feldwInputField;
    private TextView feldzInputField;
    private ListView listView;
    private static ArrayList<MyDataModel> list;
    private MyArrayAdapter adapter;
    private String MyTrain;
    private String MyZiel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long msTime = System.currentTimeMillis();
        Date curDateTime = new Date(msTime);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd'.'MM'.'yy hh:mm");
        String curDate = formatter.format(curDateTime);

        MyTrain = curDate;
        MyZiel = "-";

        setContentView(R.layout.questions_activity);

        TextView trainView = (TextView) findViewById(R.id.textVieww);
        trainView.setText((CharSequence) MyTrain);
        TextView zielView = (TextView) findViewById(R.id.textViewz);
        zielView.setText((CharSequence) MyZiel);


        Bundle extras = getIntent().getExtras();

        if(extras !=null)
        {
            MyTrain = extras.getString("MyTrain");
            MyZiel = extras.getString("MyZiel");
//            TextView trainView = (TextView) findViewById(R.id.textVieww);
            trainView.setText((CharSequence) MyTrain);
//            TextView zielView = (TextView) findViewById(R.id.textViewz);
            zielView.setText((CharSequence) MyZiel);

        }

    Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://docs.google.com/forms/u/0/d/e/")
                .build();
        final QuestionsSpreadsheetWebService spreadsheetWebService = retrofit.create(QuestionsSpreadsheetWebService.class);

        feld4InputField = (RatingBar) findViewById(R.id.question_feld4_input);
        feld5InputField = (RatingBar) findViewById(R.id.question_feld5_input);
        feld6InputField = (RatingBar) findViewById(R.id.question_feld6_input);
        feld7InputField = (CheckBox) findViewById(R.id.question_feld7_input)
        ;
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

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String MyTrain = list.get(position).getDatum() + " " + list.get(position).getAbfahrt();
                        String MyZiel = list.get(position).getZiel();

                        Intent i = new Intent (getBaseContext(),QuestionsActivity.class);
                        i.putExtra("MyTrain", MyTrain);
                        i.putExtra("MyZiel", MyZiel);

                        startActivity(i);

                    }
                });

                new GetDataTask().execute();
            }
        });

        findViewById(R.id.questions_submit_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Snackbar.make(v, "Ihr Feedback ist erfasst", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        Float feld4Input = feld4InputField.getRating();
                        Float feld5Input = feld5InputField.getRating();
                        Float feld6Input = feld6InputField.getRating();
                        Boolean feld7Input = feld7InputField.isChecked();
                        Call<Void> completeQuestionnaireCall = spreadsheetWebService.completeQuestionnaire(MyTrain, MyZiel, feld4Input, feld5Input, feld6Input, feld7Input);
                        completeQuestionnaireCall.enqueue(callCallback);
                    }
                }
        );
    }
    @SuppressLint("StaticFieldLeak")
    class GetDataTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(QuestionsActivity.this);
        int jIndex;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

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
