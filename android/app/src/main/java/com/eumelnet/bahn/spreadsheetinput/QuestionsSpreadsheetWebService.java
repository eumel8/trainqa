package com.eumelnet.bahn.spreadsheetinput;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface QuestionsSpreadsheetWebService {

    @POST("1FAIpQLSeyBr1tJ3y7SjX2c107B92yv2P600WRxmlJyAWzrPt59Bvf7g/formResponse")
    @FormUrlEncoded
    Call<Void> completeQuestionnaire(
            @Field("entry.1476348758") String feld1t,
            @Field("entry.679257558")  String feld2,
            @Field("entry.1485087550") Float feld4,
            @Field("entry.617878135") Float feld5,
            @Field("entry.1543695788") Float feld6,
            @Field("entry.51849126") Boolean feld7
    );

}
