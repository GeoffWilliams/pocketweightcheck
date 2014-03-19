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
import android.widget.Button;
import android.widget.EditText;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowHandler;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.util.FragmentTestUtil;

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
        fragmentTransaction.add(R.id.main_activity, fragment);
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
        assertEquals(getResourceString(R.string.msg_saved), 
                ShadowToast.getTextOfLatestToast());
        assertFalse(fragment.isVisible());
    }

    @Test
    public void testTooLightFail() throws Exception {
        enterText("30");

        // ensure error message and dialogue still open
        ShadowHandler.idleMainLooper();
        assertEquals(getResourceString(R.string.msg_too_light), 
                ShadowToast.getTextOfLatestToast());
        assertTrue(fragment.isVisible());
    }

    @Test
    public void testTooHeavyFail() throws Exception {
        enterText("150");
        
        // ensure error message and dialogue still visible
        ShadowHandler.idleMainLooper();
        assertEquals(getResourceString(R.string.msg_too_heavy), 
                ShadowToast.getTextOfLatestToast());
        assertTrue(fragment.isVisible());
    }

    @Test
    public void testInvalidFail() throws Exception {
        enterText("abc123");
        
        // ensure error message and dialogue still visible
        ShadowHandler.idleMainLooper();
        assertEquals(getResourceString(R.string.msg_invalid), 
                ShadowToast.getTextOfLatestToast());
        assertTrue(fragment.isVisible());
    }

    @Test
    public void testEmptyFail() throws Exception {
        enterText("");
        
        // ensure error message and dialogue still visible
        ShadowHandler.idleMainLooper();
        assertEquals(getResourceString(R.string.msg_invalid), 
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


}
