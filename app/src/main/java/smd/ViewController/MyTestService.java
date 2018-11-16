package smd.ViewController;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import smd.Model.PillBox;

/**
 * Created by wissa on 01-Aug-17.
 */

public class MyTestService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     *
     */
    public MyTestService() {
        super("sync thread");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        PillBox pillBox = new PillBox();
        pillBox.syncdown(getApplicationContext());
    }
}
