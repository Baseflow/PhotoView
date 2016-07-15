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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uk.co.senab.photoview.sample.k3b.ViewPagerActivityFromMediaDBEx;

public class LauncherActivity extends AppCompatActivity {

    // Note: LauncherActivity.ItemAdapter.onBindViewHolder(..,option) corresponds to LauncherActivity.options[option]
    public static final String[] options = {"Simple Sample", "ViewPager Sample (static)",
            "ViewPager from media-db", "ViewPager from media-db enhanced PhotoView",
            "Rotation Sample", "Picasso Sample"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ItemAdapter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        PhotoViewSampleApp.onPrepareOptionsMenu(menu);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (PhotoViewSampleApp.onOptionsItemSelected(item)) return true;

        return super.onOptionsItemSelected(item);
    }

    // Note: LauncherActivity.ItemAdapter.onBindViewHolder(..,option) corresponds to LauncherActivity.options[option]
    private static class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> {
        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return ItemViewHolder.newInstance(parent);
        }

        @Override
        public void onBindViewHolder(final ItemViewHolder holder, final int position) {
            holder.bind(options[position]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                            c = ViewPagerActivityFromMediaDB.class;
                            break;
                        case 3:
                            c = ViewPagerActivityFromMediaDBEx.class;
                            break;
                        case 4:
                            c = RotationSampleActivity.class;
                            break;
                        case 5:
                            c = PicassoSampleActivity.class;
                            break;
                    }

                    Context context = holder.itemView.getContext();
                    context.startActivity(new Intent(context, c));
                }
            });
        }

        @Override
        public int getItemCount() {
            return options.length;
        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        public static ItemViewHolder newInstance(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_item, parent, false);
            return new ItemViewHolder(view);
        }

        public TextView mTextTitle;

        public ItemViewHolder(View view) {
            super(view);
            mTextTitle = (TextView) view.findViewById(R.id.title);
        }

        private void bind(String title) {
            mTextTitle.setText(title);
        }
    }
}
