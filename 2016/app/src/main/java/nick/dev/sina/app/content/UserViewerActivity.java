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

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.nick.scalpel.ScalpelAutoActivity;
import com.nick.scalpel.annotation.binding.FindView;
import com.nick.scalpel.annotation.opt.RetrieveBean;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.openapi.models.User;

import dev.nick.imageloader.ImageLoader;
import dev.nick.imageloader.display.DisplayOption;
import dev.nick.imageloader.display.animator.FadeInImageAnimator;
import dev.nick.imageloader.display.processor.BlurBitmapProcessor;
import dev.nick.imageloader.loader.result.BitmapResult;
import dev.nick.logger.Logger;
import dev.nick.logger.LoggerManager;
import nick.dev.sina.R;
import nick.dev.sina.app.annotation.RetrieveLogger;
import nick.dev.sina.app.content.adapter.StatusAdapter;
import nick.dev.sina.app.opt.DisplayListenerStub;
import nick.dev.sina.app.widget.ColorUtils;
import nick.dev.sina.sdk.AccessTokenKeeper;
import nick.dev.sina.sdk.SdkConfig;

public class UserViewerActivity extends ScalpelAutoActivity {


    @RetrieveBean(singleton = true)
    TransactionCache mTransactionCache;

    @FindView(id = R.id.backdrop)
    ImageView backDropView;

    @FindView(id = R.id.user_profile_img)
    ImageView userProfileView;

    StatusesAPI statusesAPI;

    StatusList mStatusList;

    @FindView(id = R.id.recycler_view)
    RecyclerView mRecyclerView;

    StatusAdapter mStatusAdapter;

    @RetrieveLogger
    Logger mLogger;
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            mLogger.verbose(response);
            if (!TextUtils.isEmpty(response)) {
                if (response.startsWith("{\"statuses\"")) {
                    StatusList statuses = StatusList.parse(response);
                    if (statuses != null && statuses.total_number > 0) {
                        mStatusList = statuses;
                        createAdapter();
                    }
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            mLogger.trace("onWeiboException", e);
        }
    };

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_viewer);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Status status = (Status) mTransactionCache.get(getIntent().getStringExtra("trans_id"));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbar.setTitle(status.user.screen_name);

        initApi();

        ImageLoader.shared(this).displayImage(status.user.avatar_hd, backDropView, new DisplayOption.Builder()
                .bitmapProcessor(new BlurBitmapProcessor(20))
                .imageAnimator(new FadeInImageAnimator()).build(), new DisplayListenerStub() {
            @Override
            public void onComplete(@Nullable BitmapResult result) {
                super.onComplete(result);
                if (result != null && result.result != null) {
                    Palette.from(result.result).generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            LoggerManager.getLogger(FeedImageViewerActivity.class).funcEnter();
                            int defColor = ContextCompat.getColor(UserViewerActivity.this, R.color.primary);
                            int themeColor = palette.getLightVibrantColor(defColor);
                            int themeColorDark = ColorUtils.colorBurn(palette.getLightVibrantColor(defColor));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                getWindow().setStatusBarColor(themeColorDark);
                                getWindow().setNavigationBarColor(themeColorDark);
                                toolbar.setBackgroundColor(themeColor);
                            }
                        }
                    });
                }
            }
        });

        ImageLoader.shared(this).displayImage(status.user.avatar_hd, userProfileView);

        requestStatus(status.user);
    }

    void initApi() {
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(this);
        statusesAPI = new StatusesAPI(this, SdkConfig.APP_KEY, accessToken);
    }

    void requestStatus(User user) {
        mLogger.verbose(user);
        statusesAPI.userTimeline(0L, 0L, 20, 1, false, 0, false, mListener);//// FIXME: 2016/8/4 Right user
    }

    private void createAdapter() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mStatusAdapter = new StatusAdapter(this, mStatusList.statusList, new StatusAdapter.StatusActionListener() {
            @Override
            public void onStatusImageClick(View view, Status status) {

            }

            @Override
            public void onStatusItemClick(View view, Status status) {

            }
        });
        mRecyclerView.setAdapter(mStatusAdapter);
    }
}
