package com.google.efine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.MongoCursor;

public class History extends AppCompatActivity {

    private App app;
    private User user;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> mongoCollection;
    private LinearLayout LL;
    private String licenceNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        LL=(LinearLayout) this.findViewById(R.id.ll2);

        SharedPreferences sharePref= PreferenceManager.getDefaultSharedPreferences(this);
        licenceNo=sharePref.getString("licence",null);

        String appID = "efine-mjrwv"; // replace this with your App ID
        app = new App(new AppConfiguration.Builder(appID)
                .build());

        user = app.currentUser();
        mongoClient = user.getMongoClient("mongodb-atlas");
        mongoDatabase = mongoClient.getDatabase("EfineDB");
        mongoCollection = mongoDatabase.getCollection("Payments");

        Document queryFilter  = new Document("licence", licenceNo);
        RealmResultTask<MongoCursor<Document>> findTask = mongoCollection.find(queryFilter).iterator();
        findTask.getAsync(task -> {
            if (task.isSuccess()) {
                MongoCursor<Document> results = task.get();
                Log.v("EXAMPLE", "successfully found all plants for Store 42:");

                for (MongoCursor<Document> it = results; it.hasNext(); ) {
                    Document result = it.next();
                    Log.v("EXAMPLE", result.toString());
                    LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    TextView tv = new TextView(History.this);
                    lparams.gravity = Gravity.CENTER;
                    lparams.setMargins(10, 40, 10, 10);
                    //tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vehicle, 0, 0, 0);
                    tv.setTextSize(15);
                    tv.setLayoutParams(lparams);
                    tv.setBackgroundColor(Color.GRAY);
                    Date date =result.getDate("date");
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    String strDate = formatter.format(date);
                    int type= Integer.parseInt(result.getString("type"));
                    String[] TypeArray = getResources().getStringArray(R.array.TypeArray);
                    tv.setText(" <-| "+TypeArray[type]+" | "+strDate+" |->");
                    LL.addView(tv);
                }
            } else {
                Log.e("EXAMPLE", "failed to find documents with: ", task.getError());
            }
        });


    }
}