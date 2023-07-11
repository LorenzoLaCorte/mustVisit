package com.example.mustvisit;

import java.util.ArrayList;
import java.util.List;

public class Utility {
    private static Utility instance;

    private List<TopPlaces> list;

    public List<TopPlaces>  getList() {
        return list;
    }

    public void setList(List<TopPlaces>  list) {
        this.list = list;
    }

    private Utility(){}

    public static Utility getInstance(){
        if(instance == null){
            instance = new Utility();
        }
        return instance;
    }
}
