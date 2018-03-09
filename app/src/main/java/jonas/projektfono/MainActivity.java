package jonas.projektfono;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import static android.graphics.Bitmap.createScaledBitmap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DataBaseHelper dataBaseHandler;
    private int width, height, rows, cols, newWidth, newHeight;
    private TableLayout tableLayout;
    private boolean oneColumn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // AUTOGENERERAT

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_abstract));

        // SLUT AUTOGENERERAT

        dataBaseHandler = new DataBaseHelper(this);
        dataBaseHandler.open();

        String[] images = new String[]{"wp1", "wp2", "wp3", "wp4", "wp5", "wp6"};

        if (savedInstanceState == null) {

            //Sätter in bildinformation i databasen
            for (int i = 0; i < 6; i++) {

                dataBaseHandler.insertImgData(images[i]);

            }

        } else
            oneColumn = savedInstanceState.getBoolean("columnState");

        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();

        display.getSize(size);
        width = size.x;
        height = size.y;

        if (!oneColumn) {

            rows = 3;
            cols = 2;
            newWidth = width / 2;
            newHeight = height / 3;

        } else {

            rows = 6;
            cols = 1;
            newWidth = width;
            newHeight = height;
        }


        tableLayout = findViewById(R.id.table);

        buildTableAbstract(tableLayout, newWidth, newHeight, rows, cols);


    }

    private void buildTableAbstract(TableLayout tableLayout, int width, int height, int rows, int cols) {


        int imgId = R.drawable.wp1;

        //Hämtar info ifrån databasen
        Cursor cursor = dataBaseHandler.readFromDB();

        //I porträtt-läge
        if (!(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)) {

            //rows = 3;
            //rows = cursor.getCount() / 2;
            //cols = 2;

            //width = width / 2;
            //height = height / 3;
            Display display = getWindowManager().getDefaultDisplay();

            Point size = new Point();

            display.getSize(size);
            width = size.x;
            height = size.y;

            rows = cursor.getCount() / 6;
            cols = cursor.getCount();

            //cols = 6;

            width = width / 3;

        }

        for (int i = 0; i < rows; i++) {

            TableRow row = new TableRow(this);

            for (int j = 0; j < cols; j++) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgId);
                final Bitmap bitmapRescaled = createScaledBitmap(bitmap, width, height, false);

                final ImageButton imageButton = new ImageButton(this);

                imageButton.setImageBitmap(bitmapRescaled);

                imageButton.setPadding(0, 0, 0, 0);

                final int finalImgId = imgId;
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(MainActivity.this, BackgroundActivity.class);
                        intent.putExtra("imgId", finalImgId);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);

                    }
                });

                imgId = imgId + 1;

                row.addView(imageButton);

            }

            tableLayout.addView(row);

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Visar bara menyn(action button) i porträtt-läge
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_columns) {

                final String[] options = {"1", "2"};

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Columns");

                int checkedItem;
                if (!oneColumn)
                    checkedItem = 1;
                else
                    checkedItem = 0;

                builder.setSingleChoiceItems(options, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {

                        switch (item) {

                            case 0:

                                oneColumn = true;
                                break;

                            case 1:

                                oneColumn = false;
                                break;

                        }

                    }
                });

                builder.setPositiveButton("CHOOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();

                        tableLayout.removeAllViews();
                        if (!oneColumn) {

                            rows = 3;
                            cols = 2;
                            newWidth = width / 2;
                            newHeight = height / 3;

                        } else {

                            rows = 6;
                            cols = 1;
                            newWidth = width;
                            newHeight = height;
                        }
                        buildTableAbstract(tableLayout, newWidth, newHeight, rows, cols);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        int id = item.getItemId();

        if (id == R.id.nav_help) {

            Intent intent = new Intent(MainActivity.this, HelpActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_abstract)
            item.setChecked(true);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataBaseHandler.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataBaseHandler.close();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("columnState", oneColumn);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        oneColumn = savedInstanceState.getBoolean("columnState");
    }
}