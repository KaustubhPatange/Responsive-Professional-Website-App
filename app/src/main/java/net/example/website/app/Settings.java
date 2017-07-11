package net.example.website.app;


import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static net.example.website.app.MainActivity.REQUEST_ID_MULTIPLE_PERMISSIONS;

/**
 * Created by kp on 26/6/17.
 */

public class Settings extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
               android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // Display the fragment as the main content
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public static class SettingsFragment extends PreferenceFragment {

        CheckBoxPreference ScreenOn;
     CheckBoxPreference ExternalLinks;
   CheckBoxPreference DisableExitConf;
        Preference ClearBrowsingData;
        Preference Opensource, OpenDownloads, CheckPM;
        Preference Changelog;
        Preference About;
        CookieManager cookieManager = MainActivity.cookieManager;

        boolean[] clear_data_values={false,false,false,false,false};
        int data_counter=0;
        WebView main_web_view = MainActivity.getWebview();

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            getPreferenceManager().setSharedPreferencesName("settings");
            addPreferencesFromResource(R.xml.settings);

            ScreenOn = (CheckBoxPreference) findPreference("screen_on");
            ExternalLinks = (CheckBoxPreference) findPreference("load_external_links");
          DisableExitConf = (CheckBoxPreference) findPreference("exit_confirmation_disabled");
            Opensource = findPreference("open_source");
            OpenDownloads = findPreference("open_download");
            CheckPM = findPreference("check_pm");
            CheckPM.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (Build.VERSION.SDK_INT >= 21) {
                       if (checkAndRequestPermissions()){
                           Toast.makeText(getActivity(),"Permission already Granted !",Toast.LENGTH_SHORT).show();

                       }else{
                           Toast.makeText(getActivity(),"Granting Permission !",Toast.LENGTH_SHORT).show();
                       }
                    } else {
                        Toast.makeText(getActivity(),"Device Does not Need this !",Toast.LENGTH_SHORT).show();
                    }

                    return true;
                }
            });
Opensource.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
    @Override
    public boolean onPreferenceClick(Preference preference) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
        Uri.parse("https://github.com/KaustubhPatange/Responsive-Professional-Website-App"));
        startActivity(browserIntent);
        return true;
    }
});
OpenDownloads.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
    @Override
    public boolean onPreferenceClick(Preference preference) {
        Uri selectedUri = Uri.parse(Environment.getExternalStorageDirectory() + "/Download/");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(selectedUri, "*/*");
        startActivity(Intent.createChooser(intent, "Open Folder"));
        return true;
    }
});
            ClearBrowsingData = findPreference("clear_browsing_data");

            Changelog = findPreference("changelog");
            About = findPreference("about");

            Changelog.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showDialog();
                    return true;
                }
            });
            About.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Create_About_App_Dialog();
                    return true;
                }
            });

            ClearBrowsingData.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                  //  create_clear_data(); I basically don't know how to clear browsing data using webview
                    // At first it was working but then when i added observable scrollview, its not working.
                    // If you can fix it.. Please Contact me :)
                    // Opening App Info
                    try{
                        Intent o = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        o.setData(Uri.parse("package:" + getString(R.string.packagename)));
                        startActivity(o);
                    } catch (ActivityNotFoundException e){
                        Intent o = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                        startActivity(o);
                    }
                    return true;
                }
            });
        }
        private  boolean checkAndRequestPermissions() {
            int internet = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.INTERNET);
            int storage = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            List<String> listPermissionsNeeded = new ArrayList<>();

            if (internet != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.INTERNET);
            }
            if (storage != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!listPermissionsNeeded.isEmpty())
            {
                ActivityCompat.requestPermissions(getActivity(),listPermissionsNeeded.toArray
                        (new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
                return false;
            }
            return true;
        }
        public void clear_data(){
         if(clear_data_values[0]){
                main_web_view.clearHistory();
            }if(clear_data_values[1]){
                main_web_view.clearCache(true);
            }if(clear_data_values[2]){
                main_web_view.clearFormData();
            }if(clear_data_values[3]){
                cookieManager = CookieManager.getInstance();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
                        // a callback which is executed when the cookies have been removed
                        @Override
                        public void onReceiveValue(Boolean aBoolean) {
                        }
                    });
                }
                else cookieManager.removeAllCookie();
            }if(clear_data_values[4]){
                main_web_view.clearMatches();
                main_web_view.clearSslPreferences();
            }
            Toast.makeText(getActivity(),"Browsing Data Cleared",Toast.LENGTH_SHORT).show();

        }
        private void Create_About_App_Dialog(){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setIcon(R.drawable.ic_info_black_24dp);
           // builder.setMessage(getString(R.string.aboutdialog))
              //      .setTitle("About");
            builder.setMessage(Html.fromHtml("<b>KP'S TV App</b><br><br>Made with Android Studio by KP<br>Special Thanks to <i>Shripal Bro</i><br>Without which I couldn't do it all alone<br><br><b>Contributors:</b><br>  -Shripal Jain (shripal17)<br>  -Rishabh Singh (rishi.you)</b>  -ksoichiro (Observable scrollview)<br><br><i>App Version: "+BuildConfig.VERSION_NAME+"<br>Copyright KP @2017</i>"))
                    .setTitle("About");

            builder.setPositiveButton("Nice!", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            //show the dialog
            AlertDialog about = builder.create();
            about.show();
        }
        private void create_clear_data(){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Set the dialog title
            builder.setTitle("Clear Browsing Data")
                    // Specify the list array, the items to be selected by default (null for none),
                    // and the listener through which to receive callbacks when items are selected
                    .setMultiChoiceItems(R.array.data_items, null,
                            new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which,
                                                    boolean isChecked) {
                                    if (isChecked) {
                                        // If the user checked the item, add it to the selected items
                                        clear_data_values[which]=true;
                                        data_counter++;
                                    } else if (clear_data_values[which]) {
                                        // Else, if the item is already in the array, remove it
                                        clear_data_values[which]=false;
                                        data_counter--;
                                    }
                                }
                            })
                    .setIcon(R.drawable.ic_warning)
                    // Set the action buttons
                    .setPositiveButton("Clear Data", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK, so save the mSelectedItems results somewhere
                            // or return them to the component that opened the dialog
                            if(data_counter>0) {
                                Create_Clear_Data_Dialog();
                            }else {
                                Toast.makeText(getActivity(), "No Items Selected", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

            AlertDialog clear_data_alert = builder.create();
            clear_data_alert.show();

        }

        private void Create_Clear_Data_Dialog(){
            AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
            builder2.setIcon(R.drawable.ic_warning);
            builder2.setMessage("Are you sure you want to clear browsing data?\nNote that clearing cookies will log you out from the website")
                    .setTitle("Confirmation");
            builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    clear_data();
                }
            });
            builder2.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog data_confirm = builder2.create();
            data_confirm.show();
        }

        public void showDialog(){
            AlertDialog.Builder builder  = new AlertDialog.Builder(getActivity());
            builder.setIcon(R.drawable.ic_action_update);
            builder.setMessage(R.string.changelog).setTitle("Changelog")
                    .setPositiveButton("Alright!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            builder.create().show();
        }
    }
}
