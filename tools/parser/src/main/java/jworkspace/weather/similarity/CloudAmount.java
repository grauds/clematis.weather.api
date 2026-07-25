package jworkspace.weather.similarity;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2026 Anton Troshin

   This file is part of Java Workspace.

   This application is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

   This application is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Library General Public License for more details.

   You should have received a copy of the GNU Library General Public
   License along with this application; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   The author may be contacted at:

   anton.troshin@gmail.com
  ----------------------------------------------------------------------------
*/

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Turns the cloud amount prose reported by rp5.ru into a fraction of the sky covered.
 * <p>
 * The same vocabulary is used by both the total cloud cover ({@code N}) and the low cloud
 * amount ({@code Nh}) columns, for example:
 * </p>
 * <ul>
 * <li>{@code no clouds}</li>
 * <li>{@code 10%  or less, but not 0}</li>
 * <li>{@code 20–30%.}</li>
 * <li>{@code 70 – 80%.}</li>
 * <li>{@code 90  or more, but not 100%}</li>
 * <li>{@code 100%.}</li>
 * <li>{@code Sky obscured by fog and/or other meteorological phenomena.}</li>
 * </ul>
 * <p>
 * Every value that carries digits is resolved by averaging the percentages found in the text,
 * which copes with the range forms, the double spaces and the two different dash characters
 * without having to enumerate the exact strings.
 * </p>
 *
 * @author Anton Troshin
 */
public final class CloudAmount {

    /**
     * Sky completely covered.
     */
    public static final float OVERCAST = 1.0f;
    /**
     * Sky completely clear.
     */
    public static final float CLEAR = 0.0f;

    private static final Pattern PERCENTAGE = Pattern.compile("\\d+");

    private static final float PERCENT = 100.0f;

    private static final String NO_CLOUDS = "no clouds";

    private static final String OBSCURED = "obscured";

    private CloudAmount() {
    }

    /**
     * @param text the raw {@code N} or {@code Nh} column value, possibly null or blank
     * @return the covered fraction of the sky in the range {@code 0..1}, or null when unknown
     */
    @SuppressWarnings("checkstyle:ReturnCount")
    public static Float fraction(String text) {

        String normalized = normalize(text);
        if (normalized == null) {
            return null;
        }
        if (normalized.contains(NO_CLOUDS)) {
            return CLEAR;
        }
        if (normalized.contains(OBSCURED)) {
            return OVERCAST;
        }
        return averagePercentage(normalized);
    }

    /**
     * The sky is hidden by fog or another phenomenon rather than by cloud. Visually this is
     * nothing like an overcast sky, even though both cover the whole sky.
     *
     * @param text the raw {@code N} or {@code Nh} column value, possibly null or blank
     * @return true when the report says the sky was obscured
     */
    public static boolean isObscured(String text) {
        String normalized = normalize(text);
        return normalized != null && normalized.contains(OBSCURED);
    }

    private static Float averagePercentage(String normalized) {

        Matcher matcher = PERCENTAGE.matcher(normalized);
        int sum = 0;
        int count = 0;
        while (matcher.find()) {
            sum += Integer.parseInt(matcher.group());
            count++;
        }
        return count == 0 ? null : Math.min(OVERCAST, sum / (float) count / PERCENT);
    }

    private static String normalize(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        return text.toLowerCase(Locale.ENGLISH);
    }
}
