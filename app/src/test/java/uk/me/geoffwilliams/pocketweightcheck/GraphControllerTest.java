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
import java.util.Date;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import uk.me.geoffwilliams.pocketweightcheck.dao.DaoHelper;
import uk.me.geoffwilliams.pocketweightcheck.dao.MockDaoHelper;
import uk.me.geoffwilliams.pocketweightcheck.dao.Weight;
/**
 *
 * @author geoff
 */
public class GraphControllerTest extends TestSupport {
    GraphController graphController;
    MainActivity_ mainActivity;
    
    @Before
    public void setUp() {
        Settings.setLoadData(false);
        mainActivity =  (MainActivity_) Robolectric.buildActivity(MainActivity_.class)
                .create()
                .start()
                .resume()
                .visible()
                .get();
        mainActivity.daoHelper = new MockDaoHelper();
        Settings.setLoadData(true);
        graphController = new GraphController();
        graphController.setContext(mainActivity);
    }
    
    @Test
    public void testGraphController() {
        DaoHelper daoHelper = new MockDaoHelper();
        // a chartcontroller with no data will not give a graph yet
        assertNull(graphController.getChart());
        List<Weight> weights = daoHelper.getWeightByDateAsc();
        
        // set some data, check we get a chart back
        graphController.updateGraph(weights);
        assertNotNull(graphController.getChart());
    }
     
}
