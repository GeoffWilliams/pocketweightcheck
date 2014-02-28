/*
 * pocketweightcheck -- simple android app to track of your weight
 * Copyright (C) 2014 Geoff Williams <geoff@geoffwilliams.me.uk>
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package uk.me.geoffwilliams.pocketweightcheck;

import android.app.Activity;
import android.app.DialogFragment;
import android.widget.Button;
import android.widget.EditText;
import java.util.Calendar;
import java.util.Date;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowHandler;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.util.FragmentTestUtil;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 *
 * @author geoff
 */
@RunWith(RobolectricTestRunner.class)
public class WeightEntryDialogTest {

    protected Activity activity = Robolectric.buildActivity(MainActivity.class).create().get();

    private Button okButton;
    private EditText weightEntryEditText;// = (EditText) activity.findViewById(R.id.weightEntryEditText);
    private Button cancelButton;
    private WeightEntryDialog_ fragment;

    public static void startFragment(Fragment fragment) {
        FragmentActivity activity = Robolectric.buildActivity(FragmentActivity.class)
                .create()
                .start()
                .resume()
                .get();

        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(fragment, null);
        fragmentTransaction.commit();
    }

    /*
     @InjectView(R.id.timeTakenMessage)
     private TextView timeTakenMessage;
    
     @InjectView(R.id.dateTakenMessage)
     private TextView dateTakenMessage;
     */
    @Before
    public void setUp() {
        WeightEntryDialog_ fragment = new WeightEntryDialog_();
        startFragment(fragment);
        
        weightEntryEditText = (EditText) fragment.findViewById(R.id.weightEntryEditText);
        okButton = (Button) fragment.findViewById(R.id.okButton);
        cancelButton = (Button) fragment.findViewById(R.id.cancelButton);
        assertNotNull(fragment);
    }

    

    private void enterText(String text) {

        assertTrue(activity != null);

        weightEntryEditText.setText(text);
        okButton.performClick();

    }

    private String getResourceString(int id) {
        return activity.getResources().getString(id);
    }

    @Test
    public void testEntryOk() throws Exception {
        enterText("88.2");
        // pass
        ShadowHandler.idleMainLooper();

        assertEquals(getResourceString(R.string.msg_saved), ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testTooLightFail() throws Exception {
        enterText("30");
        // pass
        ShadowHandler.idleMainLooper();
        assertEquals(getResourceString(R.string.msg_too_light), ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testTooHeavyFail() throws Exception {
        enterText("150");
        // pass
        ShadowHandler.idleMainLooper();
        assertEquals(getResourceString(R.string.msg_too_heavy), ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testInvalidFail() throws Exception {
        enterText("abc123");
        // pass
        ShadowHandler.idleMainLooper();
        assertEquals(getResourceString(R.string.msg_invalid), ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testEmptyFail() throws Exception {
        enterText("");
        // pass
        ShadowHandler.idleMainLooper();
        assertEquals(getResourceString(R.string.msg_invalid), ShadowToast.getTextOfLatestToast());
    }

// Not possible to test this in roboelectric at the moment - gives null
// pointer exception in call to performClick()...
//    @Test
//    public void testCancel() throws Exception {
//        cancelButton.performClick();
//        assertFalse(fragment.isVisible());
//    }

    private void enterTime(int h, int m) {
        TimePickerFragment fragment = new TimePickerFragment();
    }


}
