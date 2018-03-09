package jonas.projektfono;

import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.github.clans.fab.FloatingActionButton;

import static android.graphics.Bitmap.createScaledBitmap;

public class BackgroundActivity extends AppCompatActivity {

    private WallpaperManager wallpaperManager;
    private int imgId, height, width;
    private Bitmap bitmapToScale, bitmapToSet;
    private String imgName;

    private DataBaseHelper dataBaseHandler;

    private final static int MY_REQUEST_CODE = 200;
    private PermissionHandler permissionHandler;
    private boolean permissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_background);

        // SLUT AUTOGENERERAT

        dataBaseHandler = new DataBaseHelper(this);
        dataBaseHandler.open();

        permissionHandler = new PermissionHandler();

        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();

        display.getSize(size);
        int widthContent = size.x;
        int heightContent = size.y;

        Bundle bundle = getIntent().getExtras();

        ImageView wallpaperContent = findViewById(R.id.wallpaperContent);

        if (bundle != null) {

            //Hämtar imgId med Bundle från tidigare aktivitet
            imgId = bundle.getInt("imgId");

            wallpaperContent.setPadding(0, 0, 0, 0);
            wallpaperContent.setBackgroundColor(Color.TRANSPARENT);

            Bitmap bitmapContent = BitmapFactory.decodeResource(getResources(), imgId);
            Bitmap bitmapContentRescaled;
            //I porträtt-läge
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                bitmapContentRescaled = createScaledBitmap(bitmapContent, widthContent + 155, heightContent + 275, true);
            } else {
                bitmapContentRescaled = createScaledBitmap(bitmapContent, widthContent / 2, heightContent, true);
            }

            //Sätter den aktiva bilden till den bilden man klickade på
            //Samt ändrar imgName till den specifika bilden

            switch (imgId) {
                case R.drawable.wp1:
                    wallpaperContent.setImageBitmap(bitmapContentRescaled);
                    imgName = "wp1";
                    break;

                case R.drawable.wp2:
                    wallpaperContent.setImageBitmap(bitmapContentRescaled);
                    imgName = "wp2";
                    break;

                case R.drawable.wp3:
                    wallpaperContent.setImageBitmap(bitmapContentRescaled);
                    imgName = "wp3";
                    break;

                case R.drawable.wp4:
                    wallpaperContent.setImageBitmap(bitmapContentRescaled);
                    imgName = "wp4";
                    break;

                case R.drawable.wp5:
                    wallpaperContent.setImageBitmap(bitmapContentRescaled);
                    imgName = "wp5";
                    break;

                case R.drawable.wp6:
                    wallpaperContent.setImageBitmap(bitmapContentRescaled);
                    imgName = "wp6";
                    break;
            }

        }

        FloatingActionButton fabDetails, fabSet, fabSave;

        wallpaperManager = WallpaperManager.getInstance(getApplicationContext());

        BitmapDrawable bitmapDrawable = (BitmapDrawable) wallpaperContent.getDrawable();
        bitmapToScale = bitmapDrawable.getBitmap();

        fabSet = findViewById(R.id.menu_apply);

        fabSet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //Gör om bilden till en Bitmap

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

                width = displayMetrics.widthPixels;

                height = displayMetrics.heightPixels;

                bitmapToSet = Bitmap.createScaledBitmap(bitmapToScale, width, height, false);

                wallpaperManager = WallpaperManager.getInstance(BackgroundActivity.this);

                try {

                    wallpaperManager.setBitmap(bitmapToSet);

                    wallpaperManager.suggestDesiredDimensions(width, height);

                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                }

            }
        });

        fabDetails = findViewById(R.id.menu_details);

        fabDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(BackgroundActivity.this);

                //Hämtar detaljer om den specifika bilden ifrån databasen
                String name = dataBaseHandler.readImgName(imgName);
                String category = dataBaseHandler.readImgCategory(imgName);
                String size = dataBaseHandler.readImgSize(imgName);

                builder.setTitle("Details");
                builder.setMessage("Name: " + name + System.getProperty("line.separator") + "Category: " + category + System.getProperty("line.separator") + "Size: " + size);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        fabSave = findViewById(R.id.menu_save);

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (permissionGranted) {

                    Bitmap bitmapToSave = BitmapFactory.decodeResource(getResources(), imgId);

                    saveImageToExternalStorage(bitmapToSave);

                } else
                    permissionHandler.checkPermission(BackgroundActivity.this, MY_REQUEST_CODE);

            }
        });

    }

    private void saveImageToExternalStorage(Bitmap image) {
        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/";

        try {
            File directory = new File(fullPath);
            if (!directory.exists()) {
                if (directory.mkdirs())
                    Toast.makeText(getApplicationContext(), "Directory created", Toast.LENGTH_SHORT).show();
            }

            OutputStream fOutStream;
            File file = new File(fullPath, imgName + ".png");
            if (file.createNewFile()) {
                fOutStream = new FileOutputStream(file);

                // 100 means no compression, the lower you go, the stronger the compression
                image.compress(Bitmap.CompressFormat.PNG, 100, fOutStream);
                fOutStream.flush();
                fOutStream.close();

                MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());

                Toast.makeText(getApplicationContext(), "Saved to " + directory, Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getApplicationContext(), "File already exists", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "FAILED", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case MY_REQUEST_CODE:
                boolean accepted = false;

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    accepted = true;

                if (accepted)
                    permissionGranted = true;

        }

    }
}