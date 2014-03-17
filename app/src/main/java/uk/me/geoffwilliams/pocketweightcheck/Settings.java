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

/**
 *
 * @author geoff
 */
public class Settings {

    /**
     * Automatically load data in activities (used for testing)
     */
    private static boolean loadData = true;
    
    /**
     * Automatically prompt for data entry (used for testing)
     */
    private static boolean promptForDataEntry = true;

    /**
     * Maximum weight to accept (KG)
     */
    private static int maxAllowedWeight = 140;
    
    /**
     * Minimum weight to accept (KG)
     */
    private static int minAllowedWeight = 40;
    
    /*
     * Maximum age to accept for entered data (days) 
     */
    private static int maxSampleAge = 30;
    
    /**
     * Minimum amount of points to allow graph to display
     */
    private static int graphMinDataPoints = 2;
    
    /**
     * maximum sample age expressed as a date
     */
    private static final Date oldestAllowable = new Date(
            new Date().getTime() - ((long) 60 * 60 * 24 * 1000 * maxSampleAge));
    
    public static boolean isLoadData() {
        return loadData;
    }

    public static void setLoadData(boolean loadData) {
        Settings.loadData = loadData;
    }

    public static int getMaxAllowedWeight() {
        return maxAllowedWeight;
    }

    public static void setMaxAllowedWeight(int maxAllowedWeight) {
        Settings.maxAllowedWeight = maxAllowedWeight;
    }

    public static int getMinAllowedWeight() {
        return minAllowedWeight;
    }

    public static void setMinAllowedWeight(int minAllowedWeight) {
        Settings.minAllowedWeight = minAllowedWeight;
    }
    
    public static Date getOldestAllowable() {
        return oldestAllowable;
    }
    
    public static int getMaxSampleAge() {
        return maxSampleAge;
    }
    
    public static void setMaxSampleAge(int days) {
        Settings.maxSampleAge = days;
    }

    public static boolean isPromptForDataEntry() {
        return promptForDataEntry;
    }

    public static void setPromptForDataEntry(boolean promptForDataEntry) {
        Settings.promptForDataEntry = promptForDataEntry;
    }

    public static int getGraphMinDataPoints() {
        return graphMinDataPoints;
    }

    

}
