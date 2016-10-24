package com.company;

import java.util.ArrayList;

/**
 * Created by john.tumminelli on 10/15/16.
 */
public class RootObject
{
//    private ArrayList<Face> faces;

    int id;
    String firstName;
    String lastName;
    int height;
    int weight;
    String lastAddress;
    String specialSkills;
    String userEntered;
    String eyeColor;

    public RootObject(String eyeColor) {
        this.eyeColor = eyeColor;
    }

    String imageURL;
    boolean isMe;



    public RootObject(int id, String firstName, String lastName, int height, int weight, String lastAddress, String specialSkills, String userEntered, String eyeColor, String imageURL, boolean isMe) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.height = height;
        this.weight = weight;
        this.lastAddress = lastAddress;
        this.specialSkills = specialSkills;
        this.userEntered = userEntered;
        this.eyeColor = eyeColor;
        this.imageURL = imageURL;
        this.isMe = isMe;
    }

    public RootObject(int id, String firstName, String lastName, int height, int weight, String lastAddress, String specialSkills, String eyeColor) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.height = height;
        this.weight = weight;
        this.lastAddress = lastAddress;
        this.specialSkills = specialSkills;
        this.eyeColor = eyeColor;
    }

    public RootObject(String firstName, String lastName, int height, int weight, String lastAddress, ArrayList<String> specialSkills, int id, String imageURL, boolean isMe) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getLastAddress() {
        return lastAddress;
    }

    public void setLastAddress(String lastAddress) {
        this.lastAddress = lastAddress;
    }

    public String getSpecialSkills() {
        return specialSkills;
    }

    public void setSpecialSkills(String specialSkills) {
        this.specialSkills = specialSkills;
    }

    public String getUserEntered() {
        return userEntered;
    }

    public void setUserEntered(String userEntered) {
        this.userEntered = userEntered;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getEyeColor() {
        return eyeColor;
    }

    public void setEyeColor(String eyeColor) {
        this.eyeColor = eyeColor;
    }
}
