package net.xvello.salasana;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    private String mNotifChannel;
    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText domain = findViewById(R.id.domain_edit);
        EditText pass   = findViewById(R.id.pass_edit);
        TextView hint   = findViewById(R.id.hint_text);

        domain.requestFocus();

        mNotifChannel = getString(R.string.clipboard_chan_name);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(mNotifChannel, mNotifChannel, NotificationManager.IMPORTANCE_LOW);
            mNotificationManager.createNotificationChannel(channel);
        }

        Intent intent = this.getIntent();
        if (intent != null) {
            if (Intent.ACTION_SEND.equalsIgnoreCase(intent.getAction()) && intent.hasExtra(Intent.EXTRA_TEXT)) {
                try {
                    URL url = new URL(intent.getStringExtra(Intent.EXTRA_TEXT));
                    String host = url.getHost();
                    domain.setText(host.replaceAll(".*\\.(?=.*\\.)", ""));  // http://stackoverflow.com/a/17241575
                    hint.setVisibility(View.GONE);
                    pass.requestFocus();
                } catch (MalformedURLException e) {
                    //TODO Snackbar error
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.action_night:
                switchNightMode();
                break;
        }
        return true;
    }

    private void switchNightMode() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        boolean isNight = !prefs.getBoolean(getString(R.string.pref_night_name), false);
        prefs.edit()
             .putBoolean(getString(R.string.pref_night_name), isNight)
             .apply();

        if (isNight){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        this.recreate();
    }

    // http://stackoverflow.com/questions/32586337/how-to-encode-pass-with-sha1-and-base64
    private static String encodePassword(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String result;
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(password.getBytes(StandardCharsets.UTF_8), 0, password.length());
        byte[] sha1hash = md.digest();
        result = Base64.encodeToString(sha1hash, Base64.DEFAULT);
        result = result.substring(0, 13)+"@1a";
        return result;
    }

    public void generatePassword(View view) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        EditText pass = findViewById(R.id.pass_edit);
        EditText domain = findViewById(R.id.domain_edit);

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Generated Password",
                encodePassword(pass.getText().toString()+':'+domain.getText().toString()));
        clipboard.setPrimaryClip(clip);

        Intent serviceIntent = new Intent(this, ClipboardResetService.class);
        PendingIntent resetIntent = PendingIntent.getService(this.getApplicationContext(), 0, serviceIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, mNotifChannel)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_content))
                .setDeleteIntent(resetIntent)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

        mNotificationManager.notify(R.string.notification_id, mBuilder.build());

        this.finish();
    }

    public void resetForm(View view) {
        EditText pass =  findViewById(R.id.pass_edit);
        pass.setText("");
        EditText domain = findViewById(R.id.domain_edit);
        domain.setText("");

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Generated Password", "");
        clipboard.setPrimaryClip(clip);

        mNotificationManager.cancelAll();
    }
}
