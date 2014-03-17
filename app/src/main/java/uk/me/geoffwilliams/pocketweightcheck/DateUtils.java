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

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import java.util.Date;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import org.androidannotations.annotations.res.StringRes;

/**
 *
 * @author geoff
 */
@EBean(scope = EBean.Scope.Singleton)
public class DateUtils {
    private static final String TAG = "pocketweightcheck.DateUtils";

    private Date date = new Date();

    @StringRes(R.string.msg_too_old)
    String msgTooOld;

    @StringRes(R.string.msg_future)
    String msgFuture;

    @RootContext
    Context context;

    public Date getDate() {
        return date;
    }

    public boolean setDate(Date date) {
        boolean status;
        String message = null;
        if (date.before(Settings.getOldestAllowable())) {
            // too old
            status = false;
            message = msgTooOld;
        } else if (date.after(new Date())) {
            // in future
            status = false;
            message = msgFuture;
        } else {
            Log.d(TAG, "Date accepted for processing:" + date.toString() );
            
            this.date = date;
            status = true;
        }

        if (message != null) {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
        }
        
        Log.d(TAG,"utils toast msg: " + message + " status: " + status);

        return status;

    }

}
