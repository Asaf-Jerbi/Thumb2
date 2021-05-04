package com.example.thumb2;

import android.renderscript.ScriptIntrinsicYuvToRGB;

import java.time.LocalDate;

public class UserInformation {

    //Fields
    private int id;
    private int personalNumber;
    private String fName;
    private String LName;
    private LocalDate releaseDate;
    private Integer carNumber;
    private String carModel;
    private String carColor;

    // ! ! ! ! ! !
    //NOTE: THIS CLASS MUST HAVE EMPTY CONSTRUCTOR, GETTERS AND SETTERS. DO NOT MODIFY IT.
    // ! ! ! ! ! !

    public UserInformation() {}

    public UserInformation(int id, int personalNumber, String fName, String LName,
                           LocalDate releaseDate, Integer carNumber, String carModel, String carColor) {
        this.id = id;
        this.personalNumber = personalNumber;
        this.fName = fName;
        this.LName = LName;
        this.releaseDate = releaseDate;
        this.carNumber = carNumber;
        this.carModel = carModel;
        this.carColor = carColor;
    }

    public int getId() {
        return id;
    }

    public int getPersonalNumber() {
        return personalNumber;
    }

    public String getfName() {
        return fName;
    }

    public String getLName() {
        return LName;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public Integer getCarNumber() {
        return carNumber;
    }

    public String getCarModel() {
        return carModel;
    }

    public String getCarColor() {
        return carColor;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPersonalNumber(int personalNumber) {
        this.personalNumber = personalNumber;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public void setLName(String LName) {
        this.LName = LName;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setCarNumber(Integer carNumber) {
        this.carNumber = carNumber;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public void setCarColor(String carColor) {
        this.carColor = carColor;
    }
}
