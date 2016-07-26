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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nick.scalpel.Scalpel;
import com.nick.scalpel.annotation.binding.FindView;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;

import java.util.List;

import dev.nick.imageloader.ImageLoader;
import dev.nick.imageloader.display.DisplayOption;
import dev.nick.imageloader.display.animator.FadeInImageAnimator;
import dev.nick.logger.Logger;
import nick.dev.sina.R;
import nick.dev.sina.app.annotation.RetrieveLogger;
import nick.dev.sina.sdk.AccessTokenKeeper;
import nick.dev.sina.sdk.SdkConfig;

public class BaseFragment extends Fragment {

    @FindView(id = R.id.recycler_view)
    RecyclerView mRecyclerView;

    private Oauth2AccessToken mAccessToken;
    private StatusesAPI mStatusesAPI;

    @RetrieveLogger
    Logger mLogger;

    StatusList mStatusList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Scalpel.getInstance().wire(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initStatusApi();
    }

    void initStatusApi() {
        mAccessToken = AccessTokenKeeper.readAccessToken(getActivity());
        mStatusesAPI = new StatusesAPI(getActivity(), SdkConfig.APP_KEY, mAccessToken);
        mStatusesAPI.friendsTimeline(0L, 0L, 50, 1, false, 0, false, mListener);
    }

    private void createAdapter() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new Adapter(mStatusList.statusList));
    }

    public void scrollToTop() {
        mRecyclerView.smoothScrollToPosition(0);
    }

    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
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

    private class Adapter extends RecyclerView.Adapter<FeedViewHolder> {

        private final List<Status> data;

        private DisplayOption mDisplayOption;

        public Adapter(List<Status> data) {
            this.data = data;
            this.mDisplayOption = new DisplayOption.Builder()
                    .animateOnlyNewLoaded()
                    .viewMaybeReused()
                    .imageAnimator(new FadeInImageAnimator())
                    .build();
        }

        @Override
        public FeedViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(getContext()).inflate(R.layout.feed_item, parent, false);
            return new FeedViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final FeedViewHolder holder, final int position) {
            final Status item = data.get(position);
            holder.feedText.setText(item.text);
            holder.userNameView.setText(item.user.name);
            ImageLoader.getInstance().displayImage(item.user.avatar_large, holder.userProfileView, mDisplayOption);
            if (!TextUtils.isEmpty(item.bmiddle_pic)) {
                holder.feedImageView.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(item.bmiddle_pic, holder.feedImageView, mDisplayOption);
            } else {
                holder.feedImageView.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    static class FeedViewHolder extends RecyclerView.ViewHolder {

        @FindView(id = R.id.user_profile_img)
        ImageView userProfileView;
        @FindView(id = R.id.feed_img)
        ImageView feedImageView;
        @FindView(id = R.id.feed_text)
        TextView feedText;
        @FindView(id = R.id.user_profile_name)
        TextView userNameView;

        public FeedViewHolder(final View itemView) {
            super(itemView);
            Scalpel.getInstance().wire(itemView, this);
        }
    }
}
