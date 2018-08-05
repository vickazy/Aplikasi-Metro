package com.jojo.metroapp.model;

public class FormModel {

    private String formNumber;
    private String username;
    private String titleForm;
    private String alasan;
    private String dateDari;
    private String dateSampai;
    private String imageUrl;
    private String datePublished;
    private String status;

    public FormModel(String formNumber, String username, String titleForm, String alasan, String dateDari, String dateSampai, String imageUrl, String datePublished, String status) {
        this.formNumber = formNumber;
        this.username = username;
        this.titleForm = titleForm;
        this.alasan = alasan;
        this.dateDari = dateDari;
        this.dateSampai = dateSampai;
        this.imageUrl = imageUrl;
        this.datePublished = datePublished;
        this.status = status;
    }

    public String getFormNumber() {
        return formNumber;
    }

    public void setFormNumber(String formNumber) {
        this.formNumber = formNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitleForm() {
        return titleForm;
    }

    public void setTitleForm(String titleForm) {
        this.titleForm = titleForm;
    }

    public String getAlasan() {
        return alasan;
    }

    public void setAlasan(String alasan) {
        this.alasan = alasan;
    }

    public String getDateDari() {
        return dateDari;
    }

    public void setDateDari(String dateDari) {
        this.dateDari = dateDari;
    }

    public String getDateSampai() {
        return dateSampai;
    }

    public void setDateSampai(String dateSampai) {
        this.dateSampai = dateSampai;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(String datePublished) {
        this.datePublished = datePublished;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
