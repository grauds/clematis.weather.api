package org.clematis.weather.repository;

import java.util.Date;

/**
 * This projection carries date and name of the image file
 * Used for calendars
 *
 * @author Anton Troshin
 */
public interface IDateAndPath {

    String getPath();

    Date getDate();
}
