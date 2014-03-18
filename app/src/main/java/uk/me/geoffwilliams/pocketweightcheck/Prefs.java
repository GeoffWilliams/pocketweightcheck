/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.me.geoffwilliams.pocketweightcheck;

import org.androidannotations.annotations.sharedpreferences.DefaultFloat;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 *
 * @author geoff
 */
@SharedPref(value = SharedPref.Scope.UNIQUE)
public interface Prefs {
    
    float targetWeight();
    
    float height();
}
