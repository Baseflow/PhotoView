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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LauncherActivity extends AppCompatActivity {

    public static final String[] options = {"Simple Sample", "ViewPager Sample", "Rotation Sample", "Picasso Sample", "Activity Transition Sample"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ItemAdapter());
    }


    private static class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> {
        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return ItemViewHolder.newInstance(parent);
        }

        @Override
        public void onBindViewHolder(final ItemViewHolder holder, int position) {
            holder.bind(options[position]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Class c;

                    switch (holder.getAdapterPosition()) {
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
                            c = PicassoSampleActivity.class;
                            break;
                        case 4:
                            c = ActivityTransitionActivity.class;
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
