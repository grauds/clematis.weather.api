package jworkspace.weather.parser

import jworkspace.weather.model.Observation
import jworkspace.weather.model.ObservationKey
import jworkspace.weather.model.WindDirection

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999 - 2022 Anton Troshin

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

import java.text.DateFormat
import java.text.SimpleDateFormat

import org.apache.commons.csv.CSVParser
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings

/**
 * # Weather station Moscow, Russia, WMO_ID=27612,selection from 01.02.2005 till 01.02.2006, all days
 * # Encoding: Unicode
 * # The data is provided by the website 'Reliable Prognosis', rp5.ru
 * # If you use the data, please indicate the name of the website.
 * # For meteorological parameters see the address http://rp5.ru/archive.php?wmo_id=27612&lang=en
 * #
 *
 * @param fileName
 * @return array of observations
 */
class WeatherParser extends AbstractCsvReader<Observation> {

    public static final Logger LOG = LoggerFactory.getLogger(WeatherParser.class)

    static String DATE_FORMAT = "dd.MM.yyyy hh:mm"

    static DateFormat df = new SimpleDateFormat(DATE_FORMAT)

    WeatherParser(boolean hasHeader, String csvFileName) {
        super(hasHeader, csvFileName)
    }

    protected InputStreamReader getReader() {
        new InputStreamReader(AbstractCsvReader.getResourceAsStream(csvFileName), "UTF-16BE")
    }

    @Override
    @SuppressFBWarnings("SE_NO_SERIALVERSIONID")
    List<Observation> mapToItems(CSVParser parser) {
        int counter = 0

        return parser.records.stream().filter( {
            return it.recordNumber > 8
        }).map({
            // "T";"Po";"P";"Pa";"U";"DD";"Ff";"ff10";"ff3";"N";"WW";"W1";"W2";"Tn";"Tx";"Cl";"Nh";
            // "H";"Cm";"Ch";"VV";"Td";"RRR";"tR";"E";"Tg";"E'";"sss"
            int pos = 0
            try {
                return new Observation(
                        key: new ObservationKey(
                            weatherStationId: 27612,
                            date: df.parse(it.get(pos++))
                        ),
                        t: safeParseFloat(it.get(pos++)),
                        pO: safeParseFloat(it.get(pos++)),
                        p: safeParseFloat(it.get(pos++)),
                        pA: safeParseFloat(it.get(pos++)),
                        u: safeParseFloat(it.get(pos++)),
                        dd: WindDirection.fromString(it.get(pos++)),
                        ff: it.get(pos++),
                        ff10: it.get(pos++),
                        ff3: it.get(pos++),
                        n: it.get(pos++),
                        ww: it.get(pos++),
                        w1: it.get(pos++),
                        w2: it.get(pos++),
                        tn: safeParseFloat(it.get(pos++)),
                        tx: safeParseFloat(it.get(pos++)),
                        cl: it.get(pos++),
                        nh: it.get(pos++),
                        h: it.get(pos++),
                        cm: it.get(pos++),
                        ch: it.get(pos++),
                        vv: safeParseFloat(it.get(pos++)),
                        td: safeParseFloat(it.get(pos++)),
                        rrr: safeParseFloat(it.get(pos++)),
                        tr: safeParseFloat(it.get(pos++)),
                        e: it.get(pos++),
                        tg: safeParseFloat(it.get(pos++)),
                        eApostrophe: it.get(pos++),
                        sss: safeParseFloat(it.get(pos))
                )
            } catch (IllegalArgumentException ignored) {
                // skip the record
                counter++
                String record = "Unparseable record " + it.values
                String message = String.format(" in %s column: %s" , pos, ignored.getMessage())
                LOG.error(record + message)
                println record + message
                return null
            }
        }).collect()
    }
}
