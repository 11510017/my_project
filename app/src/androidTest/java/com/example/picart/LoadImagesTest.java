package com.example.picart;

import android.content.Context;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.espresso.Espresso;



import androidx.test.rule.ActivityTestRule;



import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasFocus;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static   androidx.test.espresso.matcher.ViewMatchers.withId;
//import static android.support.test.espresso.matcher.ViewMatchers.withText;


//import androidx.test.rule.ActivityTestRule;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class LoadImagesTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.picart", appContext.getPackageName());
    }
    @Test
    public void loadGalleryDisplayTest(){
//        onView();
        Espresso.onView(withId(R.id.galleryGridView)).check(matches(isDisplayed()));
    }
    @Test
    public void loadGalleryFullyDisplayTest(){
//        onView();
        Espresso.onView(withId(R.id.galleryGridView)).check(matches(isCompletelyDisplayed()));
}
    @Test
    public void loadImageTest(){
        Espresso.onView(withId(R.id.galleryGridView)).check(matches(hasFocus()));
//        Espresso.onView();
    }

}
