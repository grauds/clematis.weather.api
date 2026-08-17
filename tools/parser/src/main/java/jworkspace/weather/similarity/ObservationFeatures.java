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

import java.time.LocalDateTime;
import java.time.ZoneId;

import jworkspace.weather.model.Observation;

/**
 * The comparable shape of an {@link Observation}: the handful of measurements that say what the
 * weather was actually like, with the free text columns already parsed into numbers.
 * <p>
 * Every field is nullable because the archive is full of gaps - snow depth and soil temperature
 * are only reported at some hours of the day, and whole columns are empty for the earlier years.
 * {@link ObservationSimilarityCalculator} skips the features that are missing on either side rather than
 * treating a gap as a zero.
 * </p>
 *
 * @param temperature     air temperature, degrees Celsius
 * @param dewPointSpread  the gap between air and dew point temperature, degrees Celsius; small
 *                        values mean damp, hazy air, and large ones mean a dry, clear day
 * @param humidity        relative humidity, percent
 * @param pressure        pressure at sea level, millimetres of mercury
 * @param cloudCover      fraction of the sky under cloud, {@code 0..1}
 * @param windSpeed       mean wind speed, metres per second
 * @param windBearing     the direction the wind blows from, degrees, or null when calm or variable
 * @param visibility      horizontal visibility, kilometres
 * @param precipitation   accumulated precipitation, millimetres
 * @param snowDepth       snow lying on the ground, centimetres
 * @param phenomenon      the classified present weather, never null
 * @param dayOfYear       position in the year, which stands in for season and sun height
 * @param hourOfDay       local time of day as a fraction of hours
 *
 * @author Anton Troshin
 */
public record ObservationFeatures(
    Float temperature,
    Float dewPointSpread,
    Float humidity,
    Float pressure,
    Float cloudCover,
    Float windSpeed,
    Float windBearing,
    Float visibility,
    Float precipitation,
    Float snowDepth,
    WeatherPhenomenon phenomenon,
    Integer dayOfYear,
    Integer hourOfDay
) {

    /**
     * Extracts the comparable features of an observation.
     *
     * @param observation the observation to read should not be null
     * @return its features, with a null entry wherever the archive has a gap
     */
    public static ObservationFeatures of(Observation observation) {
        if (observation == null) {
            return new ObservationFeatures(
                null, null, null, null, null, null, null, null, null, null, null, null, null
            );
        }

        Float temperature = observation.getT() == null ? null : observation.getT();
        Float humidity = observation.getU() == null ? null : observation.getU();
        Float pressure = observation.getP() == null ? null : observation.getP();
        Float visibility = observation.getVv() == null ? null : observation.getVv();
        Float precipitation = observation.getRrr() == null ? null : observation.getRrr();
        Float snowDepth = observation.getSss() == null ? null : observation.getSss();

        Float dewPointSpread = null;
        if (observation.getT() != null && observation.getTd() != null) {
            dewPointSpread = observation.getT() - observation.getTd();
        }

        Float windSpeed = parseWindSpeed(observation.getFf());
        Float windBearing = WindDirections.degrees(observation.getDd());
        Float cloudCover = parseCloudCover(observation.getN());

        WeatherPhenomenon phenomenon = WeatherPhenomenon.of(observation.getWw());

        Integer dayOfYear = null;
        Integer hourOfDay = null;
        if (observation.getKey() != null && observation.getKey().getDate() != null) {
            LocalDateTime dateTime = observation.getKey().getDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
            dayOfYear = dateTime.getDayOfYear();
            hourOfDay = dateTime.getHour();
        }

        return new ObservationFeatures(
            temperature,
            dewPointSpread,
            humidity,
            pressure,
            cloudCover,
            windSpeed,
            windBearing,
            visibility,
            precipitation,
            snowDepth,
            phenomenon,
            dayOfYear,
            hourOfDay
        );
    }

    private static Float parseWindSpeed(String ff) {
        if (ff != null && !ff.isBlank()) {
            try {
                return Float.parseFloat(ff.trim());
            } catch (NumberFormatException _) {}
        }
        return null;
    }

    private static Float parseCloudCover(String n) {
        return CloudAmount.fraction(n);
    }
}
