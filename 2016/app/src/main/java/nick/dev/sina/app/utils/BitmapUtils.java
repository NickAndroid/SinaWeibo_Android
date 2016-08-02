/*
 * Copyright (c) 2016 Nick Guo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nick.dev.sina.app.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import dev.nick.logger.LoggerManager;

public abstract class BitmapUtils {

    public static Bitmap scale(@NonNull Bitmap in, float wRadius, float yRadius) {
        Matrix matrix = new Matrix();
        matrix.postScale(wRadius, yRadius);
        return Bitmap.createBitmap(in, 0, 0, in.getWidth(), in.getHeight(), matrix, true);
    }

    public static void saveBitmapAsync(@NonNull final Bitmap in, final String path, @NonNull final ActionListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File out = new File(path);
                    if (!out.getParentFile().exists() && !out.getParentFile().mkdirs()) {
                        listener.onResult(false);
                    }
                    listener.onResult(in.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(out)));
                } catch (FileNotFoundException e) {
                    LoggerManager.getLogger(BitmapUtils.class).trace("saveBitmapAsync failed: ", e);
                    listener.onResult(false);
                }
            }
        }).start();
    }

    public interface ActionListener {
        void onResult(boolean ok);
    }
}
