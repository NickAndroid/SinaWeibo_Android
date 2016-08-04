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

import java.util.HashMap;

public class TransactionCache {

    private HashMap<Object, Object> storage = new HashMap<>();

    public Object get(Object token) {
        return storage.get(token);
    }

    public void put(Object token, Object value) {
        storage.put(token, value);
    }

    public void remove(Object token) {
        storage.remove(token);
    }
}
