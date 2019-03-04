package com.example.sharefiles;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.model.FileListItem;
import com.github.angads25.filepicker.model.MarkedItemList;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.tml.sharethem.receiver.ReceiverActivity;
import com.tml.sharethem.sender.SHAREthemActivity;
import com.tml.sharethem.sender.SHAREthemService;
import com.tml.sharethem.utils.HotspotControl;
import com.tml.sharethem.utils.Utils;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.AudioPickActivity;
import com.vincent.filepicker.activity.ImagePickActivity;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.activity.VideoPickActivity;
import com.vincent.filepicker.filter.entity.AudioFile;
import com.vincent.filepicker.filter.entity.ImageFile;
import com.vincent.filepicker.filter.entity.NormalFile;
import com.vincent.filepicker.filter.entity.VideoFile;


import java.io.File;
import java.util.ArrayList;

import static android.Manifest.permission.CHANGE_NETWORK_STATE;
import static android.os.Environment.DIRECTORY_DCIM;
import static com.tml.sharethem.utils.Utils.DEFAULT_PORT_OREO;

public class MainScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DialogSelectionListener callbacks;
    FilePickerDialog dialog;
    DatabaseHelper db;
    AdView banneradView,topbanner;
    InterstitialAd interstitialAd;
    Button webbtn;
    ImageView sendbtn, receivebtn, history, invite;
    private static final int PERMISSION_REQUEST_CODE = 200;
    Activity mActivity;
    String[] permissions = {CHANGE_NETWORK_STATE, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.ACCESS_WIFI_STATE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialization();
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else {

                handleSendImage(intent);
                // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            Log.d("type","TYPE_IS"+type);
            handleSendMultipleImages(intent); // Handle multiple images being sent

        } else {
            // Handle other intents, such as being started from the home screen
        }

    }

    //Initiallizing all view....

    private void initialization() {
        mActivity = MainScreen.this;
        MobileAds.initialize(mActivity, getString(R.string.ApAdId));
        AdRequest adRequest=new AdRequest.Builder().build();
        interstitialAd =new InterstitialAd(mActivity);
        interstitialAd.setAdUnitId(getString(R.string.interstitialunitid));
        interstitialAd.loadAd(adRequest);
        banneradView=findViewById(R.id.bottomad);
        topbanner=findViewById(R.id.L1topAd);
        banneradView.loadAd(adRequest);
        topbanner.loadAd(adRequest);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new DatabaseHelper(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        sendbtn = findViewById(R.id.sendbtn);
        history = findViewById(R.id.historybtn);
        invite = findViewById(R.id.invitebtn);
        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = getPackageName();
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_TEXT, "Check out this awasome Application  " + url);
                email.setType("text/plain");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                interstitialAd.setAdListener(new AdListener(){
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        Intent intent = new Intent(MainScreen.this, HistoryActivity.class);
                        startActivity(intent);
                    }
                });
                if (interstitialAd.isLoaded()|| interstitialAd.isLoading()){
                    interstitialAd.show();
                }else {
                    Intent intent = new Intent(MainScreen.this, HistoryActivity.class);
                    startActivity(intent);
                }

            }
        });
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interstitialAd.setAdListener(new AdListener(){
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        //SendMethod();
                        ViewDialogSelection alert = new ViewDialogSelection();
                        alert.showDialog(MainScreen.this);
                    }
                });
                if (interstitialAd.isLoaded()|| interstitialAd.isLoading()){
                    interstitialAd.show();
                }else {
                    //SendMethod();
                    ViewDialogSelection alert = new ViewDialogSelection();
                    alert.showDialog(MainScreen.this);
                }
            }
        });
        webbtn = findViewById(R.id.Webreceivebtn);
        webbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*                AdRequest adRequestb=new AdRequest.Builder().build();
                interstitialAd.loadAd(adRequestb);*/
                interstitialAd.setAdListener(new AdListener(){
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        OpenWebBrowser();
                    }
                });
                if (interstitialAd.isLoaded()|| interstitialAd.isLoading()){
                    interstitialAd.show();
                }else {
                    OpenWebBrowser();
                }

            }
        });
        receivebtn = findViewById(R.id.receivebtn);
        receivebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HotspotControl hotspotControl = HotspotControl.getInstance(getApplicationContext());
                if (null != hotspotControl && hotspotControl.isEnabled()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainScreen.this);
                    builder.setMessage("Sender(Hotspot) mode is active. Please disable it to proceed with Receiver mode");
                    builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.show();
                    return;
                }
