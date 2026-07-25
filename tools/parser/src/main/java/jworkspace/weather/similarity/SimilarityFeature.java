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

import java.util.function.BiFunction;

/**
 * One comparable aspect of an observation, together with the rule for measuring how far apart two
 * observations are on it.
 * <p>
 * Each rule returns a difference already normalised to {@code 0..1}, where {@code 1} means
 * "as different as this aspect gets", or null when either observation has nothing to say.
 * The scale constant is the gap in real units at which a feature is considered fully different:
 * beyond it the difference is clamped, so that one extreme outlier cannot swamp everything else.
 * </p>
 *
 * @author Anton Troshin
 */
public enum SimilarityFeature {

    /**
     * Air temperature. A dozen degrees apart is a different kind of day.
     */
    TEMPERATURE((a, b) -> linear(a.temperature(), b.temperature(), Config.TEMPERATURE_SCALE)),

    /**
     * How far the air is from saturation, which shows up as haze and softness in the light.
     */
    DEW_POINT_SPREAD((a, b) -> linear(a.dewPointSpread(), b.dewPointSpread(), Config.DEW_POINT_SPREAD_SCALE)),

    /**
     * Relative humidity.
     */
    HUMIDITY((a, b) -> linear(a.humidity(), b.humidity(), Config.HUMIDITY_SCALE)),

    /**
     * Pressure at sea level.
     */
    PRESSURE((a, b) -> linear(a.pressure(), b.pressure(), Config.PRESSURE_SCALE)),

    /**
     * How much of the sky is under cloud.
     */
    CLOUD_COVER((a, b) -> linear(a.cloudCover(), b.cloudCover(), Config.CLOUD_COVER_SCALE)),

    /**
     * Mean wind speed.
     */
    WIND_SPEED((a, b) -> linear(a.windSpeed(), b.windSpeed(), Config.WIND_SPEED_SCALE)),

    /**
     * The bearing the wind blows from, compared the short way round the compass.
     */
    WIND_BEARING((a, b) ->
        circular(a.windBearing(), b.windBearing(), WindDirections.FULL_CIRCLE, Config.WIND_BEARING_SCALE)),

    /**
     * Horizontal visibility, compressed because the difference between fog and light haze matters
     * far more than the difference between a clear day and a very clear one.
     */
    VISIBILITY((a, b) -> compressed(a.visibility(), b.visibility(), Config.VISIBILITY_SCALE)),

    /**
     * Accumulated precipitation, compressed for the same reason: dry against barely wet is the
     * interesting step.
     */
    PRECIPITATION((a, b) -> compressed(a.precipitation(), b.precipitation(), Config.PRECIPITATION_SCALE)),

    /**
     * Snow lying on the ground, compressed because bare ground against a first covering changes
     * everything, while deep against deeper changes very little.
     */
    SNOW_DEPTH((a, b) -> compressed(a.snowDepth(), b.snowDepth(), Config.SNOW_DEPTH_SCALE)),

    /**
     * The classified present weather: rain, snow, fog, thunder and how hard it was coming down.
     */
    PHENOMENON((a, b) -> a.phenomenon() == null || b.phenomenon() == null
        ? null : a.phenomenon().distance(b.phenomenon())),

    /**
     * Position in the year, compared the short way round so that the end of December sits next to
     * the start of January. Stands in for season, foliage and how high the sun gets.
     */
    DAY_OF_YEAR((a, b) -> circular(a.dayOfYear(), b.dayOfYear(), Config.DAYS_IN_YEAR, Config.DAY_OF_YEAR_SCALE)),

    /**
     * Time of day, compared the short way round midnight. Stands in for the light.
     */
    HOUR_OF_DAY((a, b) -> circular(a.hourOfDay(), b.hourOfDay(), Config.HOURS_IN_DAY, Config.HOUR_OF_DAY_SCALE));

    private static final class Config {
        private static final double TEMPERATURE_SCALE = 12.0;
        private static final double DEW_POINT_SPREAD_SCALE = 6.0;
        private static final double HUMIDITY_SCALE = 25.0;
        private static final double PRESSURE_SCALE = 12.0;
        private static final double CLOUD_COVER_SCALE = 0.5;
        private static final double WIND_SPEED_SCALE = 4.0;
        private static final double WIND_BEARING_SCALE = 90.0;
        private static final double VISIBILITY_SCALE = 10.0;
        private static final double PRECIPITATION_SCALE = 5.0;
        private static final double SNOW_DEPTH_SCALE = 15.0;
        private static final double DAY_OF_YEAR_SCALE = 30.0;
        private static final double HOUR_OF_DAY_SCALE = 3.0;

        private static final double DAYS_IN_YEAR = 366.0;
        private static final double HOURS_IN_DAY = 24.0;

        private static final double IDENTICAL = 0.0;
        private static final double COMPLETELY_DIFFERENT = 1.0;
    }

    private final BiFunction<ObservationFeatures, ObservationFeatures, Double> rule;

    SimilarityFeature(BiFunction<ObservationFeatures, ObservationFeatures, Double> rule) {
        this.rule = rule;
    }

    /**
     * @param first  the features of one observation, never null
     * @param second the features of the other, never null
     * @return how far apart they are on this feature, from {@code 0} to {@code 1}, or null when
     *         either side does not report it
     */
    public Double difference(ObservationFeatures first, ObservationFeatures second) {
        return rule.apply(first, second);
    }

    private static Double linear(Number first, Number second, double scale) {
        if (first == null || second == null) {
            return null;
        }
        return clamp(Math.abs(first.doubleValue() - second.doubleValue()) / scale);
    }

    /**
     * Compares on a square root scale, so that steps away from zero weigh more than steps taken
     * further out. Negative inputs are not expected for the quantities that use this.
     */
    private static Double compressed(Number first, Number second, double scale) {
        if (first == null || second == null) {
            return null;
        }
        double difference = Math.abs(root(first.doubleValue()) - root(second.doubleValue()));
        return clamp(difference / root(scale));
    }

    /**
     * Compares two points on a cycle the short way round, so that 350 degrees and 10 degrees are
     * 20 degrees apart rather than 340.
     */
    private static Double circular(Number first, Number second, double period, double scale) {
        if (first == null || second == null) {
            return null;
        }
        double difference = Math.abs(first.doubleValue() - second.doubleValue()) % period;
        return clamp(Math.min(difference, period - difference) / scale);
    }

    private static double root(double value) {
        return Math.sqrt(Math.max(Config.IDENTICAL, value));
    }

    private static double clamp(double difference) {
        return Math.clamp(difference, Config.IDENTICAL, Config.COMPLETELY_DIFFERENT);
    }
}
