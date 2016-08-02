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
import android.support.v4.app.Fragment;

public abstract class TransactionSafeFragment extends Fragment implements TransactionListener {

    TransactionManager transactionManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        transactionManager = (TransactionManager) getActivity();
        transactionManager.registerTransactionListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        transactionManager.unRegisterTransactionListener(this);
    }

    protected TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void onVisible() {
    }

    public void onInVisible() {
    }
}
