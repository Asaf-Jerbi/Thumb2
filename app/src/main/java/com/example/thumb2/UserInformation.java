package com.example.thumb2;

import java.time.LocalDate;

public class UserInformation {

    //Fields
    private String firstName;
    private String lastName;
    private int personalNumber;
    private int idNumber;
    private LocalDate releaseDate;
    private Integer carNumber;
    private String phoneNumber;
    private Helper.UserType userType;


    public void setUserType(Helper.UserType userType) {
        this.userType = userType;
    }

    public Helper.UserType getUserType() {
        return userType;
    }

    // ! ! ! ! ! !
    //NOTE: THIS CLASS MUST HAVE EMPTY CONSTRUCTOR, GETTERS AND SETTERS. DO NOT MODIFY IT.
    // ! ! ! ! ! !

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public UserInformation(String firstName, String lastName, int personalNumber, int idNumber,
                           LocalDate releaseDate, Integer carNumber, String phoneNumber,
                           Helper.UserType userType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalNumber = personalNumber;
        this.idNumber = idNumber;
        this.releaseDate = releaseDate;
        this.carNumber = carNumber;
        this.phoneNumber = phoneNumber;
        this.userType = userType;
    }

    public UserInformation() {
    }

    public int getIdNumber() {
        return idNumber;
    }

    public int getPersonalNumber() {
        return personalNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public Integer getCarNumber() {
        return carNumber;
    }

    public void setIdNumber(int idNumber) {
        this.idNumber = idNumber;
    }

    public void setPersonalNumber(int personalNumber) {
        this.personalNumber = personalNumber;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setCarNumber(Integer carNumber) {
        this.carNumber = carNumber;
    }
}
