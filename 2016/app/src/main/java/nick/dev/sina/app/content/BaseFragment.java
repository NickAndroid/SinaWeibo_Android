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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nick.scalpel.Scalpel;
import com.nick.scalpel.annotation.binding.FindView;

import java.util.Random;

import dev.nick.imageloader.ImageLoader;
import nick.dev.sina.R;

public class BaseFragment extends Fragment {

    @FindView(id = R.id.imageview)
    ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Scalpel.getInstance().wire(this);
        ImageLoader.getInstance().displayImage(urls[new Random(222).nextInt(urls.length - 1)], imageView);
    }

    final String[] urls = new String[]{
            "http://i.imgur.com/ZXVlev9.jpg",
            "http://i.imgur.com/LT6RmQU.png",
            "http://i.imgur.com/8w0hWDS.jpg",
            "http://i.imgur.com/wCbQpOr.jpg",
            "http://i.imgur.com/rUcXDip.jpg",
            "http://i.imgur.com/bzuhIg4.png",
            "http://i.imgur.com/LsEW9kS.png",
            "http://i.imgur.com/MyAcXe5.png",
            "http://i.imgur.com/PwErqAf.png",
            "http://i.imgur.com/jz1zgXU.png",
            "http://i.imgur.com/8PQ43ov.jpg",
            "http://i.imgur.com/vxAIMJt.png",
            "http://i.imgur.com/ZXVlev9.jpg",
            "http://i.imgur.com/LT6RmQU.png",
            "http://i.imgur.com/8w0hWDS.jpg",
            "http://i.imgur.com/wCbQpOr.jpg",
            "http://i.imgur.com/LsEW9kS.png",
            "http://i.imgur.com/MyAcXe5.png",
            "http://i.imgur.com/PwErqAf.png",
            "http://i.imgur.com/jz1zgXU.png",
            "http://i.imgur.com/moer0PI.jpg",
            "http://i.imgur.com/vRUz3TD.jpg",
            "http://i.imgur.com/ZXVlev9.jpg",
            "http://i.imgur.com/LT6RmQU.png",
            "http://i.imgur.com/8w0hWDS.jpg",
            "http://i.imgur.com/wCbQpOr.jpg",
            "http://i.imgur.com/rUcXDip.jpg",
            "http://i.imgur.com/bzuhIg4.png",
            "http://i.imgur.com/LsEW9kS.png",
            "http://i.imgur.com/MyAcXe5.png",
            "http://i.imgur.com/PwErqAf.png",
            "http://i.imgur.com/jz1zgXU.png",
            "http://i.imgur.com/8PQ43ov.jpg",
            "http://i.imgur.com/vxAIMJt.png",
            "http://i.imgur.com/ZXVlev9.jpg",
            "http://i.imgur.com/LT6RmQU.png",
            "http://i.imgur.com/8w0hWDS.jpg",
            "http://i.imgur.com/wCbQpOr.jpg",
            "http://i.imgur.com/LsEW9kS.png",
            "http://i.imgur.com/MyAcXe5.png",
            "http://i.imgur.com/PwErqAf.png",
            "http://i.imgur.com/jz1zgXU.png",
            "http://i.imgur.com/moer0PI.jpg",
            "http://i.imgur.com/vRUz3TD.jpg",
            "http://i.imgur.com/ZXVlev9.jpg",
            "http://i.imgur.com/LT6RmQU.png",
            "http://i.imgur.com/8w0hWDS.jpg",
            "http://i.imgur.com/wCbQpOr.jpg",
            "http://i.imgur.com/rUcXDip.jpg",
            "http://i.imgur.com/bzuhIg4.png",
            "http://i.imgur.com/LsEW9kS.png",
            "http://i.imgur.com/MyAcXe5.png",
            "http://i.imgur.com/PwErqAf.png",
            "http://i.imgur.com/jz1zgXU.png",
            "http://i.imgur.com/8PQ43ov.jpg",
            "http://i.imgur.com/vxAIMJt.png",
            "http://i.imgur.com/ZXVlev9.jpg",
            "http://i.imgur.com/LT6RmQU.png",
            "http://i.imgur.com/8w0hWDS.jpg",
            "http://i.imgur.com/wCbQpOr.jpg",
            "http://i.imgur.com/LsEW9kS.png",
            "http://i.imgur.com/MyAcXe5.png",
            "http://i.imgur.com/PwErqAf.png",
            "http://i.imgur.com/jz1zgXU.png",
            "http://i.imgur.com/moer0PI.jpg",
            "http://i.imgur.com/vRUz3TD.jpg",
            "http://i.imgur.com/ZXVlev9.jpg",
            "http://i.imgur.com/LT6RmQU.png",
            "http://i.imgur.com/8w0hWDS.jpg",
            "http://i.imgur.com/wCbQpOr.jpg",
            "http://i.imgur.com/rUcXDip.jpg",
            "http://i.imgur.com/bzuhIg4.png",
            "http://i.imgur.com/LsEW9kS.png",
            "http://i.imgur.com/MyAcXe5.png",
            "http://i.imgur.com/PwErqAf.png",
            "http://i.imgur.com/jz1zgXU.png",
            "http://i.imgur.com/8PQ43ov.jpg",
            "http://i.imgur.com/vxAIMJt.png",
            "http://i.imgur.com/ZXVlev9.jpg",
            "http://i.imgur.com/LT6RmQU.png",
            "http://i.imgur.com/8w0hWDS.jpg",
            "http://i.imgur.com/wCbQpOr.jpg",
            "http://i.imgur.com/LsEW9kS.png",
            "http://i.imgur.com/MyAcXe5.png",
            "http://i.imgur.com/PwErqAf.png",
            "http://i.imgur.com/jz1zgXU.png",
            "http://i.imgur.com/moer0PI.jpg",
            "http://i.imgur.com/vRUz3TD.jpg",
            "http://i.imgur.com/ZXVlev9.jpg",
            "http://i.imgur.com/LT6RmQU.png",
            "http://i.imgur.com/8w0hWDS.jpg",
            "http://i.imgur.com/wCbQpOr.jpg",
            "http://i.imgur.com/rUcXDip.jpg",
            "http://i.imgur.com/bzuhIg4.png",
            "http://i.imgur.com/LsEW9kS.png",
            "http://i.imgur.com/MyAcXe5.png",
            "http://i.imgur.com/PwErqAf.png",
            "http://i.imgur.com/jz1zgXU.png",
            "http://i.imgur.com/8PQ43ov.jpg",
            "http://i.imgur.com/vxAIMJt.png",
            "http://i.imgur.com/ZXVlev9.jpg",
            "http://i.imgur.com/LT6RmQU.png",
            "http://i.imgur.com/8w0hWDS.jpg",
            "http://i.imgur.com/wCbQpOr.jpg",
            "http://i.imgur.com/LsEW9kS.png",
            "http://i.imgur.com/MyAcXe5.png",
            "http://i.imgur.com/PwErqAf.png",
            "http://i.imgur.com/jz1zgXU.png",
            "http://i.imgur.com/moer0PI.jpg",
            "http://i.imgur.com/vRUz3TD.jpg"
    };
}
