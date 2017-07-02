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

package at.lukasberger.android.thenexus.utils;

import java.util.ArrayList;
import java.util.List;

import at.lukasberger.android.thenexus.models.Property;

public class PropertiesUtil {

    public static final String propertiesPath = "/system/build.prop";

    public static List<Property> readProperties() {
        List<Property> props = new ArrayList<>();

        int i = 1;
        for (String s : AsyncFileUtils.read(propertiesPath)) {
            // Ignore comments
            if (!s.startsWith("#")) {
                String[] str = s.split("=");
                if (str.length == 2) {
                    props.add(new Property(str[0], str[1], i));
                }
            }
            i++;
        }

        return props;
    }

    public static void addProperty(Property property) {
        AsyncFileUtils.appendSystem(propertiesPath, property.toString());
    }

    public static void updateProperty(Property property) {
        AsyncFileUtils.replaceSystemLine(propertiesPath, property.toString(), property.getLine());
    }

    public static void removeProperty(Property property) {
        AsyncFileUtils.removeSystemLine(propertiesPath, property.getLine());
    }

    public static int getLinesCount() {
        return AsyncFileUtils.getFileLinesCount(propertiesPath);
    }
}
