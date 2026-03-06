/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.time;

import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

/**
 * Custom time zone that contains offset from GMT.
 *
 * @since 3.7
 */
final class GmtTimeZone extends TimeZone {

    private static final int MILLISECONDS_PER_MINUTE = 60 * 1000;
    private static final int MINUTES_PER_HOUR = 60;
    private static final int HOURS_PER_DAY = 24;

    // Serializable!
    static final long serialVersionUID = 1L;

    private static StringBuilder twoDigits(final StringBuilder sb, final int n) {
        return sb.append((char) ('0' + n / 10)).append((char) ('0' + n % 10));
    }
    private final int offset;

    private final String zoneId;

    GmtTimeZone(final boolean negate, final int hours, final int minutes) {
        if (hours >= HOURS_PER_DAY) {
            throw new IllegalArgumentException(hours + " hours out of range");
        }
        if (minutes >= MINUTES_PER_HOUR) {
            throw new IllegalArgumentException(minutes + " minutes out of range");
        }
        // Compute milliseconds using constants to avoid extra arithmetic and allocations
        offset = negate
                ? -(hours * MINUTES_PER_HOUR + minutes) * MILLISECONDS_PER_MINUTE
                :  (hours * MINUTES_PER_HOUR + minutes) * MILLISECONDS_PER_MINUTE;

        // Build zoneId in the form "GMT+hh:mm" (9 chars) using a single char array
        // to avoid multiple StringBuilder allocations and helper calls.
        final String gmt = TimeZones.GMT_ID;
        final int gmtLen = gmt.length();
        final char[] buf = new char[gmtLen + 1 + 2 + 1 + 2]; // e.g. "GMT" + sign + "hh" + ':' + "mm"
        int p = 0;
        gmt.getChars(0, gmtLen, buf, p);
        p += gmtLen;
        buf[p++] = negate ? '-' : '+';
        // hours are in range [0,23], always two digits
        buf[p++] = (char) ('0' + (hours / 10));
        buf[p++] = (char) ('0' + (hours % 10));
        buf[p++] = ':';
        // minutes are in range [0,59], always two digits
        buf[p++] = (char) ('0' + (minutes / 10));
        buf[p++] = (char) ('0' + (minutes % 10));
        zoneId = new String(buf);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GmtTimeZone)) {
            return false;
        }
        final GmtTimeZone other = (GmtTimeZone) obj;
        return offset == other.offset && Objects.equals(zoneId, other.zoneId);
    }

    @Override
    public String getID() {
        return zoneId;
    }

    @Override
    public int getOffset(final int era, final int year, final int month, final int day, final int dayOfWeek, final int milliseconds) {
        return offset;
    }

    @Override
    public int getRawOffset() {
        return offset;
    }

    @Override
    public int hashCode() {
        return Objects.hash(offset, zoneId);
    }

    @Override
    public boolean inDaylightTime(final Date date) {
        return false;
    }

    @Override
    public void setRawOffset(final int offsetMillis) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "[GmtTimeZone id=\"" + zoneId + "\",offset=" + offset + ']';
    }

    @Override
    public boolean useDaylightTime() {
        return false;
    }
}