/*                AdRequest adRequestr=new AdRequest.Builder().build();
                interstitialAd.loadAd(adRequestr);*/
                interstitialAd.setAdListener(new AdListener(){
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        startActivity(new Intent(getApplicationContext(), ReceiverActivity.class));
                    }
                });
                if (interstitialAd.isLoaded()|| interstitialAd.isLoading()){
                    interstitialAd.show();
                }else {
                    startActivity(new Intent(getApplicationContext(), ReceiverActivity.class));
                }


            }
        });
    }

    private void OpenWebBrowser() {
        String url = "http://192.168.43.1:52287";
        try {
            Intent i = new Intent("android.intent.action.MAIN");
            i.setComponent(ComponentName.unflattenFromString("com.android.chrome/com.android.chrome.Main"));
            i.addCategory("android.intent.category.LAUNCHER");
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            // Chrome is not installed
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(i);
        }
    }

    //onSend Button Click method called
    private void SendMethod() {
        if (Utils.isShareServiceRunning(getApplicationContext())) {
            startActivity(new Intent(getApplicationContext(), SHAREthemActivity.class));
            return;
        }

        //PoP_Up Dialog for What u Want to Send....
        AlertDialog.Builder builder = new AlertDialog.Builder(MainScreen.this);
        builder.setTitle("Title");
        builder.setItems(new CharSequence[]
                        {"Images", "Videos", "Documents", "Audios"},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0:
                                Intent intent1 = new Intent(MainScreen.this, ImagePickActivity.class);
                                // intent1.putExtra(IS_NEED_CAMERA, true);
                                intent1.putExtra(Constant.MAX_NUMBER, 50);
                                startActivityForResult(intent1, Constant.REQUEST_CODE_PICK_IMAGE);
                                break;
                            case 1:
                                Intent intent2 = new Intent(MainScreen.this, VideoPickActivity.class);
                                // intent2.putExtra(IS_NEED_CAMERA, true);
                                intent2.putExtra(Constant.MAX_NUMBER, 30);
                                startActivityForResult(intent2, Constant.REQUEST_CODE_PICK_VIDEO);
                                break;
                            case 2:
                                Intent intent4 = new Intent(MainScreen.this, NormalFilePickActivity.class);
                                intent4.putExtra(Constant.MAX_NUMBER, 50);
                                //intent4.putExtra(IS_NEED_FOLDER_LIST, true);
                                intent4.putExtra(NormalFilePickActivity.SUFFIX,
                                        new String[]{"xlsx", "xls", "doc", "dOcX", "ppt", ".pptx", "pdf"});
                                startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
                                break;
                            case 3:
                                Intent intent3 = new Intent(MainScreen.this, AudioPickActivity.class);
                                //   intent3.putExtra(IS_NEED_RECORDER, true);
                                intent3.putExtra(Constant.MAX_NUMBER, 50);
                                // intent3.putExtra(IS_NEED_FOLDER_LIST, true);
                                startActivityForResult(intent3, Constant.REQUEST_CODE_PICK_AUDIO);
                                break;
                        }
                    }
                });
        builder.create().show();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            ViewDialog alert = new ViewDialog();
            alert.showDialog(MainScreen.this);
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            // Handle the camera action
        } else if (id == R.id.nav_history) {
            Intent intent = new Intent(MainScreen.this, HistoryActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_intro) {
            Intent intent = new Intent(MainScreen.this, introduction.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            String url = getPackageName();
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_TEXT, "Check out this awasome Application  " + url);
            email.setType("text/plain");
            startActivity(Intent.createChooser(email, "Choose an Email client :"));

        } else if (id == R.id.nav_exit) {
            ViewDialog alert = new ViewDialog();
            alert.showDialog(MainScreen.this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openPlayStore() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + "com.android.chrome")));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    //Add this method to show Dialog when the required permission has been granted to the app.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (dialog != null) {   //Show dialog if the read permission has been granted.
                        dialog.show();
                    }
                } else {
                    //Permission has not been granted. Notify the user.
                    Toast.makeText(this, "Permission is Required for getting list of files", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //After Choosing the Files this method is called......
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constant.REQUEST_CODE_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    ArrayList<ImageFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                    StringBuilder builder = new StringBuilder();
                    MarkedItemList.clearSelectionList();
                    for (ImageFile file : list) {
                        String path = file.getPath();
                        Log.d("LOVE", "LOVEIS" + path);
                        FileListItem parent = new FileListItem();
                        parent.setLocation(path);
                        createNote(file.getPath());
                        MarkedItemList.addSelectedItem(parent);
                        builder.append(path + "\n");
                    }
                    String file[] = MarkedItemList.getSelectedPaths();

                    //  Toast.makeText(this, builder.toString(), Toast.LENGTH_SHORT).show();
                    this.setDialogSelectionListener(new DialogSelectionListener() {
                        @Override
                        public void onSelectedFilePaths(String[] files) {
                            if (null == files || files.length == 0) {
                                Toast.makeText(MainScreen.this, "Select at least one file to start Share Mode", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Intent intent = new Intent(getApplicationContext(), SHAREthemActivity.class);
                            intent.putExtra(SHAREthemService.EXTRA_FILE_PATHS, files);

                            intent.putExtra(SHAREthemService.EXTRA_PORT, DEFAULT_PORT_OREO);

                            intent.putExtra(SHAREthemService.EXTRA_SENDER_NAME, "Sri");
                            startActivity(intent);
                        }
                    });


                    if (callbacks != null) {
                        callbacks.onSelectedFilePaths(file);
                    }
                    //mTvResult.setText(builder.toString());
                }
                break;
            case Constant.REQUEST_CODE_PICK_VIDEO:
                if (resultCode == RESULT_OK) {
                    ArrayList<VideoFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_VIDEO);
                    StringBuilder builder = new StringBuilder();
                    MarkedItemList.clearSelectionList();
                    for (VideoFile file : list) {
                        String path = file.getPath();
                        FileListItem parent = new FileListItem();
                        parent.setLocation(path);
                        createNote(file.getPath());
                        MarkedItemList.addSelectedItem(parent);
                        builder.append(path + "\n");
                    }
                    String file[] = MarkedItemList.getSelectedPaths();

                   // Toast.makeText(this, builder.toString(), Toast.LENGTH_SHORT).show();
                    this.setDialogSelectionListener(new DialogSelectionListener() {
                        @Override
                        public void onSelectedFilePaths(String[] files) {
                            if (null == files || files.length == 0) {
                                Toast.makeText(MainScreen.this, "Select at least one file to start Share Mode", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Intent intent = new Intent(getApplicationContext(), SHAREthemActivity.class);
                            intent.putExtra(SHAREthemService.EXTRA_FILE_PATHS, files);

                            intent.putExtra(SHAREthemService.EXTRA_PORT, DEFAULT_PORT_OREO);

                            intent.putExtra(SHAREthemService.EXTRA_SENDER_NAME, "Sri");
                            startActivity(intent);
                        }
                    });


                    if (callbacks != null) {
                        callbacks.onSelectedFilePaths(file);
                    }

                }
                break;
            case Constant.REQUEST_CODE_PICK_AUDIO:
                if (resultCode == RESULT_OK) {
                    ArrayList<AudioFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_AUDIO);
                    StringBuilder builder = new StringBuilder();
                    MarkedItemList.clearSelectionList();
                    for (AudioFile file : list) {
                        String path = file.getPath();
                        FileListItem parent = new FileListItem();
                        parent.setLocation(path);
                        createNote(file.getPath());
                        MarkedItemList.addSelectedItem(parent);
                        builder.append(path + "\n");
                    }
                    String file[] = MarkedItemList.getSelectedPaths();

                    //Toast.makeText(this, builder.toString(), Toast.LENGTH_SHORT).show();
                    this.setDialogSelectionListener(new DialogSelectionListener() {
                        @Override
                        public void onSelectedFilePaths(String[] files) {
                            if (null == files || files.length == 0) {
                                Toast.makeText(MainScreen.this, "Select at least one file to start Share Mode", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Intent intent = new Intent(getApplicationContext(), SHAREthemActivity.class);
                            intent.putExtra(SHAREthemService.EXTRA_FILE_PATHS, files);

                            intent.putExtra(SHAREthemService.EXTRA_PORT, DEFAULT_PORT_OREO);

                            intent.putExtra(SHAREthemService.EXTRA_SENDER_NAME, "Sri");
                            startActivity(intent);
                        }
                    });


                    if (callbacks != null) {
                        callbacks.onSelectedFilePaths(file);
                    }
                    // mTvResult.setText(builder.toString());
                }
                break;
            case Constant.REQUEST_CODE_PICK_FILE:
                if (resultCode == RESULT_OK) {
                    ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                    StringBuilder builder = new StringBuilder();
                    MarkedItemList.clearSelectionList();
                    for (NormalFile file : list) {
                        String path = file.getPath();
                        FileListItem parent = new FileListItem();
                        parent.setLocation(path);
                        createNote(file.getPath());
                        MarkedItemList.addSelectedItem(parent);
                        builder.append(path + "\n");
                    }
                    String file[] = MarkedItemList.getSelectedPaths();

                   // Toast.makeText(this, builder.toString(), Toast.LENGTH_SHORT).show();
                    this.setDialogSelectionListener(new DialogSelectionListener() {
                        @Override
                        public void onSelectedFilePaths(String[] files) {
                            if (null == files || files.length == 0) {
                                Toast.makeText(MainScreen.this, "Select at least one file to start Share Mode", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Intent intent = new Intent(getApplicationContext(), SHAREthemActivity.class);
                            intent.putExtra(SHAREthemService.EXTRA_FILE_PATHS, files);

                            intent.putExtra(SHAREthemService.EXTRA_PORT, DEFAULT_PORT_OREO);

                            intent.putExtra(SHAREthemService.EXTRA_SENDER_NAME, "Sri");
                            startActivity(intent);
                        }
                    });


                    if (callbacks != null) {
                        callbacks.onSelectedFilePaths(file);
                    }
                    //mTvResult.setText(builder.toString());
                }
                break;

        }
    }

    public void setDialogSelectionListener(DialogSelectionListener callbacks) {
        this.callbacks = callbacks;
    }

    public class ViewDialog {

        public void showDialog(Activity activity) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.custom_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            FrameLayout mDialogNo = dialog.findViewById(R.id.frmNo);
            mDialogNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    openPlayStore();
                }
            });

            FrameLayout mDialogOk = dialog.findViewById(R.id.frmOk);
            mDialogOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                    finish();
                }
            });

            dialog.show();
        }
    }


    public class ViewDialogSelection {

        public void showDialog(Activity activity) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.custom_dialog_send);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            ImageButton images = dialog.findViewById(R.id.images);
            images.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    Intent intent1 = new Intent(MainScreen.this, ImagePickActivity.class);
                    // intent1.putExtra(IS_NEED_CAMERA, true);
                    intent1.putExtra(Constant.MAX_NUMBER, 50);
                    startActivityForResult(intent1, Constant.REQUEST_CODE_PICK_IMAGE);
                }
            });
            ImageButton videos = dialog.findViewById(R.id.videos);
            videos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    Intent intent2 = new Intent(MainScreen.this, VideoPickActivity.class);
                    // intent2.putExtra(IS_NEED_CAMERA, true);
                    intent2.putExtra(Constant.MAX_NUMBER, 30);
                    startActivityForResult(intent2, Constant.REQUEST_CODE_PICK_VIDEO);

                }
            });
            ImageButton documents = dialog.findViewById(R.id.documents);
            documents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    Intent intent4 = new Intent(MainScreen.this, NormalFilePickActivity.class);
                    intent4.putExtra(Constant.MAX_NUMBER, 50);
                    //intent4.putExtra(IS_NEED_FOLDER_LIST, true);
                    intent4.putExtra(NormalFilePickActivity.SUFFIX,
                            new String[]{"xlsx", "xls", "doc", "dOcX", "ppt", ".pptx", "pdf"});
                    startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
                }
            });
            ImageButton music = dialog.findViewById(R.id.music);
            music.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    Intent intent3 = new Intent(MainScreen.this, AudioPickActivity.class);
                    //   intent3.putExtra(IS_NEED_RECORDER, true);
                    intent3.putExtra(Constant.MAX_NUMBER, 50);
                    // intent3.putExtra(IS_NEED_FOLDER_LIST, true);
                    startActivityForResult(intent3, Constant.REQUEST_CODE_PICK_AUDIO);
                }
            });

            dialog.show();
        }
    }

    private void createNote(String note) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertNote(note);

    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
            StringBuilder builder = new StringBuilder();
            MarkedItemList.clearSelectionList();
            String path = imageUri.getPath();
            if (path.endsWith(".pdf") || path.endsWith(".doc") || path.endsWith(".docx")|| path.endsWith(".txt") || path.endsWith(".vcf") || path.endsWith("pps")
                    || path.endsWith("ppt") || path.endsWith(".vcs")|| path.endsWith("pptx") || path.endsWith(".xls") || path.endsWith("xlsx") || path.endsWith(".odt")){
                Log.d("LOVE", "LOVEIS" + path);
                FileListItem parent = new FileListItem();
                parent.setLocation(path);
                createNote(path);
                MarkedItemList.addSelectedItem(parent);
                builder.append(path + "\n");
            }else {
                String pathis = getRealPathFromURI(imageUri);

                Log.d("LOVE", "LOVEIS" + pathis);
                FileListItem parent = new FileListItem();
                parent.setLocation(pathis);
                createNote(pathis);
                MarkedItemList.addSelectedItem(parent);
                builder.append(path + "\n");
            }

            final String file[] = MarkedItemList.getSelectedPaths();

            Toast.makeText(this, builder.toString(), Toast.LENGTH_SHORT).show();
            this.setDialogSelectionListener(new DialogSelectionListener() {
                @Override
                public void onSelectedFilePaths(String[] files) {
                    if (null == files || files.length == 0) {
                        Toast.makeText(MainScreen.this, "Select at least one file to start Share Mode", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(getApplicationContext(), SHAREthemActivity.class);
                    intent.putExtra(SHAREthemService.EXTRA_FILE_PATHS, files);

                    intent.putExtra(SHAREthemService.EXTRA_PORT, DEFAULT_PORT_OREO);

                    intent.putExtra(SHAREthemService.EXTRA_SENDER_NAME, "Sri");
                    startActivity(intent);
                }
            });

            if (callbacks != null) {
                callbacks.onSelectedFilePaths(file);
            }

            //Toast.makeText(MainScreen.this, "list"+imageUris.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            StringBuilder builder = new StringBuilder();
            MarkedItemList.clearSelectionList();
            for (Uri file : imageUris) {
                String path = file.getPath();
                Log.d("LOVE", "LOVEIS" + path);
                if (path.endsWith(".pdf") || path.endsWith(".doc") || path.endsWith(".docx")|| path.endsWith(".txt") || path.endsWith(".vcf") || path.endsWith("pps")
                         || path.endsWith("ppt") || path.endsWith(".vcs") || path.endsWith("pptx") || path.endsWith(".xls") || path.endsWith("xlsx") || path.endsWith(".odt")){
                    Log.d("LOVE", "LOVEIS" + path);
                    FileListItem parent = new FileListItem();
                    parent.setLocation(path);
                    createNote(path);
                    MarkedItemList.addSelectedItem(parent);
                    builder.append(path + "\n");
                }else {
                    String pathis = getRealPathFromURI(file);

                    Log.d("LOVE", "LOVEIS" + path);
                    FileListItem parent = new FileListItem();
                    parent.setLocation(pathis);
                    createNote(pathis);
                    MarkedItemList.addSelectedItem(parent);
                    builder.append(path + "\n");
                }

            }
            final String file[] = MarkedItemList.getSelectedPaths();

           // Toast.makeText(this, builder.toString(), Toast.LENGTH_SHORT).show();
            this.setDialogSelectionListener(new DialogSelectionListener() {
                @Override
                public void onSelectedFilePaths(String[] files) {
                    if (null == files || files.length == 0) {
                        Toast.makeText(MainScreen.this, "Select at least one file to start Share Mode", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(getApplicationContext(), SHAREthemActivity.class);
                    intent.putExtra(SHAREthemService.EXTRA_FILE_PATHS, files);

                    intent.putExtra(SHAREthemService.EXTRA_PORT, DEFAULT_PORT_OREO);

                    intent.putExtra(SHAREthemService.EXTRA_SENDER_NAME, "Sri");
                    startActivity(intent);
                }
            });

            if (callbacks != null) {
                callbacks.onSelectedFilePaths(file);
            }

            Toast.makeText(MainScreen.this, "list" + imageUris.toString(), Toast.LENGTH_SHORT).show();
            // Update UI to reflect multiple images being shared
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String path = null;
        String[] proj = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);

        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            path = cursor.getString(column_index);
            Log.d("path", "PATHIS" + path);
        } else {
            Toast.makeText(MainScreen.this, "Error", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        return path;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendbuttonAd();
    }

    private void sendbuttonAd() {
        AdRequest adRequests=new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequests);
    }
}
