package com.app.abdullah.atlanticsurveyors;

/**
 * Created by M. Immad Javed on 2/13/2018.
 */
public class Site {

    private int site_id;
    private String site_name;

    public Site(){}

    public Site(int site_id, String site_name){
        this.site_id = site_id;
        this.site_name = site_name;
    }

    public void setId(int site_id){
        this.site_id = site_id;
    }

    public void setName(String site_name){
        this.site_name = site_name;
    }

    public int getId(){
        return this.site_id;
    }

    public String getName(){
        return this.site_name;
    }

    }
