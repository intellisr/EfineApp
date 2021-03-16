package com.google.efine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.bson.Document;

import java.security.PublicKey;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;

public class MainActivity extends AppCompatActivity {

    public User user;
    private App app;
    public MongoClient mongoClient;
    public MongoDatabase mongoDatabase;
    public MongoCollection<Document> mongoCollection;
    public EditText li;
    public EditText nic;
    public TextView na;
    public TextView sna;
    public TextView dob;
    public TextView Lable;
    public TextView address;
    public Button confirm;
    public String licence;
    private String licenceNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        li = findViewById(R.id.licence);
        nic = findViewById(R.id.nic);
        na = findViewById(R.id.name);
        sna = findViewById(R.id.name2);
        dob = findViewById(R.id.name3);
        address = findViewById(R.id.name4);
        confirm = findViewById(R.id.confirm);
        Lable = findViewById(R.id.lable);

        Lable.setVisibility(View.INVISIBLE);
        confirm.setVisibility(View.INVISIBLE);


        Realm.init(this);
        String appID = "efine-mjrwv"; // replace this with your App ID
        app = new App(new AppConfiguration.Builder(appID)
                .build());

        SharedPreferences sharePref= PreferenceManager.getDefaultSharedPreferences(this);
        licenceNo=sharePref.getString("licence",null);

        if(licenceNo != null){
            Intent intent=new Intent(this, Home.class);
            startActivity(intent);
        }

        Credentials anonymousCredentials = Credentials.anonymous();
        app.loginAsync(anonymousCredentials, it -> {
            if (it.isSuccess()) {
                Log.v("EXAMPLE", "Successfully authenticated anonymously.");
                user = app.currentUser();

                mongoClient = user.getMongoClient("mongodb-atlas");
                mongoDatabase = mongoClient.getDatabase("EfineDB");
                mongoCollection = mongoDatabase.getCollection("dLicence");
            } else {
                Log.e("EXAMPLE", it.getError().toString());
            }
        });

    }

    public void getLicenceData(View view){
        licence=li.getText().toString();
        Document queryFilter = new Document().append("LicenceNo",licence);
        mongoCollection.findOne(queryFilter).getAsync(result -> {
            if(result.isSuccess()) {
                Document resultdata = result.get();
                Log.v("DATA", "NIC: " + resultdata.getString("NIC"));
                String nicTemp=nic.getText().toString();
                if(nicTemp.equals(resultdata.getString("NIC"))){
                    na.setText("NAME : "+resultdata.getString("Name"));
                    sna.setText("SURNAME : "+resultdata.getString("Surname"));
                    dob.setText("DATE OF BIRTH : "+resultdata.getString("DOB"));
                    address.setText("ADDRESS : "+resultdata.getString("Address"));
                    Lable.setVisibility(View.VISIBLE);
                    confirm.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Please Enter Valid Data",Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(),"Not found",Toast.LENGTH_LONG).show();
                Log.v("Data",result.getError().toString());
            }
        });
    }

    public void ConfirmLicenceData(View view){
        MongoCollection<Document> mongoCollection2 =
                mongoDatabase.getCollection("custom-user-data-collection");
        mongoCollection2.insertOne(
                new Document("user-id-field", user.getId()).append("LicenceNo",licence))
                .getAsync(result -> {
                    if (result.isSuccess()) {
                        Log.v("EXAMPLE", "Inserted custom user data document. _id of inserted document: "
                                + result.get().getInsertedId());

                        SharedPreferences sharePref= PreferenceManager.getDefaultSharedPreferences(this);
                        SharedPreferences.Editor editor = sharePref.edit();
                        editor.putString("licence",licence);
                        editor.apply();

                        Intent intent=new Intent(this, Home.class);
                        startActivity(intent);

                    } else {
                        Log.e("EXAMPLE", "Unable to insert custom user data. Error: " + result.getError());
                    }
                });
    }

}