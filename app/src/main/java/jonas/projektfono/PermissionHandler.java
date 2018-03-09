package jonas.projektfono;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

class PermissionHandler {

    void checkPermission(final BackgroundActivity activity, final int requestCode) {

        String[] PERMISSION = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (!alreadyGranted(activity, PERMISSION)) {

            boolean userDenied = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (userDenied) {

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                builder.setTitle("Permission required");

                builder.setMessage("Permission to access the external storage is required to save an image");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        makeRequest(activity, requestCode);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            } else {

                makeRequest(activity, requestCode);

            }
        }
    }

    @TargetApi(23)
    private void makeRequest(Activity activity, int requestCode) {

        String[] perms = {"android.permission.READ_EXTERNAL_STORAGE"};

        activity.requestPermissions(perms, requestCode);

    }

    private boolean alreadyGranted(Activity context, String... permissions) {

        boolean status = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {

            for (String permission : permissions) {

                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {

                    status = false;

                }

            }

        }

        return status;

    }

}