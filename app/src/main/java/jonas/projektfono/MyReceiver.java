package jonas.projektfono;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (action != null) {

            if (action.equals("android.intent.action.WALLPAPER_CHANGED")){
                Toast.makeText(context, "Wallpaper changed", Toast.LENGTH_SHORT).show();
            }
        }

    }
}