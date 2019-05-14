package com.example.picart;

//import android.content.Context;
//import android.support.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.espresso.Espresso;

//import android.support.test.espresso.matcher.ViewMatchers;

import androidx.test.rule.ActivityTestRule;
//import android.support.test.runner.AndroidJUnit4;
//
//import org.hamcrest.Matchers;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;


import java.io.File;

import static androidx.test.espresso.action.ViewActions.click;
//import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
//import static android.support.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
//import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static   androidx.test.espresso.matcher.ViewMatchers.withId;
//import static android.support.test.espresso.matcher.ViewMatchers.withText;
//import static org.hamcrest.Matchers.equalTo;
//import static org.hamcrest.Matchers.hasEntry;
//import static org.hamcrest.Matchers.is;


//import androidx.test.rule.ActivityTestRule;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PhotoTakeTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);



    @Test
    public void fileCreatedTest(){
//        onView();
        Espresso.onView(withId(R.id.cameraFab)).perform(click());
        File file = mActivityRule.getActivity().photo;
//        Espresso.onData(hasEntry(equalTo(LongListActivity.ROW_TEXT), is(str)))
        assertEquals(true,file.exists());
        mActivityRule.finishActivity();
    }

    @Test
    public void fileNotCreatedBeforeTest(){
//        onView();
        Espresso.onView(withId(R.id.galleryGridView)).check(matches(isCompletelyDisplayed()));
        File file = mActivityRule.getActivity().photo;
//        Espresso.onData(hasEntry(equalTo(LongListActivity.ROW_TEXT), is(str)))
        assertEquals(true,file.exists());
    }


}
