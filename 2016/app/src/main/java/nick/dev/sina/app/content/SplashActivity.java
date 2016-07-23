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

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.nick.scalpel.ScalpelAutoActivity;
import com.nick.scalpel.annotation.binding.FindView;
import com.nick.scalpel.annotation.binding.MainThreadHandler;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import dev.nick.imageloader.ImageLoader;
import dev.nick.imageloader.display.DisplayOption;
import dev.nick.imageloader.display.ImageQuality;
import dev.nick.imageloader.display.processor.BlurBitmapProcessor;
import dev.nick.logger.Logger;
import nick.dev.sina.R;
import nick.dev.sina.app.annotation.RetrieveLogger;
import nick.dev.sina.sdk.AccessTokenKeeper;

public class SplashActivity extends ScalpelAutoActivity {

    @RetrieveLogger()
    private Logger mLogger;

    @MainThreadHandler
    Handler mHandler;

    @FindView(id = R.id.imageview)
    ImageView mSplashView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        setContentView(R.layout.activity_splash);

        ImageLoader.getInstance().displayImage("drawable://splash", mSplashView, new DisplayOption.Builder()
        .imageQuality(ImageQuality.RAW)
                .bitmapProcessor(new BlurBitmapProcessor(24))
        .build());

        initAuth();
    }

    void initAuth() {

        final Oauth2AccessToken mAccessToken = AccessTokenKeeper.readAccessToken(this);

        mLogger.debug(mAccessToken);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAccessToken.isSessionValid()) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }, 3000);

    }
}
