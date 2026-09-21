package com.reset.focus;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import java.util.Calendar;

public final class ReminderScheduler {
    private static final int[] MINUTES = {60,40,20};
    private ReminderScheduler() {}

    public static void schedule(Context c, String sleep, String tomorrow, String planUrl) {
        cancel(c);
        if (sleep == null || sleep.length() < 5) return;
        String[] p = sleep.split(":");
        int h = Integer.parseInt(p[0]), m = Integer.parseInt(p[1]);
        AlarmManager am = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        for (int slot : MINUTES) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            cal.set(Calendar.HOUR_OF_DAY,h); cal.set(Calendar.MINUTE,m); cal.set(Calendar.SECOND,0); cal.set(Calendar.MILLISECOND,0);
            cal.add(Calendar.MINUTE,-slot);
            if (cal.getTimeInMillis() <= System.currentTimeMillis()) cal.add(Calendar.DAY_OF_YEAR,1);
            Intent i = new Intent(c, ReminderReceiver.class).setAction("RESET_NIGHT");
            i.putExtra("slot", slot); i.putExtra("tomorrow", tomorrow); i.putExtra("url", planUrl);
            PendingIntent pi = PendingIntent.getBroadcast(c, 9000+slot, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            if (Build.VERSION.SDK_INT >= 31 && !am.canScheduleExactAlarms()) {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
            } else {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
            }
        }
    }

    public static void cancel(Context c) {
        AlarmManager am = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        for (int slot : MINUTES) {
            Intent i = new Intent(c, ReminderReceiver.class).setAction("RESET_NIGHT");
            PendingIntent pi = PendingIntent.getBroadcast(c, 9000+slot, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            am.cancel(pi); pi.cancel();
        }
    }
}
