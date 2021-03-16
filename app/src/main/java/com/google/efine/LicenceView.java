package com.google.efine;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class LicenceView extends AppCompatActivity {
    public DrivingLicence eUser;
    public TextView li;
    public TextView nic;
    public TextView na;
    public TextView sna;
    public TextView dob;
    public TextView address;
    public TextView issued;
    public TextView expired;
    public TextView bGroup;
    public TextView spec;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licence_view);

        eUser= (DrivingLicence) getIntent().getSerializableExtra("User");

        li = findViewById(R.id.licence);
        nic = findViewById(R.id.nic);
        na = findViewById(R.id.name);
        sna = findViewById(R.id.surname);
        dob = findViewById(R.id.dob);
        address = findViewById(R.id.address);
        issued = findViewById(R.id.issued);
        expired = findViewById(R.id.expired);
        bGroup = findViewById(R.id.bgroup);
        spec = findViewById(R.id.spec);

        li.setText("Licence No : "+eUser.getLicenceNo());
        nic.setText("NIC No : "+eUser.getNIC());
        na.setText("Name : "+eUser.getName());
        sna.setText("Surname : "+eUser.getSurname());
        dob.setText("Date of birth : "+eUser.getDOB());
        address.setText("Address : "+eUser.getAddress());
        issued.setText("Issued Date : "+eUser.getIssued());
        expired.setText("Expired Date : "+eUser.getExpired());
        bGroup.setText("Blood Group : "+eUser.getBloodGroup());
        if(eUser.getSpectacles()){
            spec.setText("Spectacles Required");
        }




    }
}