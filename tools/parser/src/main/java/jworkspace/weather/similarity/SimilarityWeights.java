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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * How much each feature counts towards the difference between two observations.
 * <p>
 * A weight of zero drops the feature entirely. Weights need not sum to anything in particular -
 * {@link ObservationSimilarity} divides by the weight it actually managed to use, so that
 * observations with gaps in the archive are still comparable.
 * </p>
 * @param values the weights for each feature
 */
public record SimilarityWeights(Map<SimilarityFeature, Double> values) {

    /**
     * Tuned for picking a photograph that looks like the requested day.
     * <p>
     * Season and time of day dominate. They set the sun height, the length of the shadows,
     * and whether there are leaves on the trees - a mild overcast morning in April and one in
     * October are the same weather but nothing like the same picture. Snow on the ground, cloud
     * cover, and what was falling out of the sky come next. Pressure and wind direction are almost
     * invisible in a photograph and are kept only as tiebreakers.
     * </p>
     */
    public static final SimilarityWeights PHOTO = weights(
        Map.entry(SimilarityFeature.DAY_OF_YEAR, 3.0),
        Map.entry(SimilarityFeature.SNOW_DEPTH, 2.5),
        Map.entry(SimilarityFeature.CLOUD_COVER, 2.5),
        Map.entry(SimilarityFeature.PHENOMENON, 2.0),
        Map.entry(SimilarityFeature.HOUR_OF_DAY, 2.0),
        Map.entry(SimilarityFeature.TEMPERATURE, 1.5),
        Map.entry(SimilarityFeature.VISIBILITY, 1.0),
        Map.entry(SimilarityFeature.DEW_POINT_SPREAD, 0.8),
        Map.entry(SimilarityFeature.PRECIPITATION, 0.8),
        Map.entry(SimilarityFeature.WIND_SPEED, 0.5),
        Map.entry(SimilarityFeature.HUMIDITY, 0.5),
        Map.entry(SimilarityFeature.PRESSURE, 0.2),
        Map.entry(SimilarityFeature.WIND_BEARING, 0.2)
    );

    /**
     * Every measurement counts the same. Use this to compare weather as weather, without the
     * bias towards what a camera would have seen.
     */
    public static final SimilarityWeights BALANCED = uniform();

    /**
     * @param feature the feature to look up
     * @return its weight, or zero when the profile does not use it
     */
    public double weightOf(SimilarityFeature feature) {
        return values.getOrDefault(feature, 0.0);
    }

    @SafeVarargs
    private static SimilarityWeights weights(Map.Entry<SimilarityFeature, Double>... entries) {

        Map<SimilarityFeature, Double> values = new LinkedHashMap<>();
        for (Map.Entry<SimilarityFeature, Double> entry : entries) {
            values.put(entry.getKey(), entry.getValue());
        }
        return new SimilarityWeights(Map.copyOf(values));
    }

    private static SimilarityWeights uniform() {

        Map<SimilarityFeature, Double> values = new LinkedHashMap<>();
        for (SimilarityFeature feature : SimilarityFeature.values()) {
            values.put(feature, 1.0);
        }
        return new SimilarityWeights(Map.copyOf(values));
    }
}
