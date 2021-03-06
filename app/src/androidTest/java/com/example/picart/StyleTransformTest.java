package com.example.picart;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class StyleTransformTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.CAMERA",
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE");

    @Test
    public void sizeButtonTest() {
        DataInteraction cardView = onData(anything())
                .inAdapterView(allOf(withId(R.id.galleryGridView),
                        childAtPosition(
                                withClassName(is("androidx.coordinatorlayout.widget.CoordinatorLayout")),
                                0)))
                .atPosition(0);
        cardView.perform(click());

        DataInteraction cardView2 = onData(anything())
                .inAdapterView(allOf(withId(R.id.galleryGridView),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                0)))
                .atPosition(0);
        cardView2.perform(click());

        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.btn_filter),
                        childAtPosition(
                                allOf(withId(R.id.rg_modes),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                2)),
                                2),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction button = onView((withText(containsString("256"))));
        button.check(matches(isDisplayed()));

        button.perform(click());

        ViewInteraction button2 = onView((withText(containsString("128"))));
        button2.check(matches(isDisplayed()));

    }

    @Test
    public void saveButtonTest() {
        DataInteraction cardView = onData(anything())
                .inAdapterView(allOf(withId(R.id.galleryGridView),
                        childAtPosition(
                                withClassName(is("androidx.coordinatorlayout.widget.CoordinatorLayout")),
                                0)))
                .atPosition(0);
        cardView.perform(click());

        DataInteraction cardView2 = onData(anything())
                .inAdapterView(allOf(withId(R.id.galleryGridView),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                0)))
                .atPosition(0);
        cardView2.perform(click());

        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.btn_filter),
                        childAtPosition(
                                allOf(withId(R.id.rg_modes),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                2)),
                                2),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction button = onView((withText(containsString("save"))));
        button.check(matches(isDisplayed()));
    }
    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
