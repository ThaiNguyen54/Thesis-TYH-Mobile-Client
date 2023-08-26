package com.example.tryyourhair.Models;

public class GenerationData {
    private String ImageBase64;
    private String ImageName;
    private String HairstyleName;
    private String GeneratedImageURL;
    private String RegistrationToken;

    // Constructor with 3 parameters
    public GenerationData(String ImageBase64, String ImageName, String HairstyleName, String RegistrationToken) {
        this.ImageBase64 = ImageBase64;
        this.ImageName = ImageName;
        this.HairstyleName = HairstyleName;
        this.GeneratedImageURL = "";
        this.RegistrationToken = RegistrationToken;
    }

    // Constructor with 4 parameters
//    public GenerationData(String ImageBase64, String ImageName, String HairstyleName, String GeneratedImageURL) {
//        this.ImageBase64 = ImageBase64;
//        this.ImageName = ImageName;
//        this.HairstyleName = HairstyleName;
//        this.GeneratedImageURL = GeneratedImageURL;
//    }

    public void setImageBase64(String imageBase64) {
        ImageBase64 = imageBase64;
    }

    public String getImageBase64() {
        return ImageBase64;
    }

    public void setImageName(String imageName) {
        ImageName = imageName;
    }

    public String getImageName() {
        return ImageName;
    }

    public void setHairstyleName(String hairstyleName) {
        HairstyleName = hairstyleName;
    }

    public String getHairstyleName() {
        return HairstyleName;
    }

    public void setGeneratedImageURL(String generatedImageURL) {
        GeneratedImageURL = generatedImageURL;
    }

    public String getGeneratedImageURL() {
        return GeneratedImageURL;
    }

    public void setRegistrationToken(String registrationToken) {
        RegistrationToken = registrationToken;
    }

    public String getRegistrationToken() {
        return RegistrationToken;
    }
}
