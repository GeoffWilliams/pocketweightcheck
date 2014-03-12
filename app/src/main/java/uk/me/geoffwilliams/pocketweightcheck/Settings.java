/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.me.geoffwilliams.pocketweightcheck;

/**
 *
 * @author geoff
 */
public class Settings {
    private static boolean production = false;

    public static boolean isProduction() {
        return production;
    }
    
    public static void setProduction(boolean production) {
        Settings.production = production;
    }
}
