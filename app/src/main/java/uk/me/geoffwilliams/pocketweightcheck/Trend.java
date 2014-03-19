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

import android.util.Log;
import java.util.List;
import uk.me.geoffwilliams.pocketweightcheck.dao.Weight;
/**
 *
 * @author geoff
 */
public class Trend {
    public static final String TAG = "pocketweightcheck.Trend";
    
    // Important! these constants also represent index in a resource array in
    // in Strings.xml
    public static final int TREND_ERROR = 0;
    public static final int TREND_CONVERGING = 1;
    public static final int TREND_STABLE = 2;
    public static final int TREND_DIVERGING = 3;
    
    public int calculateTrend(float target, List<Weight> weights) {
        int status;
         // plot a straight line from earliest trend to latest trend ...
        if (weights.size() > 1 && target > 0) {
            int firstEntry = Math.max(0, weights.size() - 1 - Settings.getTrendSamples());
            double firstTrend = weights.get(firstEntry).getTrend();
            
            // always the latest
            double lastTrend = weights.get(weights.size() - 1).getTrend();
            
            Log.d(TAG, "earliest weight: " + firstTrend );
            Log.d(TAG, "latest weight: " + lastTrend);

            double t1 = Math.abs(target - firstTrend);
            double t2 = Math.abs(target - lastTrend);
            
            Log.d(TAG, "******");
            Log.d(TAG, "target: " + target);
            Log.d(TAG, "t1: " + t1);
            Log.d(TAG, "t2: " + t2);
            Log.d(TAG, "******");
            
            if (Math.abs(t1 - t2) <= Settings.getStableTrendTolerance()) {
                status = TREND_STABLE;    
            } else if (t1>t2) {
                status = TREND_CONVERGING;
            } else if (t1<t2) {
                status = TREND_DIVERGING;
            } else {
                // wtf?!
                status = TREND_ERROR;
                Log.e(TAG, "unsupported trend wtf!");
            }
            Log.d(TAG, "decision: " + status);
        } else {
            Log.d(TAG, "not enough weights to calculate trend");
            status = TREND_ERROR;
        }


        
        return status;
    }
}
