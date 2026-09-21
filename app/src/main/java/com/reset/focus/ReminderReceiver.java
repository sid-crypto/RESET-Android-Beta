package com.reset.focus;

import android.Manifest; import android.app.NotificationChannel; import android.app.NotificationManager; import android.app.PendingIntent; import android.content.BroadcastReceiver; import android.content.Context; import android.content.Intent; import android.content.pm.PackageManager; import androidx.annotation.NonNull;

public class ReminderReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        int slot=intent.getIntExtra("slot",20); String url=intent.getStringExtra("url");
        NotificationManager nm=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channel="reset_night";
        if (android.os.Build.VERSION.SDK_INT>=26) nm.createNotificationChannel(new NotificationChannel(channel,"RESET reminders",NotificationManager.IMPORTANCE_DEFAULT));
        if (android.os.Build.VERSION.SDK_INT>=33 && context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED) return;
        Intent open=new Intent(context,MainActivity.class).putExtra("open_url",url);
        PendingIntent pi=PendingIntent.getActivity(context,9100+slot,open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        android.app.Notification.Builder b=android.os.Build.VERSION.SDK_INT>=26?new android.app.Notification.Builder(context,channel):new android.app.Notification.Builder(context);
        b.setSmallIcon(android.R.drawable.ic_popup_reminder).setContentTitle("RESET · Plan tomorrow").setContentText(slot==60?"Your night reset starts now. Review tomorrow’s tasks.":"Take 30 seconds: update tomorrow’s plan.").setContentIntent(pi).setAutoCancel(true);
        nm.notify(9100+slot,b.build());
    }
}
