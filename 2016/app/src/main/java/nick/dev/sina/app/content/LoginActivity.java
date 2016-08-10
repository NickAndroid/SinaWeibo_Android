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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;

import com.nick.scalpel.ScalpelAutoActivity;
import com.nick.scalpel.annotation.binding.FindView;
import com.nick.scalpel.annotation.binding.OnClick;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import dev.nick.imageloader.logger.Logger;
import nick.dev.sina.R;
import nick.dev.sina.app.annotation.CalledByScalpel;
import nick.dev.sina.app.annotation.RetrieveLogger;
import nick.dev.sina.sdk.AccessTokenKeeper;
import nick.dev.sina.sdk.SdkConfig;

public class LoginActivity extends ScalpelAutoActivity {

    @FindView(id = R.id.login_button)
    @OnClick(action = "onRequestLogin")
    Button mLoginBtn;

    @RetrieveLogger()
    private Logger mLogger;

    private SsoHandler mSsoHandler;

    private Oauth2AccessToken mAccessToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initAuth();
    }

    void initAuth() {
        AuthInfo mAuthInfo = new AuthInfo(this, SdkConfig.APP_KEY, SdkConfig.REDIRECT_URL, SdkConfig.SCOPE);
        mSsoHandler = new SsoHandler(this, mAuthInfo);
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        mLogger.debug(mAccessToken);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @CalledByScalpel
    void onRequestLogin() {
        mSsoHandler.authorize(new AuthListener());
    }

    void onAuthTokenValid() {
        startActivity(new Intent(this, NavigatorActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            mLogger.funcEnter();
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                AccessTokenKeeper.writeAccessToken(LoginActivity.this, mAccessToken);
                mLogger.info("Auth valid.");
                onAuthTokenValid();
            } else {
                String code = values.getString("code");
                String message = getString(R.string.weibosdk_demo_toast_auth_failed);
                mLogger.error(String.format("Auth failure, code %s, message %s", code, message));
            }
        }

        @Override
        public void onCancel() {
            mLogger.funcEnter();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            mLogger.trace("onWeiboException", e);
        }
    }
}
