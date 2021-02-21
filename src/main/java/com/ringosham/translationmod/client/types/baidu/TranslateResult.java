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

package com.ringosham.translationmod.client.types.baidu;

import org.apache.commons.lang3.StringEscapeUtils;

public class TranslateResult {
    private String src;
    private String dst;

    public String getSrc() {
        return src;
    }

    public String getDst() {
        return dst;
    }

    //Uses deprecated class but it should work for our case
    //Importing a library just for one method is stupid
    @SuppressWarnings("deprecation")
    public String getSrcDecoded() {
        return StringEscapeUtils.unescapeJson(src);
    }

    @SuppressWarnings("deprecation")
    public String getDstDecoded() {
        return StringEscapeUtils.unescapeJson(dst);
    }
}
