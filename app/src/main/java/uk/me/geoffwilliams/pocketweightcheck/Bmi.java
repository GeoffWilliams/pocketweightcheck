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
import org.androidannotations.annotations.res.StringArrayRes;
import uk.me.geoffwilliams.pocketweightcheck.dao.Weight;
/**
 *
 * @author geoff
 */
@EBean(scope = EBean.Scope.Singleton)
public class Bmi {
    
    @StringArrayRes
    String[] bmiCategory;

    // keys into bmiCategory array...
    public final static int VERY_SEVERELY_UNDERWEIGHT   = 0;
    public final static int SEVERELY_UNDERWEIGHT        = 1;
    public final static int UNDERWEIGHT                 = 2;
    public final static int NORMAL                      = 3;
    public final static int OVERWEIGHT                  = 4;
    public final static int OBESE_CLASS_I               = 5;
    public final static int OBESE_CLASS_II              = 6;
    public final static int OBESE_CLASS_III             = 7;

    
    /**
     * Calculate the BMI for given height and weight
     * @param weight
     * @param height
     * @return 
     */
    public Double calculateBmi(double weight, float height) {
        return weight / (height * height);
    }
    
    public String lookupBmiCategory(double bmi) {
        String category;
        // BMI categories -- http://en.wikipedia.org/wiki/Body_mass_index
        //Category 	BMI range – kg/m2 	BMI Prime
        
        if (bmi < 15) {
            //Very severely underweight 	less than 15 	less than 0.60
            category = bmiCategory[VERY_SEVERELY_UNDERWEIGHT];
        } else if (bmi >= 15 && bmi < 16) {
            //Severely underweight 	from 15.0 to 16.0 	from 0.60 to 0.64
            category = bmiCategory[SEVERELY_UNDERWEIGHT];
        } else if (bmi >= 16 && bmi < 18.5) {
            //Underweight 	from 16.0 to 18.5 	from 0.64 to 0.74
            category = bmiCategory[UNDERWEIGHT];
        } else if (bmi >= 18.5 && bmi < 25) {
            //Normal (healthy weight) 	from 18.5 to 25 	from 0.74 to 1.0
            category = bmiCategory[NORMAL];
        } else if (bmi >= 25 && bmi < 30) {
            //Overweight 	from 25 to 30 	from 1.0 to 1.2
            category = bmiCategory[OVERWEIGHT];
        } else if (bmi >= 30 && bmi < 35) {
            //Obese Class I (Moderately obese) 	from 30 to 35 	from 1.2 to 1.4
            category = bmiCategory[OBESE_CLASS_I];
        } else if (bmi >= 35 && bmi < 40) {
            //Obese Class II (Severely obese) 	from 35 to 40 	from 1.4 to 1.6
            category = bmiCategory[OBESE_CLASS_II];
        } else if (bmi > 40) {
            //Obese Class III (Very severely obese) 	over 40 	over 1.6
            category = bmiCategory[OBESE_CLASS_III];
        } else {
            category = "ERROR - unsupported BMI";
        }
        return category;
    }
}
