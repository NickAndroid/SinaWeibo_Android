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

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nick.scalpel.Scalpel;
import com.nick.scalpel.annotation.binding.FindStringArray;
import com.nick.scalpel.annotation.binding.FindView;
import com.nick.scalpel.annotation.opt.AutoRecycle;
import com.nick.scalpel.annotation.opt.RetrieveBean;
import com.nick.scalpel.annotation.request.RequirePermission;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;
import com.sina.weibo.sdk.openapi.models.Status;

import java.util.ArrayList;
import java.util.List;

import dev.nick.imageloader.logger.Logger;
import nick.dev.sina.R;
import nick.dev.sina.app.annotation.RetrieveLogger;
import nick.dev.sina.app.provider.SettingsProvider;
import nick.dev.sina.app.provider.ThemeProvider;
import nick.dev.sina.app.widget.ColorUtils;
import nick.dev.sina.sdk.AuthHelper;

@RequirePermission
public class NavigatorActivity extends AppCompatActivity
        implements TransactionManager, StatusFragment.StatusActionListener {

    final List<TransactionListener> transactionListeners = new ArrayList<>();

    BottomBar mBottomBar;

    @FindView(id = R.id.toolbar)
    Toolbar mToolbar;

    @FindView(id = R.id.coordinator)
    CoordinatorLayout mCoordinator;

    @FindStringArray(id = R.array.tab_colors)
    String[] mColors;

    FragmentController mController;

    @AutoRecycle
    SparseIntArray idMap = new SparseIntArray();

    @RetrieveLogger
    Logger mLogger;

    @RetrieveBean(id = R.id.settings_provider)
    SettingsProvider mSettingsProvider;

    @RetrieveBean(id = R.id.theme_provider)
    ThemeProvider mThemeProvider;

    @RetrieveBean(singleton = true)
    @AutoRecycle
    TransactionCache mTransactionCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!AuthHelper.sessionValid(this)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_navigator);

        Scalpel.getInstance().wire(this);

        setSupportActionBar(mToolbar);

        initUI(savedInstanceState);
    }

    void initUI(Bundle savedInstanceState) {

        initPages();

        mBottomBar = BottomBar.attachShy(mCoordinator, findViewById(R.id.container), savedInstanceState);
        mBottomBar.noTopOffset();
        mBottomBar.setItems(R.menu.navigator_tabs);

        mapColorForTab(R.id.nav_status, 0);
        mapColorForTab(R.id.nav_message, 1);
        mapColorForTab(R.id.nav_hot, 2);
        mapColorForTab(R.id.nav_me, 3);
        mapColorForTab(R.id.nav_config, 4);

        mBottomBar.setOnMenuTabClickListener(new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {

                TransactionSafeFragment from = mController.getCurrent();
                int toId = idMap.get(menuItemId);
                mController.setCurrent(toId);
                TransactionSafeFragment to = mController.getCurrent();

                synchronized (transactionListeners) {
                    for (TransactionListener listener : transactionListeners) {
                        listener.onFragmentTransaction(from, to);
                    }
                }

                int themedColor = Color.parseColor(mColors[idMap.get(menuItemId)]);

                mToolbar.setBackgroundColor(themedColor);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(ColorUtils.colorBurn(themedColor));
                }

                setTitle(to.getTransactionName());
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                TransactionSafeFragment current = mController.getCurrent();
                if (current instanceof Scrollable) {
                    ((Scrollable) current).scrollToTop();
                }
            }
        });

        mBottomBar.selectTabAtPosition(mSettingsProvider.getLastTabIndex(), false);
    }

    void mapColorForTab(int id, int index) {
        idMap.put(id, index);
        mBottomBar.mapColorForTab(index, mColors[index]);
    }

    private void initPages() {
        List<TransactionSafeFragment> fragments = new ArrayList<>(4);
        fragments.add(new StatusFragment());
        fragments.add(new StatusFragment());
        fragments.add(new StatusFragment());
        fragments.add(new StatusFragment());
        fragments.add(new StatusFragment());
        mController = new FragmentController(getSupportFragmentManager(), fragments);
        mController.setDefaultIndex(mSettingsProvider.getLastTabIndex());
    }


    @Override
    public void registerTransactionListener(TransactionListener listener) {
        mLogger.debug(listener);
        synchronized (transactionListeners) {
            transactionListeners.add(listener);
        }
    }

    @Override
    public void unRegisterTransactionListener(TransactionListener listener) {
        mLogger.debug(listener);
        synchronized (transactionListeners) {
            transactionListeners.remove(listener);
        }
    }

    @Override
    public void onStatusImageClick(View view, Status status) {
        mLogger.funcEnter();
        Intent intent = new Intent(this, FeedImageViewerActivity.class);
        intent.putExtra("thumb", status.bmiddle_pic);
        intent.putExtra("url", status.original_pic);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions activityOptions = ActivityOptions.makeScaleUpAnimation(view, 0, view.getHeight() / 2, view.getWidth(), view.getHeight());
            startActivity(intent, activityOptions.toBundle());
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onStatusItemClick(View view, Status status) {

    }

    @Override
    public void onStatusAvatarClick(View view, Status status) {
        Intent intent = new Intent(this, UserViewerActivity.class);
        intent.putExtra("trans_id", status.id);
        mTransactionCache.put(status.id, status);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.feed_back:
                startActivity(new Intent(this, FeedBackActivity.class));
                break;
        }
        return true;
    }
}
