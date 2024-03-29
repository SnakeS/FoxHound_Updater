package foxhoundupdaterapp.tasks;

import android.content.Context;
import android.os.AsyncTask;
import foxhoundupdaterapp.customTypes.Screenshot;
import foxhoundupdaterapp.customTypes.UpdateInfo;
import foxhoundupdaterapp.database.DbAdapter;
import foxhoundupdaterapp.misc.Log;
import foxhoundupdaterapp.ui.ScreenshotActivity;
import foxhoundupdaterapp.utils.ImageUtilities;

import java.net.URI;

public class DownloadImageTask extends AsyncTask<UpdateInfo, Screenshot, Void> {
    private static final String TAG = "DownloadImageTask";

    private final Boolean showDebugOutput;
    private final Context context;

    public DownloadImageTask(Context ctx, Boolean _showDebugOutput) {
        showDebugOutput = _showDebugOutput;
        context = ctx;
    }

    @Override
    protected Void doInBackground(UpdateInfo... params) {
        DbAdapter db = new DbAdapter(context, showDebugOutput);
        UpdateInfo ui = params[0];

        try {
            db.open();
            String[] PrimaryKeys = new String[ui.screenshots.size()];
            boolean ScreenFound = false;
            boolean NeedsUpdate = false;
            int counter = 0;
            for (URI uri : ui.screenshots) {
                if (showDebugOutput) Log.d(TAG, "Started Downloading Image number " + counter);
                if (isCancelled())
                    return null;
                //Add to DB if not there, otherwise get the DatabaseObject
                Screenshot screeni = db.ScreenshotExists(ui.PrimaryKey, uri.toString());
                if (screeni.PrimaryKey != -1) {
                    ScreenFound = true;
                }
                if (isCancelled())
                    return null;
                Screenshot s = ImageUtilities.load(uri.toString(), screeni.getModifyDateAsMillis(), screeni.PrimaryKey, ui.PrimaryKey);
                //Null when Modifydate not changed
                if (s != null) {
                    NeedsUpdate = true;
                    screeni = s;
                }
                if (isCancelled())
                    return null;
                //When not found insert in DB
                if (!ScreenFound) {
                    screeni = s;
                    screeni.ForeignExtraListKey = ui.PrimaryKey;
                    screeni.url = uri;
                    screeni.PrimaryKey = db.insertScreenshot(screeni);
                }
                if (isCancelled())
                    return null;
                    //Only Update if Screenshot was there
                else if (ScreenFound && NeedsUpdate) {
                    db.updateScreenshot(screeni.PrimaryKey, screeni);
                }
                if (isCancelled())
                    return null;
                //Calls onProgressUpdate (runs in UI Thread)
                publishProgress(screeni);
                PrimaryKeys[counter] = Long.toString(screeni.PrimaryKey);
                counter++;
                ScreenFound = false;
                NeedsUpdate = false;
            }
            if (isCancelled())
                return null;
            //Delete old Screenshots from DB
            db.removeScreenshotExcept(ui.PrimaryKey, PrimaryKeys);
        }
        finally {
            if (db != null)
                db.close();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Screenshot... screeni) {
        //This runs in the UI Thread
        if (!isCancelled()) {
            ScreenshotActivity.AddScreenshot(screeni[0]);
            ScreenshotActivity.NotifyChange();
        }
    }

    @Override
    protected void onCancelled() {
        try {
            this.finalize();
        }
        catch (Throwable e) {
            Log.e(TAG, "Exception in finalize", e);
        }
    }
}
