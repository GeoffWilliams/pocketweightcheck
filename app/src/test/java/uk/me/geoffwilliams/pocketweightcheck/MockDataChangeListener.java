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
public class MockDataChangeListener implements DataChangeListener {
    
    private boolean updated = false;

    @Override
    public void onDataChanged() {
        updated = true;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }
    
    
    
}
