package com.reset.focus;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int slot = intent.getIntExtra("slot", 20);
        String url = intent.getStringExtra("url");

        NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channel = "reset_night";

        if (android.os.Build.VERSION.SDK_INT >= 26) {
            nm.createNotificationChannel(
                    new NotificationChannel(
                            channel,
                            "RESET reminders",
                            NotificationManager.IMPORTANCE_DEFAULT
                    )
            );
        }

        if (android.os.Build.VERSION.SDK_INT >= 33
                && context.checkSelfPermission(
                        Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Intent open = new Intent(context, MainActivity.class);

        if (url != null && url.startsWith("https://reset.ct.ws")) {
            open.putExtra("open_url", url);
        }

        PendingIntent pi = PendingIntent.getActivity(
                context,
                9100 + slot,
                open,
                PendingIntent.FLAG_UPDATE_CURRENT
                        | PendingIntent.FLAG_IMMUTABLE
        );

        android.app.Notification.Builder builder;

        if (android.os.Build.VERSION.SDK_INT >= 26) {
            builder = new android.app.Notification.Builder(
                    context,
                    channel
            );
        } else {
            builder = new android.app.Notification.Builder(context);
        }

        String title = "RESET · Plan tomorrow";

        String message;

        if (slot == 60) {
            message = "Your night reset starts now. Review tomorrow's tasks.";
        } else if (slot == 40) {
            message = "40 minutes before sleep. Take a moment to review tomorrow.";
        } else {
            message = "20 minutes before sleep. Update tomorrow's plan.";
        }

        builder
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pi)
                .setAutoCancel(true);

        nm.notify(9100 + slot, builder.build());
    }
}
