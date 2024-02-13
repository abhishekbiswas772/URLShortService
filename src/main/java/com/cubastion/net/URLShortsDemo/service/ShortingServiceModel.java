package com.cubastion.net.URLShortsDemo.service;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ShortingServiceModel {
    private String url;
    @JsonCreator
    public ShortingServiceModel() {}
    @JsonCreator
    public ShortingServiceModel(@JsonProperty("url") String url){this.url = url;}
    public String getUrl(){return this.url;}
    public void setUrl(String url){this.url = url;}
}
