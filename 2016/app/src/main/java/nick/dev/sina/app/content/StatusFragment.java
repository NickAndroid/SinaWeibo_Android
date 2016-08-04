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

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.nick.scalpel.Scalpel;
import com.nick.scalpel.annotation.binding.FindView;
import com.nick.scalpel.annotation.opt.RetrieveBean;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;

import java.io.File;
import java.util.List;

import dev.nick.imageloader.ImageLoader;
import dev.nick.imageloader.LoaderConfig;
import dev.nick.imageloader.display.DisplayOption;
import dev.nick.imageloader.display.animator.ResAnimator;
import dev.nick.imageloader.loader.network.NetworkPolicy;
import dev.nick.logger.Logger;
import dev.nick.logger.LoggerManager;
import nick.dev.sina.R;
import nick.dev.sina.app.annotation.RetrieveLogger;
import nick.dev.sina.app.provider.SettingsProvider;
import nick.dev.sina.app.utils.BitmapUtils;
import nick.dev.sina.sdk.AccessTokenKeeper;
import nick.dev.sina.sdk.SdkConfig;

public class StatusFragment extends TransactionSafeFragment implements Scrollable {

    @FindView(id = R.id.recycler_view)
    RecyclerView mRecyclerView;

    @RetrieveLogger
    Logger mLogger;

    StatusAdapter mStatusAdapter;

    StatusList mStatusList;

    StatusActionListener mStatusActionListener;

    @RetrieveBean(id = R.id.settings_provider)
    SettingsProvider mSettingsProvider;

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mStatusActionListener = (StatusActionListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_recycler, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Scalpel.getInstance().wire(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getStatus();
    }

    void getStatus() {
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(getActivity());
        StatusesAPI statusesAPI = new StatusesAPI(getActivity(), SdkConfig.APP_KEY, accessToken);
        statusesAPI.friendsTimeline(0L, 0L, 50, 1, false, 0, false, mListener);
    }

    private void createAdapter() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mStatusAdapter = new StatusAdapter(mStatusList.statusList, mStatusActionListener);
        mRecyclerView.setAdapter(mStatusAdapter);
    }

    @Override
    public void onVisible() {
        super.onVisible();
        LoggerManager.getLogger(getClass()).funcEnter();
        if (mStatusAdapter != null) {
            mStatusAdapter.onVisible();
        }
    }

    @Override
    public void onInVisible() {
        super.onInVisible();
        if (mStatusAdapter != null) {
            mStatusAdapter.onInVisible();
        }
    }

