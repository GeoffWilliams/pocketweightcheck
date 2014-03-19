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

package uk.me.geoffwilliams.pocketweightcheck.dao;

import android.database.sqlite.SQLiteDatabase;
import com.j256.ormlite.support.ConnectionSource;
import java.util.List;
import uk.me.geoffwilliams.pocketweightcheck.Bmi;
import uk.me.geoffwilliams.pocketweightcheck.DataChangeListener;
import uk.me.geoffwilliams.pocketweightcheck.Prefs_;
import uk.me.geoffwilliams.pocketweightcheck.Bmi_;
import uk.me.geoffwilliams.pocketweightcheck.PrefsWrapper;

/**
 *
 * @author geoff
 */
public interface DaoHelper {

    public void create(final Weight weight);

    public int delete(Weight weight);

    public RecordWeight getMaxWeight();

    public RecordWeight getMinWeight();

    public List<Weight> getWeightByDateAsc();

    public List<Weight> getWeightByDateDesc();

    public void onCreate(SQLiteDatabase sqld, ConnectionSource cs);

    public void onUpgrade(SQLiteDatabase sqld, ConnectionSource cs, int i, int i1);
    
    public void deleteAllData();
    
    public long getWeightCount();
    
    public void registerListener(DataChangeListener listener);
    
    public Weight getLatestWeight();
    
    public Double getBmi();
    
    public int getTrend();
    
    /**
     * Allow preference injection for testing
     * @param prefs 
     */
    public void setPrefsWrapper(PrefsWrapper prefs);

    /**
     * Allow bmi injection for testing
     */
    public void setBmi(Bmi_ bmi);
}
