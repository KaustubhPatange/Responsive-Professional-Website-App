package net.example.website.app;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener, ObservableScrollViewCallbacks {

    public static CookieManager cookieManager;
    SharedPreferences sharedPref;
    boolean disable_exit_conf = true;
    boolean disable_url_overload = false;
    boolean keep_screen_on = false;
    public WebView mwebview;
    public static WebView cwebview;
    private static final String PRIVATE_PREF = "myapp";
    private static final String VERSION_KEY = "version_number";
    public static WebView getWebview() {
        return cwebview;
    }
    InterstitialAd mInterstitialAd;
    public Intent i;
int c = 0;
        public String tmp;
    private CoordinatorLayout coordinatorLayout;
    private ShareActionProvider mShareActionProvider;
        ProgressBar bar;
    private SwipeRefreshLayout swipeRefreshLayout;
        private boolean isRedirected;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

        @Override

        protected void onCreate (Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_main);
           final ObservableWebView mwebview = (ObservableWebView) findViewById(R.id.cwebview);
            mwebview.setScrollViewCallbacks(this);
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            if (!isNetworkStatusAvialable(getApplicationContext())) {
            //Toast.makeText(getApplicationContext(), "Check your Internet Connection !", Toast.LENGTH_SHORT).show();
            Snackbar.make(findViewById(R.id.coorder), "Check your Internet Connection !", Snackbar.LENGTH_LONG)
                    .setAction("Refresh", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mwebview.loadUrl(getString(R.string.MainWebsite));
                                                   }
                    })

                                       .show();

                    }

            if (Build.VERSION.SDK_INT >= 21) {
                checkAndRequestPermissions();
            }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bar = (ProgressBar) findViewById(R.id.progressBar2);

            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.setRefreshing(true);
                    bar.setVisibility(View.GONE);
                    WebView test1 = (WebView) findViewById(R.id.cwebview);
                    test1.reload();
                    createSnackbar("Refreshing..", "Stop");
                }
            });
                 fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                  //      .setAction("Action", null).show();
                 WebView test1 = (WebView) findViewById(R.id.cwebview);
                tmp = test1.getUrl();
              String test = test1.getTitle();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT,Html.fromHtml(test + "<br><br>" + tmp).toString());
                startActivity(Intent.createChooser(shareIntent, "Share Post using"));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
            MobileAds.initialize(getApplicationContext(), getString(R.string.InterstitialAds_ADBMOD));
            mInterstitialAd = new InterstitialAd(this);

            // set the ad unit ID
            mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));


            // Load ads into Interstitial Ads


            sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE);
            viewChangelog();
            lookforsettings();
        // Webveiw Boys
       loadwebview();
    }

    private void showInterstitial() {
        SharedPreferences SP = getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean show = SP.getBoolean("show_ads",true);
        if (show) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }
    }
    public void createSnackbar(final String msg, final String btn){
        final WebView test = (WebView) findViewById(R.id.cwebview);
        Snackbar.make(findViewById(R.id.coorder), msg, Snackbar.LENGTH_LONG)
                .setAction(btn, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        test.stopLoading();
                    }
                })

                .show();
    }
    //ad
    public void startAD() {
        c=0;
        SharedPreferences SP = getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean show = SP.getBoolean("show_ads", true);
        if (show) {
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            mInterstitialAd.loadAd(adRequest);
            mInterstitialAd.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    showInterstitial();
                }
            });
        }
    }
    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll,
                                boolean dragging) {
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        ActionBar ab = getSupportActionBar();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (scrollState == ScrollState.UP) {
            if (ab.isShowing()) {
                ab.hide();
                fab.hide();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!ab.isShowing()) {
                ab.show();
                fab.show();
            }
        }

    }
    @Override
    protected void onResume() {
        super.onResume();

       lookforsettings();
    }
    private void lookforsettings(){
        ExitConfChanged();
        ExternalLinkChanged();
   //  ScreenOnChanged();
            }

    private void ExitConfChanged() {
        disable_exit_conf = sharedPref.getBoolean("exit_confirmation_disabled", true);

    }
    private void ExternalLinkChanged() {
        disable_url_overload = sharedPref.getBoolean("load_external_links", false);
    }

    private void ScreenOnChanged() {
        SharedPreferences SP = getSharedPreferences("settings", Context.MODE_PRIVATE);
         boolean screenon = SP.getBoolean("screen_on",false);
        mwebview.setKeepScreenOn(screenon);
    }
    private  boolean checkAndRequestPermissions() {
        int internet = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
         List<String> listPermissionsNeeded = new ArrayList<>();

        if (internet != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.INTERNET);
        }
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
                if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
    private void viewChangelog() {
        sharedPref = getSharedPreferences(PRIVATE_PREF, Context.MODE_PRIVATE);
        int currentVersionNumber = 0;

        int savedVersionNumber = sharedPref.getInt(VERSION_KEY, 0);

        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            currentVersionNumber = pi.versionCode;
        } catch (Exception e) {
        }

        if (currentVersionNumber > savedVersionNumber) {
            showDialog();

            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putInt(VERSION_KEY, currentVersionNumber);
            editor.commit();
        }
    }
    public void showDialog(){
        AlertDialog.Builder builder  = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_action_update);
        builder.setMessage(R.string.changelog).setTitle("What's New")
                .setPositiveButton("Alright!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }
public void loadwebview(){
    final ObservableWebView mwebview = (ObservableWebView) findViewById(R.id.cwebview);
    mwebview.setScrollViewCallbacks(this);
    WebSettings webSettings = mwebview.getSettings();
    webSettings.setJavaScriptEnabled(true);
    mwebview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
    mwebview.getSettings().setCacheMode(webSettings.LOAD_CACHE_ELSE_NETWORK);
    mwebview.getSettings().setAppCacheEnabled(true);
   // mwebview.getSettings().setSupportMultipleWindows(true);
    mwebview.getSettings().setAllowFileAccess(true);
    mwebview.getSettings().setBuiltInZoomControls(true);
    mwebview.getSettings().setDisplayZoomControls(false);
    mwebview.getSettings().setLoadWithOverviewMode(true);
  //  mwebview.getSettings().setUseWideViewPort(true);
   // mwebview.loadData(mresult, "text/html; charset=UTF-8", null);
    mwebview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    webSettings.setDomStorageEnabled(true);
    webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
    webSettings.setUseWideViewPort(true);
    webSettings.setSavePassword(true);
    webSettings.setSaveFormData(true);
    webSettings.setEnableSmoothTransition(true);
    mwebview.setDownloadListener(new DownloadListener() {

        @Override
        public void onDownloadStart(String url, String userAgent,
                                    String contentDisposition, String mimetype,
                                    long contentLength) {
            i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            final DownloadManager.Request request = new DownloadManager.Request(
                    Uri.parse(url));
                       request.setMimeType(mimetype);

            String cookies = CookieManager.getInstance().getCookie(url);

            request.addRequestHeader("cookie", cookies);

            request.addRequestHeader("User-Agent", userAgent);

            request.setDescription("Downloading file...");

            request.setTitle(URLUtil.guessFileName(url, contentDisposition,
                    mimetype));

            request.allowScanningByMediaScanner();

            File dir = new File("Download/");
            String name =  URLUtil.guessFileName(
                    url, contentDisposition, mimetype);
            if (!dir.exists())
                dir.mkdirs();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(dir.toString(), name);

            final AlertDialog.Builder ask = new AlertDialog.Builder(MainActivity.this);
            ask.setIcon(R.drawable.ic_file_download);
                    ask.setTitle("Download")
                    .setMessage(Html.fromHtml("<b>File name:</b><br> "+name + "<br><br><b>Size:</b><br> "+ String.format("%.2f", (contentLength / 1048576.037)) + "MB<br><br>All downloads are saved to Storage/Download/"))
                    .setPositiveButton("Download Externally", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(i);
                            }
                    })
                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                    .setNegativeButton("Download", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                            dm.enqueue(request);
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Downloading Started !",Toast.LENGTH_SHORT).show();

                        }
                    })

            .show();

        }
    });
    mwebview.loadUrl(getString(R.string.MainWebsite));
    if (isNetworkStatusAvialable(getApplicationContext())) {
        createSnackbar("Loading Main Website", "Stop");
    }
    mwebview.setWebViewClient(new MyWebviewClient());
}

        @Override
        public void onBackPressed () {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            WebView test1 = (WebView) findViewById(R.id.cwebview);
            if (test1.canGoBack()) {

                test1.goBack();
            } else {
                // Alert box to close the app
                SharedPreferences SP = getSharedPreferences("settings", Context.MODE_PRIVATE);
                boolean exittype = SP.getBoolean("exit_confirmation_disabled",true);
                if(exittype) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(this);
                    adb.setTitle("Warning");
                    adb.setIcon(android.R.drawable.ic_dialog_alert);
                    adb.setMessage("Are you Sure ?");
                    adb.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    adb.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    adb.show();
                }else{
                    finish();
                }
            }
        }
    }

        @Override
        public boolean onCreateOptionsMenu (Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_forward:
                if (isNetworkStatusAvialable(getApplicationContext())) {
                    WebView test1 = (WebView) findViewById(R.id.cwebview);
                    if (test1.canGoForward()) {
                    test1.goForward();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Cannot go Forward!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "No Internet Connection Found !", Toast.LENGTH_SHORT);
                    toast.show();
                }

                return true;
                                     case R.id.action_search:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                final EditText edittext = new EditText(this);
                               alert.setTitle("Search");
                  alert.setIcon(R.drawable.ic_action_search);
                alert.setView(edittext);

                alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //What ever you want to do with the value
                        if (isNetworkStatusAvialable(getApplicationContext())) {
                            WebView test1 = (WebView) findViewById(R.id.cwebview);
                            String text = edittext.getText().toString();
                            String text1 = text.replaceAll(" ", "%20");
                            String url1 = getString(R.string.MainWebsite) + "/?s=" + text1;
                            test1.loadUrl(url1);
                            Toast toast2 = Toast.makeText(getApplicationContext(), "Searching For " + text, Toast.LENGTH_SHORT);
                            toast2.show();
                        } else {
                            Toast.makeText(getApplicationContext(), "No Internet Connection Found !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                    }
                });

                alert.show();
                return true;
            case R.id.action_refresh:
                if (isNetworkStatusAvialable(getApplicationContext())) {
                    WebView test1 = (WebView) findViewById(R.id.cwebview);
                    test1.reload();
                    createSnackbar("Refreshing..", "Stop");
                } else {
                    Toast toast2 = Toast.makeText(getApplicationContext(), "No Internet Connection Found !", Toast.LENGTH_SHORT);
                    toast2.show();
                }
                return true;
            case R.id.action_stop:
                WebView test1 = (WebView) findViewById(R.id.cwebview);
                test1.stopLoading();
                bar.setVisibility(View.GONE);
                Toast toast = Toast.makeText(getApplicationContext(), "Loading Stop!", Toast.LENGTH_SHORT);
                toast.show();
                return true;
            case R.id.action_exit:
                SharedPreferences SP = getSharedPreferences("settings", Context.MODE_PRIVATE);
                boolean exittype = SP.getBoolean("exit_confirmation_disabled",true);
                if(exittype) {
                    AlertDialog.Builder adb1 = new AlertDialog.Builder(this);
                    adb1.setTitle("Warning");
                    adb1.setIcon(android.R.drawable.ic_dialog_alert);
                    adb1.setMessage("Are you Sure ?");
                    adb1.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    adb1.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    adb1.show();
                } else {
                    finish();
                }

                return true;
            case R.id.action_owp:
                WebView test2 = (WebView) findViewById(R.id.cwebview);
                tmp = test2.getUrl();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                 Uri.parse(tmp));
                startActivity(browserIntent);
                return true;
            case R.id.action_webinfo:
                WebView test3 = (WebView) findViewById(R.id.cwebview);
                String test = test3.getTitle();
                tmp = test3.getUrl();
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle("Web Details");
                adb.setIcon(android.R.drawable.ic_dialog_info);
                adb.setMessage(Html.fromHtml("<b>" + "Title: " + "</b>" + test + "<br>" + "<b>" + "Url: " + "</b>" + tmp));
                adb.setPositiveButton("Copy Url", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        android.content.ClipData clip = android.content.ClipData.newPlainText("", tmp.toString());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getApplicationContext(), "Copied to Clipboard!", Toast.LENGTH_SHORT).show();
                    }
                });
                adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                adb.show();
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, Settings.class);
                startActivity(settingsIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    public static boolean isNetworkStatusAvialable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if (netInfos != null)
                if (netInfos.isConnected())
                    return true;
        }
        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        WebView test = (WebView) findViewById(R.id.cwebview);
        if (id == R.id.nav_home) {
            test.loadUrl(getString(R.string.MainWebsite));
            createSnackbar("Loading Main Website", "Stop");
            // Handle the camera action
        } else if (id == R.id.nav_devtool) {
            test.loadUrl(getString(R.string.Page1));
            createSnackbar("Loading " + getString(R.string.Page1ID), "Stop");

        } else if (id == R.id.nav_othdev) {
            test.loadUrl(getString(R.string.Page2));
            createSnackbar("Loading " + getString(R.string.Page2ID), "Stop");

        } else if (id == R.id.nav_guide) {
            test.loadUrl(getString(R.string.Page3));
            createSnackbar("Loading " + getString(R.string.Page3ID), "Stop");

        } else if (id == R.id.nav_author) {
            test.loadUrl(getString(R.string.Page4));
            createSnackbar("Loading " + getString(R.string.Page4ID), "Stop");

        } else if (id == R.id.nav_github) {
            test.loadUrl(getString(R.string.Page5));
            createSnackbar("Loading " + getString(R.string.Page5ID), "Stop");

        } else if (id == R.id.nav_xda) {
            test.loadUrl(getString(R.string.Page6));
            createSnackbar("Loading " + getString(R.string.Page6ID), "Stop");
        } else if (id == R.id.nav_youtube) {
         // TODO Edit This below text,, add the comments and add link to page7 in strings.xml
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
            Uri.parse(getString(R.string.YoutubeLink)));
            startActivity(browserIntent);
           //test.loadUrl(getString(R.string.Page7));
           // createSnackbar("Loading " + getString(R.string.Page7ID), "Stop");
        } else if (id == R.id.nav_email){
            Email();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void Email() {
        String[] TO = {getString(R.string.EmailID)};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email to "+ getString(R.string.Ownername)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private class MyWebviewClient extends WebViewClient {
        class CustomWebViewClient extends WebViewClient {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    SharedPreferences SP = getSharedPreferences("settings", Context.MODE_PRIVATE);
                 boolean externellinko = SP.getBoolean("load_external_links",false);
                if (!externellinko) {
                    view.loadUrl(url);
                    tmp = url;
                    if (!isNetworkStatusAvialable(getApplicationContext())) {
                        Snackbar.make(findViewById(R.id.coorder), "Check your Internet Connection !", Snackbar.LENGTH_LONG)
                                                                .show();

                    }

                } else {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url));
                    startActivity(browserIntent);
                }
                return true;
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            swipeRefreshLayout.setRefreshing(false);
            bar.setVisibility(View.GONE);
            bar.setProgress(100);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            c++;
            if (c >= 2)
                startAD();
            swipeRefreshLayout.setRefreshing(false);
            bar.setVisibility(View.VISIBLE);
            bar.setProgress(0);
        }

    }


}

