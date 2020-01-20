package com.eumelnet.bahn.spreadsheetinput;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ScrollView;
import android.widget.RatingBar;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.support.design.widget.Snackbar;


import java.text.Format;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class QuestionsActivity extends AppCompatActivity {

    private DatePicker feld1DayInputField;
    private TimePicker feld1TimeInputField;
    private RadioButton feld2InputField;
    private RatingBar feld4InputField;
    private RatingBar feld5InputField;
    private RatingBar feld6InputField;
    private CheckBox feld7InputField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questions_activity);

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

        feld1TimeInputField.setIs24HourView(true);

        findViewById(R.id.questions_submit_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Snackbar.make(v, "Ihr Feedback ist erfasst", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        String feld1Input = String.format("%02d.%02d.%04d %02d:%02d",feld1DayInputField.getDayOfMonth(),feld1DayInputField.getMonth() + 1,feld1DayInputField.getYear(),feld1TimeInputField.getHour(),feld1TimeInputField.getMinute());
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
