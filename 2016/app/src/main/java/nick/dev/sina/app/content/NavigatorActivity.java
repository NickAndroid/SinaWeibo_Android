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

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.ContextCompatApi24;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.nick.scalpel.Scalpel;
import com.nick.scalpel.annotation.binding.FindView;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import nick.dev.sina.R;

public class NavigatorActivity extends AppCompatActivity {

    BottomBar mBottomBar;

    @FindView(id = R.id.toolbar)
    Toolbar mToolbar;

    FragmentController mController;

    @FindView(id = R.id.coordinator)
    CoordinatorLayout mCoordinator;

    int[] mColors = new int[]{
            R.color.tab_1,
            R.color.tab_2,
            R.color.tab_3,
            R.color.tab_4,
            R.color.tab_5,
    };

    class TabParams {
        int id;
        int colorIdRes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigator);

        Scalpel.getInstance().wire(this);

        setSupportActionBar(mToolbar);

        initPages();

        mBottomBar = BottomBar.attachShy(mCoordinator, findViewById(R.id.container), savedInstanceState);
        mBottomBar.noTopOffset();
        mBottomBar.setItems(R.menu.navigator);
        mBottomBar.setOnMenuTabClickListener(new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                mController.setCurrent(new Random(1000).nextInt(4));
                mToolbar.setBackgroundColor(ContextCompat.getColor(NavigatorActivity.this, mColors[new Random(1000).nextInt(4)]));
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {

            }
        });

        mapColorForTab(0);
        mapColorForTab(1);
        mapColorForTab(2);
        mapColorForTab(3);
        mapColorForTab(4);
    }

    void mapColorForTab(int index) {
        mBottomBar.mapColorForTab(index, ContextCompat.getColor(this, mColors[index]));
    }

    private void initPages() {
        List<Fragment> fragments = new ArrayList<>(4);
        fragments.add(new BaseFragment());
        fragments.add(new BaseFragment());
        fragments.add(new BaseFragment());
        fragments.add(new BaseFragment());
        fragments.add(new BaseFragment());
        mController = new FragmentController(getSupportFragmentManager(), fragments);
    }
}
