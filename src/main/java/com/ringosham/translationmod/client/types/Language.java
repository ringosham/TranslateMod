/*
 * Copyright (C) 2021 Ringosham
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

package com.ringosham.translationmod.client.types;

public class Language {
    private final String name;
    private final String nameUnicode;
    private final String googleCode;
    private final String baiduCode;

    public Language(String name, String nameUnicode, String googleCode, String baiduCode) {
        this.name = name;
        this.nameUnicode = nameUnicode;
        this.googleCode = googleCode;
        this.baiduCode = baiduCode;
    }

    public String getName() {
        return name;
    }

    public String getNameUnicode() {
        return nameUnicode;
    }

    public String getGoogleCode() {
        return googleCode;
    }

    public String getBaiduCode() {
        return baiduCode;
    }
}