    @Override
    public int getTransactionName() {
        return R.string.tab_title_status;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mStatusAdapter != null) {
            mStatusAdapter.onDestroy();
        }
    }

    @Override
    public void scrollToTop() {
        if (mRecyclerView != null) {
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void scrollToBottom() {
        if (mRecyclerView != null) {
            mRecyclerView.smoothScrollToPosition(mStatusAdapter.getItemCount() - 1);
        }
    }

    @Override
    public void onFragmentTransaction(TransactionSafeFragment from, TransactionSafeFragment to) {
        LoggerManager.getLogger(getClass()).debug("from:" + from + "- to" + to);
        if (from == this) {
            onInVisible();
        } else if (to == this) {
            onVisible();
        }
    }

    interface StatusActionListener {
        void onStatusImageClick(View view, Status status);
        void onStatusItemClick(View view, Status status);
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
        @FindView(id = R.id.button_more)
        ImageButton moreBtn;
        @FindView(id = R.id.likes_cnt)
        TextSwitcher likesCntView;
        @FindView(id = R.id.repost_cnt)
        TextView repostCntView;
        @FindView(id = R.id.comment_cnt)
        TextView commentCntView;

        public FeedViewHolder(final View itemView) {
            super(itemView);
            Scalpel.getInstance().wire(itemView, this);
        }
    }

    private class StatusAdapter extends RecyclerView.Adapter<FeedViewHolder> {

        private final List<Status> data;

        private ImageLoader mAvatarLoader;
        private ImageLoader mContentLoader;
        private DisplayOption mContentDisplayOption;
        private DisplayOption mAvatarDisplayOption;

        private StatusActionListener mStatusActionListener;

        public StatusAdapter(List<Status> data, StatusActionListener statusActionListener) {
            this.mStatusActionListener = statusActionListener;
            this.data = data;
            this.mContentLoader = ImageLoader.create(getContext(), LoaderConfig.DEFAULT_CONFIG);
            this.mAvatarLoader = ImageLoader.create(getContext(),
                    new LoaderConfig
                            .Builder()
                            .networkPolicy(new NetworkPolicy
                                    .Builder()
                                    .build()).build());
            this.mContentDisplayOption = new DisplayOption.Builder()
                    .viewMaybeReused()
                    .imageAnimator(ResAnimator.from(getContext(), R.anim.grow_fade_in_from_bottom))
                    .build();
            this.mAvatarDisplayOption = new DisplayOption.Builder()
                    .viewMaybeReused()
                    .imageAnimator(ResAnimator.from(getContext(), R.anim.grow_fade_in_from_bottom))
                    .build();
        }

        public void onDestroy() {
            mAvatarLoader.terminate();
            mContentLoader.terminate();
        }

        public void onVisible() {
            LoggerManager.getLogger(getClass()).funcEnter();
            mAvatarLoader.resume();
            mContentLoader.resume();
        }

        public void onInVisible() {
            mContentLoader.pause();
            mAvatarLoader.pause();
        }

        @Override
        public FeedViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(getContext()).inflate(R.layout.feed_item, parent, false);
            return new FeedViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final FeedViewHolder holder, final int position) {
            final Status item = data.get(position);
            holder.itemView.setOnClickListener(new StatusItemListener(item, mStatusActionListener));
            holder.feedText.setText(item.text);
            holder.userNameView.setText(item.user.name);
            mAvatarLoader.displayImage(item.user.avatar_large, holder.userProfileView, mAvatarDisplayOption);
            if (!TextUtils.isEmpty(item.bmiddle_pic)) {
                holder.feedImageView.setVisibility(View.VISIBLE);
                mContentLoader.displayImage(item.bmiddle_pic, holder.feedImageView, mContentDisplayOption);
            } else {
                holder.feedImageView.setVisibility(View.GONE);
            }
            holder.likesCntView.setText(String.valueOf(item.attitudes_count));
            holder.commentCntView.setText(String.valueOf(item.comments_count));
            holder.repostCntView.setText(String.valueOf(item.reposts_count));
            holder.feedImageView.setOnClickListener(new StatusImageListener(item, mStatusActionListener));
            holder.moreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(getContext(), view);
                    popupMenu.inflate(R.menu.feed_item);
                    popupMenu.setOnMenuItemClickListener(new PopMenuListener(holder.itemView, item));
                    popupMenu.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    class PopMenuListener implements PopupMenu.OnMenuItemClickListener {

        View itemView;
        Status status;

        public PopMenuListener(View itemView, Status status) {
            this.itemView = itemView;
            this.status = status;
        }

        @Override
        public boolean onMenuItemClick(final MenuItem item) {
            itemView.setDrawingCacheEnabled(true);
            itemView.buildDrawingCache();
            Bitmap cache = itemView.getDrawingCache();
            BitmapUtils.saveBitmapAsync(cache, mSettingsProvider.snapShotPath() + File.separator
                            + status.user.screen_name
                            + File.separator
                            + status.idstr
                            + "."
                            + Bitmap.CompressFormat.PNG.name(),
                    new BitmapUtils.ActionListener() {
                        @Override
                        public void onResult(boolean ok) {
                            if (ok) {
                                //noinspection ConstantConditions
                                Snackbar.make(getView(), R.string.status_snapshot_save_result_ok, Snackbar.LENGTH_LONG)
                                        .setAction(R.string.status_snapshot_action_view, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                // FIXME: 2016/8/3 Set action
                                            }
                                        }).show();
                            }
                            itemView.setDrawingCacheEnabled(false);
                            itemView.destroyDrawingCache();
                        }
                    });
            return true;
        }
    }

    class StatusItemListener extends StatusListener {

        public StatusItemListener(Status status, StatusActionListener statusActionListener) {
            super(status, statusActionListener);
        }

        @Override
        public void onClick(View view) {
            statusActionListener.onStatusItemClick(view, status);
        }
    }

    class StatusImageListener extends StatusListener {


        public StatusImageListener(Status status, StatusActionListener statusActionListener) {
            super(status, statusActionListener);
        }

        @Override
        public void onClick(View view) {
            statusActionListener.onStatusImageClick(view, status);
        }
    }

    abstract class StatusListener implements View.OnClickListener {

        Status status;
        StatusActionListener statusActionListener;

        public StatusListener(Status status, StatusActionListener statusActionListener) {
            this.status = status;
            this.statusActionListener = statusActionListener;
        }
    }
}
