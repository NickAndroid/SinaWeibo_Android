package nick.dev.sina;

import com.nick.scalpel.ScalpelApplication;

import dev.nick.imageloader.ImageLoader;

public class SinaApp extends ScalpelApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoader.init(this);
    }
}
