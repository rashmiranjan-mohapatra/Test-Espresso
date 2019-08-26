package com.noralynn.coffeeShop;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.uiautomator.UiDevice;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.noralynn.coffeeShop.beveragelist.BeverageListActivity;
import com.noralynn.coffeeShop.beveragelist.BeverageViewHolder;
import com.noralynn.coffeeShop.coffeeshoplist.CoffeeShopListActivity;
import com.noralynn.coffeeShop.coffeeshoplist.idlingresource.CoffeeShopsIdlingResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.runner.lifecycle.Stage.RESUMED;
import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4.class)
public class BevarageListTest{

    CoffeeShopsIdlingResource idlingResource;

    @Rule
    public ActivityTestRule<BeverageListActivity> mActivityRule =
            new ActivityTestRule<>(BeverageListActivity.class);

//    @Before
//    public void grantPermission() {
//
//        getInstrumentation().getUiAutomation().executeShellCommand(
//                "pm grant " + getTargetContext().getPackageName()
//                        + " android.permission.ACCESS_COARSE_LOCATION");
//
//        getInstrumentation().getUiAutomation().executeShellCommand(
//                "pm grant " + getTargetContext().getPackageName()
//                        + " android.permission.ACCESS_FINE_LOCATION");
//    }

    public Activity getActivityInstance() {
        final Activity[] activity = new Activity[1];
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable( ) {
            public void run() {
                Activity currentActivity = null;
                Collection resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
                if (resumedActivities.iterator().hasNext()){
                    currentActivity = (Activity) resumedActivities.iterator().next();
                    activity[0] = currentActivity;
                }
            }
        });

        return activity[0];
    }

    @Before
    public void before() {
        Instrumentation instrumentation
                = InstrumentationRegistry.getInstrumentation();
        Context ctx = instrumentation.getTargetContext();
    }

    @After
    public void after() {
        if (null != idlingResource) Espresso.unregisterIdlingResources(idlingResource);
    }

    @Test
    public void ensureTextChangesWork() throws InterruptedException {
        onView(withId(R.id.beverages_recycler))
                .perform(
                        RecyclerViewActions.actionOnItemAtPosition(3, click())
                );
        // Check that the text was changed.
        onView(withId(R.id.text_title)).check(matches(withText("Flat white")));
    }

    @Test
    public void iterateOverEachView() throws InterruptedException {
        final View viewById = mActivityRule.getActivity().findViewById(R.id.beverages_recycler);
        final RecyclerView recyclerView = (RecyclerView) viewById;
        final RecyclerView.Adapter adapter = recyclerView.getAdapter();
        final int itemsCount = adapter.getItemCount();
        for (int i = 0;i < itemsCount;i++){
            onView(withId(R.id.beverages_recycler))
                    .perform(RecyclerViewActions.scrollToPosition(i)
                    );
            Thread.sleep(1000);
            onView(withId(R.id.beverages_recycler))
                    .perform(
                            RecyclerViewActions.actionOnItemAtPosition(i, click())
                    );
            UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            mDevice.pressBack();
        }
    }


    @Test
    public void iterateOverEachView3() throws InterruptedException {
        final View viewById = mActivityRule.getActivity().findViewById(R.id.beverages_recycler);
        final RecyclerView recyclerView = (RecyclerView) viewById;
        final RecyclerView.Adapter adapter = recyclerView.getAdapter();
        final int itemsCount = adapter.getItemCount();
        for (int i = 0;i < itemsCount;i++){
            onView(withId(R.id.beverages_recycler)).perform(TestUtils.actionOnItemViewAtPosition(i,
                    R.id.text_title,
                    click()));
            UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            mDevice.pressBack();
        }
    }


    @Test
    public void scrollToPosition7() {
        onView(withId(R.id.beverages_recycler))
                .perform(RecyclerViewActions.scrollToPosition(7)
                );
    }

    @Test
    public void iterateOverEachView2(){
        final View viewById = mActivityRule.getActivity().findViewById(R.id.beverages_recycler);
        final RecyclerView recyclerView = (RecyclerView) viewById;
        final RecyclerView.Adapter adapter = recyclerView.getAdapter();
        final int itemsCount = adapter.getItemCount();
        for (int i = 0;i < itemsCount;i++){
            onView(new RecyclerViewMatcher(R.id.beverages_recycler)
                    .atPositionOnView(i, -1))
                    .perform(click());
            UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            mDevice.pressBack();
        }
    }

    @Test
    public void nearbyCoffeeShopTest() {
        onView(withId(R.id.map_fab)).perform(click());
        Activity activity = getActivityInstance();
        boolean b = (activity instanceof  CoffeeShopListActivity);
        assertTrue(b);
        idlingResource = ((CoffeeShopListActivity)activity).getCoffeeShopsIdlingResource();
        Espresso.registerIdlingResources(idlingResource);
        String myTextFromResources = getResourceString(R.string.error_unable_to_load_coffee_shops);
        onView(withId(R.id.empty_text)).check(matches(isDisplayed()));
    }

    private String getResourceString(int id) {
        Context targetContext = InstrumentationRegistry.getTargetContext();
        return targetContext.getResources().getString(id);
    }

}
