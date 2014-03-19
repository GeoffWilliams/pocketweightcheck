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

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.robolectric.Robolectric;

/**
 *
 * @author geoff
 */
public class BmiTest extends TestSupport {

    private Bmi_ bmi;
    private MainActivity_ mainActivity;

    private String[] bmiCategory;

    
    @Before
    public void setUp() {
        // disable autoload for testing
        Settings.setRefreshUi(false);
        mainActivity = Robolectric.buildActivity(MainActivity_.class).create().get();
        bmi = Bmi_.getInstance_(mainActivity);
        bmiCategory = getResourceStringArray(R.array.bmiCategory);
        assertNotNull(bmiCategory);
        assertTrue(bmiCategory.length > 0);
    }
    
    @Test
    public void testBmiCalculation() {
        // check BMI calculation gives correct calculation for input height and
        // weight

        double weight = 95.5;
        float height = 1.75f;
        double expectedResult = 31.1836734694;
        
        // BMI is determined by your weight in kg divided by your 
        // (height in metres)^2.
        Double calculated = bmi.calculateBmi(weight, height);
        assertNotNull(calculated);
        
        // there is some delta between expected and actual due to the precision
        // of the calculator I used...
        assertEquals(expectedResult, calculated.doubleValue(),  1e-7);
    }
    
    @Test
    public void testBmiClassification() {
        String category;
        // test each category returns correct string resource...
        
        //Very severely underweight 	less than 15 	less than 0.60
        category = bmi.lookupBmiCategory(14);
        assertEquals(bmiCategory[Bmi.VERY_SEVERELY_UNDERWEIGHT], category);
        
        //Severely underweight 	from 15.0 to 16.0 	from 0.60 to 0.64
        category = bmi.lookupBmiCategory(15.5);
        assertEquals(bmiCategory[Bmi.SEVERELY_UNDERWEIGHT], category);

        //Underweight 	from 16.0 to 18.5 	from 0.64 to 0.74
        category = bmi.lookupBmiCategory(17);
        assertEquals(bmiCategory[Bmi.UNDERWEIGHT], category);

        //Normal (healthy weight) 	from 18.5 to 25 	from 0.74 to 1.0
        category = bmi.lookupBmiCategory(19);
        assertEquals(bmiCategory[Bmi.NORMAL], category);

        //Overweight 	from 25 to 30 	from 1.0 to 1.2
        category = bmi.lookupBmiCategory(26);
        assertEquals(bmiCategory[Bmi.OVERWEIGHT], category);

        //Obese Class I (Moderately obese) 	from 30 to 35 	from 1.2 to 1.4
        category = bmi.lookupBmiCategory(32);
        assertEquals(bmiCategory[Bmi.OBESE_CLASS_I], category);

        //Obese Class II (Severely obese) 	from 35 to 40 	from 1.4 to 1.6
        category = bmi.lookupBmiCategory(36);
        assertEquals(bmiCategory[Bmi.OBESE_CLASS_II], category);

        //Obese Class III (Very severely obese) 	over 40 	over 1.6
        category = bmi.lookupBmiCategory(41);
        assertEquals(bmiCategory[Bmi.OBESE_CLASS_III], category);
        
    }
}
