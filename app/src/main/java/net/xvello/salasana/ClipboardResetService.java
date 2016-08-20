package net.xvello.salasana;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.IBinder;

public class ClipboardResetService extends Service {
    public ClipboardResetService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Generated Password","");
        clipboard.setPrimaryClip(clip);

        this.stopSelf();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
