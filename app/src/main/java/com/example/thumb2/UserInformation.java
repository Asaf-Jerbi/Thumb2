package com.example.thumb2;

import android.net.Uri;

import java.io.Serializable;
import java.time.LocalDate;

public class UserInformation implements Serializable {

    //Fields
    private String carNumber;
    private String firstName;
    private String idNumber;
    private String lastName;
    private String personalNumber;
    private String phoneNumber;
    private String releaseDate;
    private String carDescription;
    private Helper.UserType userType;

    // ! ! ! ! ! !
    //NOTE: THIS CLASS MUST HAVE EMPTY CONSTRUCTOR, GETTERS AND SETTERS. DO NOT MODIFY IT.
    // ! ! ! ! ! !

    //Constructors
    public UserInformation() {
    }

    public UserInformation(String firstName, String lastName, String personalNumber,
                           String idNumber, String releaseDate, String carNumber,
                           String phoneNumber, String carDescription, Helper.UserType userType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalNumber = personalNumber;
        this.idNumber = idNumber;
        this.releaseDate = releaseDate;
        this.carNumber = carNumber;
        this.phoneNumber = phoneNumber;
        this.carDescription = carDescription;
        this.userType = userType;
    }

    public void setCarDescription(String carDescription) {
        this.carDescription = carDescription;
    }

    public String getCarDescription() {
        return carDescription;
    }

    public void setUserType(Helper.UserType userType) {
        this.userType = userType;
    }

    public Helper.UserType getUserType() {
        return userType;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
}