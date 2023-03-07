package com.example.todelperm;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_CODE = 200;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = findViewById(R.id.mainlayout);

        Button check_permission = findViewById(R.id.check_permission);
        Button request_permission = findViewById(R.id.request_permission);
        check_permission.setOnClickListener(this);
        request_permission.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.check_permission:
                if (checkPermission()) {
                    Snackbar.make(v, "Permission already granted.", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar snackbar = Snackbar
                            .make(v, "Please request permission.", Snackbar.LENGTH_LONG)
                            .setAction("REQUEST", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // Show another Snackbar.
                                    Snackbar snackbar1 = Snackbar.make(view, "Allow permissions", Snackbar.LENGTH_SHORT);
                                    snackbar1.show();
                                    // Lancement requestPermission();
                                    requestPermission();
                                }
                            });
                    snackbar.show();
                }
                break;
            case R.id.request_permission:
                if (!checkPermission()) {
                    requestPermission();
                } else {
                    Snackbar.make(v, "Permission already granted.", Snackbar.LENGTH_LONG).show();
                }
                break;
        }

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, CAMERA}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && cameraAccepted) {
                        Snackbar
                                .make(view, "Permission Granted, Now you can access location data and camera.", Snackbar.LENGTH_LONG)
                                .show();
                    } else {
                        // La snackbar avec son template perso pour affiche run icône
//                        Snackbar.make(view, "Permission Denied, You cannot access location data and camera.", Snackbar.LENGTH_LONG).show();

                        final Snackbar snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG);
                        // inflate the custom_snackbar_view created previously
                        View customSnackView = getLayoutInflater().inflate(R.layout.snackbar_permisson_denied, null);
                        // set the background of the default snackbar as transparent
                        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
                        // now change the layout of the snackbar
                        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
                        // set padding of the all corners as 0
                        snackbarLayout.setPadding(0, 0, 0, 0);
                        // Text for the message
                        TextView tvMessageSnack = customSnackView.findViewById(R.id.tv_message_snackbar);
                        tvMessageSnack.setText("Please request permission.");
                        // register the button from the custom_snackbar_view layout file
                        Button btnAllow = customSnackView.findViewById(R.id.btn_allow);
                        btnAllow.setText("REQUEST");
                        // now handle the same button with onClickListener
                        btnAllow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestPermission();
                                snackbar.dismiss();
                            }
                        });

                        // add the custom snack bar layout to snackbar layout
                        snackbarLayout.addView(customSnackView, 0);

                        snackbar.show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}
