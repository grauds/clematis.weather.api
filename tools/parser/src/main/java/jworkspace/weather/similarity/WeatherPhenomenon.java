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

/**
 * The present weather ({@code WW}) column of rp5.ru is free English prose describing what the
 * observer saw, for example {@code "Continuous fall of snowflakes, slight at time of observation. "}
 * or {@code "Shower(s) of hail, or of rain and hail. "}. This classifies that prose onto a few
 * independent axes so two reports can be compared numerically.
 * <p>
 * The axes are deliberately independent rather than a single category, which avoids having to
 * maintain a square distance matrix over the couple of a hundred distinct phrases in the archive.
 * </p>
 *
 * @param precipitation what was falling, on a liquid-to-solid axis
 * @param obscuration   what was reducing visibility
 * @param intensity     how strong it was when the report says so
 * @param thunderstorm  whether a thunderstorm or lightning was reported
 * @param shower        whether the precipitation was of a showery character
 *
 * @author Anton Troshin
 */
public record WeatherPhenomenon(
    Precipitation precipitation,
    Obscuration obscuration,
    Intensity intensity,
    boolean thunderstorm,
    boolean shower
) {

    /**
     * Nothing of note was reported. Almost half of the archive looks like this.
     */
    public static final WeatherPhenomenon NONE =
        new WeatherPhenomenon(Precipitation.NONE, Obscuration.NONE, Intensity.UNKNOWN, false, false);

    /**
     * How much of the overall difference each axis accounts for. These sum to one.
     */
    private static final double PRECIPITATION_SHARE = 0.50;
    private static final double OBSCURATION_SHARE = 0.25;
    private static final double INTENSITY_SHARE = 0.15;
    private static final double THUNDERSTORM_SHARE = 0.10;

    private static final double MATCH = 0.0;
    private static final double MISMATCH = 1.0;

    /**
     * The shares that remain once no intensity is known on either side.
     */
    private static final double SHARES_WITHOUT_INTENSITY =
        PRECIPITATION_SHARE + OBSCURATION_SHARE + THUNDERSTORM_SHARE;

    /**
     * Reports often carry a trailing measurement clause such as
     * {@code "Diameter of glaze deposit is 3 mm."} or {@code "Maximum diameter of hailstones is 3 mm."}.
     * It describes a deposit rather than the weather itself, and the stray "hailstones" in it would
     * otherwise be read as a hail report, so it is cut off before classifying.
     */
    private static final String MEASUREMENT_CLAUSE = "diameter of";

    /**
     * {@code "snow grains (with or without fog)"} and {@code "diamond dust (with or without fog)"}
     * are not fog reports; the parenthetical is removed so it cannot be mistaken for one.
     */
    private static final String CONDITIONAL_FOG = "with or without fog";

    /**
     * Classifies a raw {@code WW} value.
     *
     * @param text the reported present weather, possibly null or blank
     * @return the classification, never null; {@link #NONE} when nothing was reported
     */
    public static WeatherPhenomenon of(String text) {

        String report = normalize(text);
        if (report.isEmpty()) {
            return NONE;
        }
        return new WeatherPhenomenon(
            precipitationOf(report),
            obscurationOf(report),
            intensityOf(report),
            report.contains("thunderstorm") || report.contains("lightning"),
            report.contains("shower")
        );
    }

    /**
     * @param other the phenomenon to compare against, never null
     * @return how different the two reports are, from {@code 0} for identical to {@code 1}
     */
    public double distance(WeatherPhenomenon other) {

        double total = PRECIPITATION_SHARE * precipitation.distance(other.precipitation)
            + OBSCURATION_SHARE * obscuration.distance(other.obscuration)
            + THUNDERSTORM_SHARE * (thunderstorm == other.thunderstorm ? MATCH : MISMATCH);

        Double intensityDistance = intensity.distance(other.intensity);
        if (intensityDistance == null) {
            // neither report states an intensity, so that share is spread over the known axes
            return total / SHARES_WITHOUT_INTENSITY;
        }
        return total + INTENSITY_SHARE * intensityDistance;
    }

    @SuppressWarnings("checkstyle:ReturnCount")
    private static Precipitation precipitationOf(String report) {

        if (report.contains("hail") || report.contains("snow pellets")) {
            return Precipitation.HAIL;
        }
        boolean snow = report.contains("snow")
            || report.contains("diamond dust")
            || report.contains("ice pellets");
        boolean rain = report.contains("rain");
        boolean drizzle = report.contains("drizzle");

        if (snow) {
            return rain || drizzle ? Precipitation.RAIN_AND_SNOW : Precipitation.SNOW;
        }
        if (rain) {
            return Precipitation.RAIN;
        }
        return drizzle ? Precipitation.DRIZZLE : Precipitation.NONE;
    }

    @SuppressWarnings("checkstyle:ReturnCount")
    private static Obscuration obscurationOf(String report) {

        if (report.contains("fog")) {
            return Obscuration.FOG;
        }
        if (report.contains("mist")) {
            return Obscuration.MIST;
        }
        if (report.contains("haze") || report.contains("duststorm") || report.contains("sandstorm")) {
            return Obscuration.HAZE;
        }
        return Obscuration.NONE;
    }

    @SuppressWarnings("checkstyle:ReturnCount")
    private static Intensity intensityOf(String report) {

        if (report.contains("heavy")) {
            return Intensity.HEAVY;
        }
        if (report.contains("moderate")) {
            return Intensity.MODERATE;
        }
        return report.contains("slight") ? Intensity.SLIGHT : Intensity.UNKNOWN;
    }

    private static String normalize(String text) {

        if (text == null) {
            return "";
        }
        String report = text.toLowerCase(Locale.ENGLISH).replace(CONDITIONAL_FOG, "");

        int measurement = report.indexOf(MEASUREMENT_CLAUSE);
        if (measurement >= 0) {
            report = report.substring(0, measurement);
        }
        return report.trim();
    }

    /**
     * What was falling out of the sky, ordered from liquid to solid so that neighbouring
     * constants describe visually similar weather.
     */
    public enum Precipitation {

        NONE, DRIZZLE, RAIN, RAIN_AND_SNOW, SNOW, HAIL;

        private static final double SPAN = HAIL.ordinal() - DRIZZLE.ordinal();

        @SuppressWarnings("checkstyle:ReturnCount")
        double distance(Precipitation other) {
            if (this == other) {
                return MATCH;
            }
            if (this == NONE || other == NONE) {
                // dry weather against any precipitation is as different as this axis gets
                return MISMATCH;
            }
            return Math.abs(ordinal() - other.ordinal()) / SPAN;
        }
    }

    /**
     * What was cutting visibility, ordered by how much of it was lost.
     */
    public enum Obscuration {

        NONE, HAZE, MIST, FOG;

        private static final double SPAN = FOG.ordinal() - NONE.ordinal();

        double distance(Obscuration other) {
            return Math.abs(ordinal() - other.ordinal()) / SPAN;
        }
    }

    /**
     * How strong the reported weather was. Plenty of reports name no intensity at all, and
     * {@link #UNKNOWN} keeps that distinct from a genuinely slight one.
     */
    public enum Intensity {

        UNKNOWN, SLIGHT, MODERATE, HEAVY;

        private static final double SPAN = HEAVY.ordinal() - SLIGHT.ordinal();

        /**
         * @return the difference, or null when either side did not state an intensity
         */
        Double distance(Intensity other) {
            if (this == UNKNOWN || other == UNKNOWN) {
                return null;
            }
            return Math.abs(ordinal() - other.ordinal()) / SPAN;
        }
    }
}
