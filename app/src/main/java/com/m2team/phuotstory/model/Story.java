package com.m2team.phuotstory.model;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "STORY".
 */
public class Story {

    private Long id;
    private String title;
    private String description;
    private String location;
    private String photoUri;
    private String friends;
    private String feeling;
    private Long travelTime;
    private Float lat;
    private Float lng;
    private Float road;

    public Story() {
    }

    public Story(Long id) {
        this.id = id;
    }

    public Story(Long id, String title, String description, String location, String photoUri, String friends, String feeling, Long travelTime, Float lat, Float lng, Float road) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.photoUri = photoUri;
        this.friends = friends;
        this.feeling = feeling;
        this.travelTime = travelTime;
        this.lat = lat;
        this.lng = lng;
        this.road = road;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getFriends() {
        return friends;
    }

    public void setFriends(String friends) {
        this.friends = friends;
    }

    public String getFeeling() {
        return feeling;
    }

    public void setFeeling(String feeling) {
        this.feeling = feeling;
    }

    public Long getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(Long travelTime) {
        this.travelTime = travelTime;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLng() {
        return lng;
    }

    public void setLng(Float lng) {
        this.lng = lng;
    }

    public Float getRoad() {
        return road;
    }

    public void setRoad(Float road) {
        this.road = road;
    }

}
