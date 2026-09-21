package com.reset.focus;

import android.Manifest; import android.app.Activity; import android.content.*; import android.content.pm.PackageManager; import android.net.Uri; import android.os.*; import android.provider.Settings; import android.webkit.*; import android.graphics.Color; import android.view.*; import android.widget.*;

public class MainActivity extends Activity {
    private static final String HOME="https://reset.ct.ws/";
    private WebView web;
    @Override public void onCreate(Bundle state){ super.onCreate(state); getWindow().setStatusBarColor(Color.rgb(7,8,12)); getWindow().setNavigationBarColor(Color.rgb(7,8,12));
        web=new WebView(this); web.setBackgroundColor(Color.rgb(7,8,12));
        WebSettings s=web.getSettings(); s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); s.setDatabaseEnabled(true); s.setSupportZoom(false); s.setBuiltInZoomControls(false); s.setDisplayZoomControls(false); s.setUserAgentString(s.getUserAgentString()+" RESET-Android/0.1.0");
        web.setWebViewClient(new WebViewClient(){ @Override public void onPageFinished(WebView v,String url){ super.onPageFinished(v,url); syncNativeReminders(); }});
        web.setWebChromeClient(new WebChromeClient()); web.addJavascriptInterface(new ResetBridge(this),"RESETNative"); setContentView(web);
        requestNotifications(); handleIntent(getIntent()); if(state==null) web.loadUrl(HOME); else web.restoreState(state);
    }
    private void requestNotifications(){ if(Build.VERSION.SDK_INT>=33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED) requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},501); }
    private void handleIntent(Intent i){ String u=i.getStringExtra("open_url"); if(u!=null && u.startsWith("https://reset.ct.ws")) web.loadUrl(u); }
    private void syncNativeReminders(){ web.evaluateJavascript("(function(){var g=function(n){var e=document.querySelector('meta[name=\\\"'+n+'\\\"]');return e?e.content:''}; return JSON.stringify({sleep:g('reset-sleep-time'),tomorrow:g('reset-tomorrow'),confirmed:g('reset-plan-confirmed'),enabled:g('reset-notifications'),url:g('reset-plan-url')});})()", value -> { if(value==null)return; try { String x=value.replace("\\\"","\"").replace("\\\\","\\"); org.json.JSONObject o=new org.json.JSONObject(x); if("1".equals(o.optString("enabled")) && !"1".equals(o.optString("confirmed"))) ReminderScheduler.schedule(this,o.optString("sleep"),o.optString("tomorrow"),o.optString("url")); else ReminderScheduler.cancel(this); } catch(Exception ignored){} }); }
    @Override public void onNewIntent(Intent i){ super.onNewIntent(i); handleIntent(i); }
    @Override protected void onSaveInstanceState(Bundle out){ web.saveState(out); super.onSaveInstanceState(out); }
    @Override public void onBackPressed(){ if(web.canGoBack()) web.goBack(); else super.onBackPressed(); }
    public static class ResetBridge { private final MainActivity a; ResetBridge(MainActivity a){this.a=a;} @JavascriptInterface public void cancelNightPlan(){ ReminderScheduler.cancel(a); } @JavascriptInterface public void openTomorrow(){ a.runOnUiThread(()->a.web.loadUrl(HOME+"tomorrow")); } }
}
