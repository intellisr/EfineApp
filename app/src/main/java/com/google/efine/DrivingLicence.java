package com.google.efine;
import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import io.realm.RealmObject;

public class DrivingLicence extends RealmObject implements Serializable {
    @PrimaryKey
    public String LicenceNo;
    @Required
    public String NIC;
    @Required
    public String Name;
    @Required
    public String Surname;
    @Required
    public String Address;
    @Required
    public String DOB;
    @Required
    public String Issued;
    @Required
    public String Expired;
    @Required
    public String BloodGroup;
    @Required
    public Boolean Spectacles;

    public DrivingLicence(String licenceNo, String NIC, String name, String surname, String address, String DOB, String issued, String expired, String bloodGroup, Boolean spectacles) {
        LicenceNo = licenceNo;
        this.NIC = NIC;
        Name = name;
        Surname = surname;
        Address = address;
        this.DOB = DOB;
        Issued = issued;
        Expired = expired;
        BloodGroup = bloodGroup;
        Spectacles = spectacles;
    }

    public DrivingLicence(){}

    public String getLicenceNo() {
        return LicenceNo;
    }

    public String getNIC() {
        return NIC;
    }

    public String getName() {
        return Name;
    }

    public String getSurname() {
        return Surname;
    }

    public String getAddress() {
        return Address;
    }

    public String getDOB() {
        return DOB;
    }

    public String getIssued() {
        return Issued;
    }

    public String getExpired() {
        return Expired;
    }

    public String getBloodGroup() {
        return BloodGroup;
    }

    public Boolean getSpectacles() {
        return Spectacles;
    }
}
