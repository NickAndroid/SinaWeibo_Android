package nick.dev.sina.app.provider;

import android.content.Context;
import android.os.Environment;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import nick.dev.sina.app.utils.XmlUtils;

/**
 * Created by nick on 16-8-1.
 */
public class ThemeProviderImpl implements ThemeProvider {

    Context mContext;

    public ThemeProviderImpl(Context context) {
        this.mContext = context;
    }

    @Override
    public void inflateTabColorMap(Map in) {
        File outFile = new File(Environment.getExternalStorageDirectory().getPath()
         + File.separator + "tests.xml");
        outFile.delete();
        try {
            outFile.createNewFile();
        } catch (IOException e) {

        }
        try {
            XmlUtils.writeMapXml(in, new FileOutputStream(outFile));
        } catch (XmlPullParserException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
