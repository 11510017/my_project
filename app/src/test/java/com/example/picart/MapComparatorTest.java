package com.example.picart;
//import android.app.Activity;
//import android.content.ContextWrapper;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.content.Context;
//import android.widget.GridView;
//import android.widget.ImageView;
//
//import com.bumptech.glide.GlideContext;

import org.junit.Test;
import static org.junit.Assert.*;
//import org.junit.Assert;
import org.junit.Before;
//import org.junit.Test;
//
//import static org.mockito.Mockito.*;
//import static org.junit.Assert.*;

//import java.util.ArrayList;
import java.util.HashMap;
//import java.util.List;
import java.util.Map;

public class MapComparatorTest {
    MapComparator mc;
    String key;
//    String value = "value1";
    Map<String, String> map1;
    Map<String, String> map2;
    @Before
    public void setup(){
//        mc = new MapComparator(key1,"asc");
        map1 = new HashMap<>();
        map2 = new HashMap<>();
        map1.put("1","a");
        map2.put("1","b");
        key = "1";

    }

    @Test(timeout = 4000)
    public void compareUsingASCTest(){
        mc = new MapComparator(key,"asc");
        int r = mc.compare(map1,map2);
        assertEquals(-1,r);
    }
    @Test(timeout = 4000)
    public void compareNotUsingASCTest(){
        mc = new MapComparator(key,"abb");
        int r = mc.compare(map1,map2);
        assertEquals(1,r);
    }

}
