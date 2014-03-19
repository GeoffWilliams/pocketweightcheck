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

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

/**
 * Workaround for all preferences being stored as strings by android - convert 
 * them at runtime
 * @author geoff
 */
@EBean
public class PrefsWrapper {
    
    private static final float UNDEFINED = 0;
    
    @Pref
    Prefs_ prefs;
    
    private float convertFloat(String value) {
        float f;
        try {
            f = Float.parseFloat(value);
        } catch (NumberFormatException e) {
            f = UNDEFINED;
        }
        return f;
    }
    
    public float getHeight() {
        return convertFloat(prefs.height().get());
    }

    public float getTargetWeight() {
        return convertFloat(prefs.targetWeight().get());
    }

    public Prefs_ getPrefs() {
        return prefs;
    }

    public void setPrefs(Prefs_ prefs) {
        this.prefs = prefs;
    }
    
    
}
