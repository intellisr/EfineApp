package com.google.efine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.bson.Document;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.MongoCursor;

public class Home extends AppCompatActivity {

    private String licenceNo;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> mongoCollection;
    private User user;
    private App app;
    private TextView na;
    public DrivingLicence eUser;
    public int notificationId=1234521234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences sharePref= PreferenceManager.getDefaultSharedPreferences(this);
        licenceNo=sharePref.getString("licence",null);
        Log.v("SRA", "licence: " + licenceNo);

        //na = findViewById(R.id.textView6);

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
                //na.setText(txt);
                eUser = new DrivingLicence(resultdata.getString("LicenceNo"),resultdata.getString("NIC"),resultdata.getString("Name"),resultdata.getString("Surname"),resultdata.getString("Address"),resultdata.getString("DOB"),resultdata.getString("Issued"),resultdata.getString("Expired"),resultdata.getString("BloodGroup"),resultdata.getBoolean("Spectacles"));
                Log.v("SRA", "Name: " + eUser.getName());

            } else {
                Toast.makeText(getApplicationContext(),"Not found",Toast.LENGTH_LONG).show();
                Log.v("SRA",result.getError().toString()+"SRAAAAAAAAAA");
            }
        });

        checkFines();
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

        Intent intent=new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void checkFines(){

        mongoCollection = mongoDatabase.getCollection("Report");

        Document queryFilter  = new Document("licence", licenceNo).append("paid", false);
        RealmResultTask<MongoCursor<Document>> findTask = mongoCollection.find(queryFilter).iterator();
        findTask.getAsync(task -> {
            if (task.isSuccess()) {
                MongoCursor<Document> results = task.get();
                Log.v("EXAMPLE", "successfully found all plants for Store 42:");

                for (MongoCursor<Document> it = results; it.hasNext(); ) {
                    Document result = it.next();
                    Date date =result.getDate("date");
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    String strDate = formatter.format(date);
                    int type= Integer.parseInt(result.getString("type"));
                    String[] TypeArray = getResources().getStringArray(R.array.TypeArray);
                    String CHANNEL_ID = TypeArray[type];
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(Home.this, CHANNEL_ID)
                            .setSmallIcon(R.drawable.e)
                            .setContentTitle(strDate)
                            .setContentText(TypeArray[type])
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        CharSequence name = "MyNotifi";
                        String description = "MyNotifi";
                        int importance = NotificationManager.IMPORTANCE_DEFAULT;
                        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                        channel.setDescription(description);
                        NotificationManager notificationManager = getSystemService(NotificationManager.class);
                        notificationManager.createNotificationChannel(channel);
                    }

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Home.this);
                    notificationManager.notify(notificationId, builder.build());

                }
            } else {
                Log.e("EXAMPLE", "failed to find documents with: ", task.getError());
            }
        });



    }
}