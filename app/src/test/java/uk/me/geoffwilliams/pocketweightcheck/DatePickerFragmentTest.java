/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.me.geoffwilliams.pocketweightcheck;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import java.util.Calendar;
import java.util.Date;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowToast;
import static org.robolectric.Robolectric.shadowOf;

import org.robolectric.util.FragmentTestUtil;

/**
 *
 * @author geoff
 */
@RunWith(RobolectricTestRunner.class)
public class DatePickerFragmentTest {

    protected FragmentActivity activity;// = // Robolectric.buildActivity(MainActivity.class).create().get();
    private DatePickerFragment fragment;
    private FragmentManager fragmentManager;
    private DateUtils utils = new DateUtils();

    public static void startFragment(Fragment fragment) {
        FragmentActivity activity = Robolectric.buildActivity(FragmentActivity.class)
                .create()
                .start()
                .resume()
                .get();

        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(fragment, "tag");
        fragmentTransaction.commit();
    }

    @Before
    public void setUp() {
        activity = Robolectric.buildActivity(MainActivity.class)
                .create()
                .start()
                .resume()
                .get();

//        activity = new FragmentActivity();
//        shadowOf(activity).callOnCreate(null);
//        shadowOf(activity).callOnStart();
//        shadowOf(activity).callOnResume();

        fragment = new DatePickerFragment();
        fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_activity, fragment);
        //fragmentTransaction.add(fragment, null);
        fragmentTransaction.commit();

        assertNotNull(fragment);
        assertNotNull(fragment.getActivity());

//        fragmentManager = activity.getSupportFragmentManager();
        // manually inject dependencies -- they have to be public for androidannotations
        // to work so may as well use this to our advantage.
        //
        // we cannot directly test the picker but we can test the logic in the 
        // onDateSet method...
        fragment.utils = utils;
        utils.context = activity;
        FragmentTestUtil.startFragment(fragment);
        assertNotNull(fragment);

    }

    private String getResourceString(int id) {
        return activity.getResources().getString(id);
    }

    private void enterDate(int y, int m, int d) {
        DatePickerFragment fragment = new DatePickerFragment();
    }

    @Test
    public void testDateTooOldFail() throws Exception {

        // work out a date that is too old
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, - 1000);// DateUtils.MAX_SAMPLE_DATE - 1);

        System.out.println("cal date " + cal.getTime().toString());
        
        assertNotNull(fragment);

        // send for processing
        fragment.show(activity.getSupportFragmentManager(), "tag");
        fragment.onDateSet(null, //new DatePicker(activity), 
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));

        assertEquals(getResourceString(R.string.msg_too_old), ShadowToast.getTextOfLatestToast());

    }

    @Test
    public void testDateFutureFail() throws Exception {

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        enterDate(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) - 30,
                cal.get(Calendar.DAY_OF_MONTH)
        );

        assertEquals(getResourceString(R.string.msg_future), ShadowToast.getTextOfLatestToast());

    }
}
