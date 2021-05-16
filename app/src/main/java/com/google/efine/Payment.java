package com.google.efine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.bson.Document;
import org.bson.types.ObjectId;

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

public class Payment extends AppCompatActivity {

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
        setContentView(R.layout.activity_payment);

        LL=(LinearLayout) this.findViewById(R.id.ll2);

        SharedPreferences sharePref= PreferenceManager.getDefaultSharedPreferences(this);
        licenceNo=sharePref.getString("licence",null);

        String appID = "efine-mjrwv"; // replace this with your App ID
        app = new App(new AppConfiguration.Builder(appID)
                .build());

        user = app.currentUser();
        mongoClient = user.getMongoClient("mongodb-atlas");
        mongoDatabase = mongoClient.getDatabase("EfineDB");
        mongoCollection = mongoDatabase.getCollection("Report");

        Document queryFilter  = new Document("licence", licenceNo).append("paid", false);
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
                    TextView tv = new TextView(Payment.this);
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
                    Double py=paymentCalc(result.getString("type"));
                    String[] TypeArray = getResources().getStringArray(R.array.TypeArray);
                    tv.setText(""+py+" has to pay for "+TypeArray[type]+" at "+strDate);
                    LL.addView(tv);

                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            update(result.getObjectId("_id"),result.getString("type"),result.getString("description"),result.getString("licence"),result.getString("police"),result.getDouble("latitude"),result.getDouble("longitude"),result.getDate("date"));
                            pay(result.getString("type"),result.getString("licence"));
                        }
                    });
                }
            } else {
                Log.e("EXAMPLE", "failed to find documents with: ", task.getError());
            }
        });
    }

    public void pay(String violationType,String licence){
        mongoCollection = mongoDatabase.getCollection("Payments");
        Double py=paymentCalc(violationType);

        Document reportDoc  = new Document("_id", new ObjectId()).append("type", violationType).append("licence", licence).append("date", new Date()).append("payment", py);
        mongoCollection.insertOne(reportDoc).getAsync(task -> {
            if (task.isSuccess()) {
                Toast.makeText(getApplicationContext(),"Paid Successfully",Toast.LENGTH_LONG).show();
                Log.v("SRA", "successfully inserted a document with id: " + task.get().getInsertedId());
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            } else {
                Log.e("SRA", "failed to insert documents with: " + task.getError().getErrorMessage());
                Toast.makeText(getApplicationContext(),"Something Wrong",Toast.LENGTH_LONG).show();
            }
        });

    }

    public void update(ObjectId id,String violationType,String dis,String licence,String emailAd,double latitude,double longitude,Date date){
        Document queryFilter = new Document("_id",id);
        Document updateDocument = new Document("paid", true).append("type", violationType).append("description", dis).append("licence", licence).append("police", emailAd).append("latitude", latitude).append("longitude", longitude).append("date", date);
        mongoCollection.updateOne(queryFilter, updateDocument).getAsync(task -> {
            if (task.isSuccess()) {
                long count = task.get().getModifiedCount();
                if (count == 1) {
                    Log.v("EXAMPLE", "successfully updated a document.");
                } else {
                    Log.v("EXAMPLE", "did not update a document.");
                }
            } else {
                Log.e("EXAMPLE", "failed to update document with: ", task.getError());
            }
        });
    }

    public Double paymentCalc(String type){
        int id = Integer.parseInt(type);
        Double value;
        if(id < 24){
            value=1000.0;
        }else if(24 < id && id < 29){
            value=2000.0;
        }else if(id > 28 ){
            value=500.0;
        }else{
            value=3000.0;
        }
        return value;
    }
}