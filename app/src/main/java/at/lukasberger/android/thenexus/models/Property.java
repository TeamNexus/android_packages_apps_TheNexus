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

package at.lukasberger.android.thenexus.models;

public class Property {

    private String title;
    private String value;
    private int line;

    public Property() {
        // Nothing here
    }

    public Property(String title, String value, int line) {
        this.title = title;
        this.value = value;
        this.line = line;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getLine() {
        return this.line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    @Override
    public String toString() {
        return title + "=" + value;
    }
}
