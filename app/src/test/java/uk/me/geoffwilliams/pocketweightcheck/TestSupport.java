/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.me.geoffwilliams.pocketweightcheck;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLog;

/**
 *
 * @author geoff
 */
@RunWith(RobolectricTestRunner.class)
public class TestSupport {

    protected FragmentActivity activity;
    protected FragmentManager fragmentManager;
    protected FragmentTransaction fragmentTransaction;
    
    @Before
    public void setUp() {
        ShadowLog.stream = System.out;
    }

    public void startFragment(Fragment fragment) {
        activity = Robolectric.buildActivity(FragmentActivity.class)
                .create()
                .start()
                .resume()
                .get();

        fragmentManager = activity.getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(fragment, "tag");
        fragmentTransaction.commit();
    }
}
