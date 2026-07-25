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
import java.util.Date;

import org.apache.commons.lang3.math.NumberUtils;

import jworkspace.weather.model.Observation;

/**
 * The comparable shape of an {@link Observation}: the handful of measurements that say what the
 * weather was actually like, with the free text columns already parsed into numbers.
 * <p>
 * Every field is nullable because the archive is full of gaps - snow depth and soil temperature
 * are only reported at some hours of the day, and whole columns are empty for the earlier years.
 * {@link ObservationSimilarity} skips the features that are missing on either side rather than
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
     * @param observation the observation to read, never null
     * @return its features, with a null entry wherever the archive has a gap
     */
    public static ObservationFeatures of(Observation observation) {

        LocalDateTime moment = toLocalDateTime(observation.getKey() == null ? null : observation.getKey().getDate());

        return new ObservationFeatures(
            observation.getT(),
            spread(observation.getT(), observation.getTd()),
            observation.getU(),
            observation.getP(),
            CloudAmount.fraction(observation.getN()),
            parseFloat(observation.getFf()),
            WindDirections.degrees(observation.getDd()),
            observation.getVv(),
            observation.getRrr(),
            observation.getSss(),
            WeatherPhenomenon.of(observation.getWw()),
            moment == null ? null : moment.getDayOfYear(),
            moment == null ? null : moment.getHour()
        );
    }

    private static Float spread(Float temperature, Float dewPoint) {
        return temperature == null || dewPoint == null ? null : temperature - dewPoint;
    }

    private static Float parseFloat(String text) {
        return NumberUtils.isParsable(text) ? Float.parseFloat(text) : null;
    }

    private static LocalDateTime toLocalDateTime(Date date) {
        return date == null ? null : LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
}
