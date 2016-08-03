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

package nick.dev.sina.app.content;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.nick.scalpel.ScalpelAutoActivity;
import com.nick.scalpel.annotation.binding.FindView;
import com.nick.scalpel.annotation.binding.OnClick;

import dev.nick.imageloader.DisplayListener;
import dev.nick.imageloader.ImageLoader;
import dev.nick.imageloader.display.DisplayOption;
import dev.nick.imageloader.display.ImageQuality;
import dev.nick.imageloader.display.ImageSettable;
import dev.nick.imageloader.display.processor.BlurBitmapProcessor;
import dev.nick.imageloader.loader.result.BitmapResult;
import dev.nick.imageloader.loader.result.Cause;
import dev.nick.logger.LoggerManager;
import nick.dev.sina.R;
import nick.dev.sina.app.widget.ColorUtils;

public class FeedImageViewerActivity extends ScalpelAutoActivity {

    @FindView(id = R.id.image)
    @OnClick(action = "finish")
    ImageView mImageView;

    @FindView(id = R.id.fab)
    FloatingActionButton mFab;

    @FindView(id = R.id.root)
    ViewGroup mRootView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.image_viewer);

        String thumb = getIntent().getStringExtra("thumb");
        ImageLoader.shared(this)
                .displayImage(thumb, new ImageSettable() {
                    @Override
                    public void setImageBitmap(@NonNull Bitmap bitmap) {
                        // Nothing.
                    }

                    @Override
                    public void setImageResource(int resId) {
                        // Nothing.
                    }

                    @Override
                    public int getWidth() {
                        return mRootView.getWidth();
                    }

                    @Override
                    public int getHeight() {
                        return mRootView.getHeight();
                    }

                    @Override
                    public void startAnimation(Animation animation) {
                        // Nothing.
                    }
                }, new DisplayOption.Builder()
                        .bitmapProcessor(new BlurBitmapProcessor(20))
                        .build());

        String url = getIntent().getStringExtra("url");

        ImageLoader.shared(this)
                .displayImage(url, mImageView,
                        new DisplayOption.Builder()
                                .imageQuality(ImageQuality.RAW)
                                .build(), new DisplayListenerStub() {
                            @Override
                            public void onComplete(@Nullable BitmapResult result) {
                                LoggerManager.getLogger(FeedImageViewerActivity.class).funcEnter();
                                if (result != null && result.result != null) {
                                    Palette.from(result.result).generate(new Palette.PaletteAsyncListener() {
                                        @Override
                                        public void onGenerated(Palette palette) {
                                            LoggerManager.getLogger(FeedImageViewerActivity.class).funcEnter();
                                            int defColor = ContextCompat.getColor(FeedImageViewerActivity.this, R.color.primary_light);
                                            int themeColor = ColorUtils.colorBurn(palette.getLightVibrantColor(defColor));
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                getWindow().setStatusBarColor(themeColor);
                                                getWindow().setNavigationBarColor(themeColor);
                                            }
                                        }
                                    });
                                }
                            }
                        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.activity_out);
    }

    class DisplayListenerStub implements DisplayListener {

        @Override
        public void onError(@NonNull Cause cause) {

        }

        @Override
        public void onComplete(@Nullable BitmapResult result) {

        }

        @Override
        public void onProgressUpdate(float progress) {

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onStartLoading() {

        }
    }
}
