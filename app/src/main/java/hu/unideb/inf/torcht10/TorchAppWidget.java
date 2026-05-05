package hu.unideb.inf.torcht10;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class TorchAppWidget extends AppWidgetProvider {

    private static final String TORCH_SHRPREF_KEY = "hu.unideb.inf.torcht10.TORCH_SHRPREF_KEY";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.torch_app_widget);


        Intent intentUpdate = new Intent(context, TorchAppWidget.class);
        intentUpdate.setAction("TEST_ACTION");
        PendingIntent pendingUpdate = PendingIntent.getBroadcast(context,
                appWidgetId, intentUpdate, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.torch_button_w, pendingUpdate);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        SharedPreferences prefs = context.getSharedPreferences(
                "shrprf", 0);
        boolean torch = prefs.getBoolean(TORCH_SHRPREF_KEY, true);
        CameraManager camManager =
                (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        String cameraId = null;
        try {
            cameraId = camManager.getCameraIdList()[0];
            camManager.setTorchMode(cameraId, torch);
            torch = !torch;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        // Save count back to prefs.
        SharedPreferences.Editor prefEditor = prefs.edit();
        prefEditor.putBoolean(TORCH_SHRPREF_KEY, torch);
        prefEditor.apply();
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}