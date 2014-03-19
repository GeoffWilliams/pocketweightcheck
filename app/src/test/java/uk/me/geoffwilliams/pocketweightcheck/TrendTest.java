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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import uk.me.geoffwilliams.pocketweightcheck.dao.Weight;
import static org.junit.Assert.*;

/**
 *
 * @author geoff
 */
public class TrendTest extends TestSupport {
    
    private Trend trend = new Trend();
    private float target = 50.0f;
    
    @Test
    public void testTrendNoData() {
        List weights = new ArrayList<Weight>();
        int status = trend.calculateTrend(target, weights);
        assertEquals(Trend.TREND_ERROR, status);
    }
    
    @Test
    public void testTrendStable() {
        List weights = new ArrayList<Weight>();
        weights.add(new Weight(new Date(), -1d, 88.8));
        weights.add(new Weight(new Date(), -1d, 88.7));
        int status = trend.calculateTrend(target, weights);
        assertEquals(Trend.TREND_STABLE, status);
        
    }
    
    @Test
    public void testTrendDiverging() {
        List weights = new ArrayList<Weight>();
        weights.add(new Weight(new Date(), -1d, 51d));
        weights.add(new Weight(new Date(), -1d, 52d));
        int status = trend.calculateTrend(target, weights);
        assertEquals(Trend.TREND_DIVERGING, status);
        
    }
    
    @Test
    public void testTrendConverging() {
        List weights = new ArrayList<Weight>();
        weights.add(new Weight(new Date(), -1d, 52d));
        weights.add(new Weight(new Date(), -1d, 51d));
        int status = trend.calculateTrend(target, weights);
        assertEquals(Trend.TREND_CONVERGING, status);
    }
}
