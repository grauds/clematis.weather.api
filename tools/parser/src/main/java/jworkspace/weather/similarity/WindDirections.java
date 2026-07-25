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

import java.util.EnumMap;
import java.util.Map;

import jworkspace.weather.model.WindDirection;

/**
 * Places {@link WindDirection} on the compass rose so two directions can be compared as angles.
 * <p>
 * {@link WindDirection#NO_WIND} and {@link WindDirection#VARIABLE_WIND} have no angle by
 * definition. Neither do the four values that name two opposing quadrants at once, such as
 * {@link WindDirection#EAST_NORTHWEST} - they are not points of the compass and are treated as
 * "direction not comparable" rather than guessed at.
 * </p>
 *
 * @author Anton Troshin
 */
public final class WindDirections {

    /**
     * A full turn of the compass, in degrees.
     */
    public static final float FULL_CIRCLE = 360.0F;

    private static final WindDirection[] COMPASS_ROSE = {
        WindDirection.NORTH,
        WindDirection.NORTH_NORTHEAST,
        WindDirection.NORTH_EAST,
        WindDirection.EAST_NORTHEAST,
        WindDirection.EAST,
        WindDirection.EAST_SOUTHEAST,
        WindDirection.SOUTH_EAST,
        WindDirection.SOUTH_SOUTHEAST,
        WindDirection.SOUTH,
        WindDirection.SOUTH_SOUTHWEST,
        WindDirection.SOUTH_WEST,
        WindDirection.WEST_SOUTHWEST,
        WindDirection.WEST,
        WindDirection.WEST_NORTHWEST,
        WindDirection.NORTH_WEST,
        WindDirection.NORTH_NORTHWEST,
    };

    private static final Map<WindDirection, Float> DEGREES = new EnumMap<>(WindDirection.class);

    static {
        for (int point = 0; point < COMPASS_ROSE.length; point++) {
            DEGREES.put(COMPASS_ROSE[point], point * FULL_CIRCLE / COMPASS_ROSE.length);
        }
    }

    private WindDirections() {
    }

    /**
     * @param direction the reported wind direction, possibly null
     * @return the bearing the wind blows from in the range {@code 0..360}, or null when the
     *         direction is absent, calm, variable or otherwise not a point of the compass
     */
    public static Float degrees(WindDirection direction) {
        return direction == null ? null : DEGREES.get(direction);
    }
}
