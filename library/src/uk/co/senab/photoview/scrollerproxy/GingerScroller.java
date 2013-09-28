/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
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
 *******************************************************************************/
package uk.co.senab.photoview.scrollerproxy;

import android.annotation.TargetApi;
import android.content.Context;
import android.widget.OverScroller;

@TargetApi(9)
public class GingerScroller extends ScrollerProxy {

    protected OverScroller mScroller;
    private boolean mFirstScroll = false;

    public GingerScroller(Context context) {
        mScroller = new OverScroller(context);
    }

    @Override
    public boolean computeScrollOffset() {
        // Workaround for first scroll returning 0 for the direction of the edge it hits.
        // Simply recompute values.
        if (mFirstScroll) {
            mScroller.computeScrollOffset();
            mFirstScroll = false;
        }
        return mScroller.computeScrollOffset();
    }

    @Override
    public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY,
                      int overX, int overY) {
        mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, overX, overY);
    }

    @Override
    public void forceFinished(boolean finished) {
        mScroller.forceFinished(finished);
    }

    @Override
    public boolean isFinished() {
        return mScroller.isFinished();
    }

    @Override
    public int getCurrX() {
        return mScroller.getCurrX();
    }

    @Override
    public int getCurrY() {
        return mScroller.getCurrY();
    }
}