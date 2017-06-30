/*
 * The Nexus - ROM-Control for ROMs made by TeamNexus
 * Copyright (C) 2017  TeamNexus, Luca Polesel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.lukasberger.android.thenexus.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import at.lukasberger.android.thenexus.R;
import at.lukasberger.android.thenexus.models.Property;

public class PropertiesAdapter extends RecyclerView.Adapter<PropertiesAdapter.MyViewHolder> {

    private List<Property> properties;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, value;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.prop_title);
            value = (TextView) view.findViewById(R.id.prop_value);
        }
    }

    public PropertiesAdapter(List<Property> properties) {
        this.properties = properties;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.property_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Property property = properties.get(position);
        holder.title.setText(property.getTitle());
        holder.value.setText(property.getValue());
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }
}
