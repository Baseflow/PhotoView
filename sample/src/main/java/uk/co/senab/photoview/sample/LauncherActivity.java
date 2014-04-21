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
package uk.co.senab.photoview.sample;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LauncherActivity extends ListActivity {

    public static final String[] options = {"Simple Sample", "ViewPager Sample", "Rotation Sample", "Android Universal Image Loader"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Class c;

        switch (position) {
            default:
            case 0:
                c = SimpleSampleActivity.class;
                break;
            case 1:
                c = ViewPagerActivity.class;
                break;
            case 2:
                c = RotationSampleActivity.class;
                break;
            case 3:
                c = AUILSampleActivity.class;
                break;
        }

        startActivity(new Intent(this, c));
    }

}
