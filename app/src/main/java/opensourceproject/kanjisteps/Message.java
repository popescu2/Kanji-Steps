/*
COPYRIGHT (c) 2015 Matthew Popescu
This is licensed under GNU General Public License
Detailed Licensing information can be found in the COPYING file
 */

package opensourceproject.kanjisteps;

/**
 * Created by Bob on 7/6/2015.
 */

import android.content.Context;
import android.widget.Toast;

public class Message {
    public static void message(Context context, String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
