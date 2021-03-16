package com.google.efine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.bson.Document;

import java.io.Serializable;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;

public class Home extends AppCompatActivity {

    private String licenceNo;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> mongoCollection;
    private User user;
    private App app;
    private TextView na;
    public DrivingLicence eUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences sharePref= PreferenceManager.getDefaultSharedPreferences(this);
        licenceNo=sharePref.getString("licence",null);

        na = findViewById(R.id.textView6);

        String appID = "efine-mjrwv"; // replace this with your App ID
        app = new App(new AppConfiguration.Builder(appID)
                .build());

        user = app.currentUser();
        mongoClient = user.getMongoClient("mongodb-atlas");
        mongoDatabase = mongoClient.getDatabase("EfineDB");
        mongoCollection = mongoDatabase.getCollection("dLicence");

        Document queryFilter = new Document().append("LicenceNo",licenceNo);
        mongoCollection.findOne(queryFilter).getAsync(result -> {
            if(result.isSuccess()) {
                Document resultdata = result.get();
                String txt="Hello "+resultdata.getString("Name");
                na.setText(txt);
                eUser = new DrivingLicence(resultdata.getString("LicenceNo"),resultdata.getString("NIC"),resultdata.getString("Name"),resultdata.getString("Surname"),resultdata.getString("Address"),resultdata.getString("DOB"),resultdata.getString("Issued"),resultdata.getString("Expired"),resultdata.getString("BloodGroup"),resultdata.getBoolean("Spectacles"));
                Log.v("DATA", "Name: " + eUser.getName());

            } else {
                Toast.makeText(getApplicationContext(),"Not found",Toast.LENGTH_LONG).show();
                Log.v("Data",result.getError().toString());
            }
        });
    }

    public void goELicence(View view){
        Intent intent=new Intent(this, LicenceView.class);
        intent.putExtra("User", (Serializable) eUser);
        startActivity(intent);
    }

    public void goQR(View view){
        Intent intent=new Intent(this, QrView.class);
        intent.putExtra("User", (Serializable) eUser);
        startActivity(intent);
    }

    public void goHistory(View view){
        Intent intent=new Intent(this, History.class);
        intent.putExtra("User", (Serializable) eUser);
        startActivity(intent);
    }

    public void goPayment(View view){
        Intent intent=new Intent(this, Payment.class);
        intent.putExtra("User", (Serializable) eUser);
        startActivity(intent);
    }

    public void logout(View view){
        SharedPreferences sharePref= PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharePref.edit();
        editor.putString("licence",null);
        editor.apply();

    }
}