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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import at.lukasberger.android.thenexus.R;
import at.lukasberger.android.thenexus.models.Property;
import at.lukasberger.android.thenexus.utils.PropertiesUtil;

public class PropertiesAdapter extends RecyclerView.Adapter<PropertiesAdapter.ViewHolder> {

    private Context context;
    private List<Property> properties;

    public PropertiesAdapter(Context context, List<Property> properties) {
        this.context = context;
        this.properties = properties;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, value;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.prop_title);
            value = (TextView) view.findViewById(R.id.prop_value);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            final Property property = properties.get(getAdapterPosition());

            if (property.getTitle().startsWith("ro.")) {
                Toast.makeText(view.getContext(), context.getString(R.string.fragment_properties_toast_readonly), Toast.LENGTH_SHORT).show();
                return;
            }

            new MaterialDialog.Builder(context)
                    .title(context.getString(R.string.dialog_properties_title, property.getTitle()))
                    .positiveText(context.getString(R.string.dialog_properties_save_button))
                    .negativeText(context.getString(R.string.dialog_properties_remove_button))
                    .neutralText(context.getString(R.string.dialog_properties_cancel_button))
                    .input(context.getString(R.string.dialog_properties_input_value), property.getValue(), new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            // Nothing here
                        }
                    })
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            String value = dialog.getInputEditText().getText().toString();
                            if (!value.trim().isEmpty()) {
                                property.setValue(value);
                                PropertiesUtil.updateProperty(property);
                                notifyDataSetChanged();
                            }
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            PropertiesUtil.removeProperty(property);
                            properties.remove(property);
                            notifyDataSetChanged();
                        }
                    }).show();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.property_list_row, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Property property = properties.get(position);
        holder.title.setText(property.getTitle());
        holder.value.setText(property.getValue());
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }
}
