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
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import java.util.Calendar;
import java.util.GregorianCalendar;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowHandler;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.util.FragmentTestUtil;
import java.util.Date;

import uk.me.geoffwilliams.pocketweightcheck.dao.MockDaoHelper;

/**
 *
 * @author geoff
 */
public class WeightEntryDialogTest extends TestSupport{

    private Button okButton;
    private EditText weightEntryEditText;
    private Button cancelButton;
    private WeightEntryDialog_ fragment;
    
    @Before
    public void setUp() {      
        Settings.setRefreshUi(false);
        
        fragmentActivity = Robolectric.buildActivity(MainActivity_.class)
                .create()
                .start()
                .resume()
                .visible()
                .get();

        fragment = new WeightEntryDialog_();
        
        fragmentManager = fragmentActivity.getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.mainActivity, fragment);
        fragmentTransaction.commit();

        assertNotNull(fragment);
        assertNotNull(fragment.getActivity());

        FragmentTestUtil.startFragment(fragment);
        assertNotNull(fragment);

        // replace the DAO with our mock one that always succeeds...
        fragment.daoHelper = new MockDaoHelper();
        
        // wire up the buttons an fields
        weightEntryEditText = (EditText) fragment.findViewById(R.id.weightEntryEditText);
        okButton = (Button) fragment.findViewById(R.id.okButton);
        cancelButton = (Button) fragment.findViewById(R.id.cancelButton);

    }

    private void enterText(String text) {
        fragment.show(fragmentActivity.getSupportFragmentManager(), "tag");
        assertTrue(fragmentActivity != null);

        weightEntryEditText.setText(text);
        okButton.performClick();
    }

    @Test
    public void testEntryOk() throws Exception {
        enterText("88.2");

        ShadowHandler.idleMainLooper();

        // ensure you get saved message and dialogue is closed 
        assertEquals(getResourceString(R.string.msgSaved), 
                ShadowToast.getTextOfLatestToast());
        assertFalse(fragment.isVisible());
    }

    @Test
    public void testTooLightFail() throws Exception {
        enterText("30");

        // ensure error message and dialogue still open
        ShadowHandler.idleMainLooper();
        assertEquals(getResourceString(R.string.msgTooLight), 
                ShadowToast.getTextOfLatestToast());
        assertTrue(fragment.isVisible());
    }

    @Test
    public void testTooHeavyFail() throws Exception {
        enterText("150");
        
        // ensure error message and dialogue still visible
        ShadowHandler.idleMainLooper();
        assertEquals(getResourceString(R.string.msgTooHeavy), 
                ShadowToast.getTextOfLatestToast());
        assertTrue(fragment.isVisible());
    }

    @Test
    public void testInvalidFail() throws Exception {
        enterText("abc123");
        
        // ensure error message and dialogue still visible
        ShadowHandler.idleMainLooper();
        assertEquals(getResourceString(R.string.msgInvalid), 
                ShadowToast.getTextOfLatestToast());
        assertTrue(fragment.isVisible());
    }

    @Test
    public void testEmptyFail() throws Exception {
        enterText("");
        
        // ensure error message and dialogue still visible
        ShadowHandler.idleMainLooper();
        assertEquals(getResourceString(R.string.msgInvalid), 
                ShadowToast.getTextOfLatestToast());
        assertTrue(fragment.isVisible());
    }

    
    
    @Test
    public void testCancel() throws Exception {
        fragment.show(fragmentActivity.getSupportFragmentManager(), "tag");
        assertTrue(fragment.isVisible());
        cancelButton.performClick();
        assertFalse(fragment.isVisible());
    }

    @Test
    public void testNewDialogFreshDate() {
        // each new weight entry dialogue should have a fresh (today's)
        // date set - otherwise you can accidentally enter data with an
        // old date
        
        // first set an old date and have it accepted
        fragment.show(fragmentActivity.getSupportFragmentManager(), "tag");
        assertTrue(fragmentActivity != null);

        weightEntryEditText.setText("80.8");
        DateUtils dateUtils = fragment.getDateUtils();
        
        // compute an old date
        Calendar cal = GregorianCalendar.getInstance();
        // yesterday
        cal.add(Calendar.DAY_OF_YEAR, - 1);
        
        dateUtils.setDate(cal.getTime());
        okButton.performClick();
        
        // now reshow the dialog and make sure the has been reset
        fragment.show(fragmentActivity.getSupportFragmentManager(), "tag");
        Calendar currentCal = GregorianCalendar.getInstance();
        Calendar dialogCal = GregorianCalendar.getInstance();
        dialogCal.setTime(dateUtils.getDate());
                
        // check date is set to current time
        assertEquals(currentCal.get(Calendar.YEAR), dialogCal.get(Calendar.YEAR));
        assertEquals(currentCal.get(Calendar.MONTH), dialogCal.get(Calendar.MONTH));
        assertEquals(currentCal.get(Calendar.DAY_OF_MONTH), dialogCal.get(Calendar.DAY_OF_MONTH));
        assertEquals(currentCal.get(Calendar.HOUR_OF_DAY), dialogCal.get(Calendar.HOUR_OF_DAY));
        assertEquals(currentCal.get(Calendar.MINUTE), dialogCal.get(Calendar.MINUTE));
    }
}
