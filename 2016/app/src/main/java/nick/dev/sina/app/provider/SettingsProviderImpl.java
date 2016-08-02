package nick.dev.sina.app.provider;

import android.os.Environment;

import java.io.File;

import nick.dev.sina.app.SinaApp;

/**
 * Created by nick on 16-8-1.
 */
public class SettingsProviderImpl implements SettingsProvider {

    public SettingsProviderImpl() {
    }

    @Override
    public int getLastTabIndex() {
        return 0;
    }

    @Override
    public String snapShotPath() {// FIXME: 2016/8/2 No path
        return Environment.getExternalStorageDirectory().getPath() + File.separator + SinaApp.class.getSimpleName();
    }
}
