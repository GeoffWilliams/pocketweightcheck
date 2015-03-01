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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
     * Automatically refresh the UI after new data (for testing)
     */
    private static boolean refreshUi = true;

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
     * Maximum age to keep detailed data (days)
     */
    private static int maxDetailAge = 60;
    
    /**
     * Minimum amount of points to allow graph to display
     */
    private static int graphMinDataPoints = 2;
    
    /**
     * constant used in calculation of trend data
     */
    private static double smoothingConstant = 0.1d;

    /**
     * smoothing constant used for archived data (makes it almost exactly follow entered weights)
     */
    private static double smoothingConstantArchived = 0.9d;
    /**
     * Argument used for String.format to round floats
     */
    private static String decimalFormat = "%.2f";
    
    /**
     * Tolerance to exceed before a trend is deemed to be converging/diverging
     */
    private static float stableTrendTolerance = 0.25f;
    
    /**
     * How many SAMPLES to calculate trend over
     */
    private static int trendSamples = 5;
    
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


    /**
     * Maximum sample age to accept (as timestamp)
     * @return
     */
    public static Date getOldestAllowable() {
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, - maxSampleAge);
        return cal.getTime();
    }

    /**
     * Data older then this timestamp should be archived
     * @return
     */
    public static Date archiveAfter() {
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, - maxDetailAge);
        return cal.getTime();
    }

    public static int getMaxDetailAge() {
        return maxDetailAge;
    }

    public static void setMaxDetailAge(int maxDetailAge) {
        Settings.maxDetailAge = maxDetailAge;
    }


    /**
     * Maximum sample age to accept (days)
     * @return
     */
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

    public static boolean isRefreshUi() {
        return refreshUi;
    }

    public static void setRefreshUi(boolean refreshUi) {
        Settings.refreshUi = refreshUi;
    }

    public static double getSmoothingConstant() {
        return smoothingConstant;
    }

    public static void setSmoothingConstant(double smoothingConstant) {
        Settings.smoothingConstant = smoothingConstant;
    }

    public static String getDecimalFormat() {
        return decimalFormat;
    }

    public static void setDecimalFormat(String decimalFormat) {
        Settings.decimalFormat = decimalFormat;
    }

    public static float getStableTrendTolerance() {
        return stableTrendTolerance;
    }

    public static void setStableTrendTolerance(float stableTrendTolerance) {
        Settings.stableTrendTolerance = stableTrendTolerance;
    }

    public static int getTrendSamples() {
        return trendSamples;
    }

    public static void setTrendSamples(int trendSamples) {
        Settings.trendSamples = trendSamples;
    }

    public static double getSmoothingConstantArchived() {
        return smoothingConstantArchived;
    }

    public static void setSmoothingConstantArchived(double smoothingConstantArchived) {
        Settings.smoothingConstantArchived = smoothingConstantArchived;
    }
}
