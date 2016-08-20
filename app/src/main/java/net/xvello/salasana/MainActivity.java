package net.xvello.salasana;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText domain = (EditText) findViewById(R.id.domain_edit);
        EditText pass =  (EditText) findViewById(R.id.pass_edit);
        TextView hint = (TextView) findViewById(R.id.hint_text);

        domain.requestFocus();

        Intent intent = this.getIntent();
        if (intent != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_SEND) && intent.hasExtra(Intent.EXTRA_TEXT)) {
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
        }
        return true;
    }

    // http://stackoverflow.com/questions/32586337/how-to-encode-pass-with-sha1-and-base64
    private static String encodePassword(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String result;
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(password.getBytes("UTF-8"), 0, password.length());
        byte[] sha1hash = md.digest();
        result = Base64.encodeToString(sha1hash, Base64.DEFAULT);
        result = result.substring(0, 13)+"@1a";
        return result;
    }

    public void generatePassword(View view) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        EditText pass =  (EditText) findViewById(R.id.pass_edit);
        EditText domain = (EditText) findViewById(R.id.domain_edit);

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Generated Password",
                encodePassword(pass.getText().toString()+':'+domain.getText().toString()));
        clipboard.setPrimaryClip(clip);

        Intent serviceIntent = new Intent(this, ClipboardResetService.class);
        PendingIntent resetIntent = PendingIntent.getService(this.getApplicationContext(), 0, serviceIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_content))
                .setDeleteIntent(resetIntent)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(R.string.notification_id, mBuilder.build());

        this.finish();
    }

    public void resetForm(View view) {
        EditText pass =  (EditText) findViewById(R.id.pass_edit);
        pass.setText("");
        EditText domain = (EditText) findViewById(R.id.domain_edit);
        domain.setText("");

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Generated Password", "");
        clipboard.setPrimaryClip(clip);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
    }
}
