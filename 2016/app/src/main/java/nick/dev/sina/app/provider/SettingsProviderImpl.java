package nick.dev.sina.app.provider;

/**
 * Created by nick on 16-8-1.
 */
public class SettingsProviderImpl implements SettingsProvider{

    public SettingsProviderImpl() {
    }

    @Override
    public int getLastTabIndex() {
        return 3;
    }
}
