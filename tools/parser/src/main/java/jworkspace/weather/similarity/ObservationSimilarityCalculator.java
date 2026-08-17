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

import jworkspace.weather.model.Observation;

/**
 * Calculates the total mathematical distance between two weather observations.
 * <p>
 * This class maps the domain fields from an {@link Observation} to the properties
 * consumed by {@link SimilarityFeature}, then computes a weighted average distance.
 * It tolerates data gaps by ignoring missing features and dividing by the total weight
 * of the features that were actually evaluated.
 * </p>
 */
public final class ObservationSimilarityCalculator {

    private ObservationSimilarityCalculator() {}

    /**
     * @param first   the first weather observation, never null
     * @param second  the second weather observation, never null
     * @param weights the weighting configuration to apply, never null
     * @return the total distance between {@code 0} (identical) and {@code 1} (completely different),
     *         or null when there are no matching features to compare
     */
    public static Double between(Observation first, Observation second, SimilarityWeights weights) {

        ObservationFeatures featuresFirst = ObservationFeatures.of(first);
        ObservationFeatures featuresSecond = ObservationFeatures.of(second);

        double accumulatedDistance = 0.0;
        double accumulatedWeight = 0.0;

        for (SimilarityFeature feature : SimilarityFeature.values()) {
            double weight = weights.weightOf(feature);
            if (weight <= 0.0) {
                continue;
            }

            Double distance = feature.difference(featuresFirst, featuresSecond);
            if (distance != null) {
                accumulatedDistance += distance * weight;
                accumulatedWeight += weight;
            }
        }

        return accumulatedWeight == 0.0 ? null : accumulatedDistance / accumulatedWeight;
    }

}
