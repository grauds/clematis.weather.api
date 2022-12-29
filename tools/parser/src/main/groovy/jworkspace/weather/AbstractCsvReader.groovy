package jworkspace.weather
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
import groovy.transform.TupleConstructor

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.lang3.math.NumberUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.google.common.base.Stopwatch

@TupleConstructor
abstract class AbstractCsvReader<T> {

    public static final Logger LOG = LoggerFactory.getLogger(AbstractCsvReader.class)

    boolean hasHeader

    String csvFileName

    abstract List<T> mapToItems(CSVParser records)

    protected List<T> read() {
        def stopwatch = Stopwatch.createStarted()
        LOG.info(String.format("Started reading %s", csvFileName))

        def parser = getParser()
        def items = mapToItems(parser)

        LOG.info(String.format("Finished reading %s, number of records: %s, time: %s", csvFileName, items.size(), stopwatch.stop()))
        items
    }

    protected CSVParser getParser() {
        Reader fileReader = getReader()
        def parser = getFormat().parse(fileReader)
        parser
    }

    protected static Float safeParseFloat(String text) {
        return NumberUtils.isParsable(text) ? Float.parseFloat(text) : null
    }

    protected static Float safeParseInteger(String text) {
        return NumberUtils.isParsable(text) ? Integer.parseInt(text) : null
    }

    protected InputStreamReader getReader() {
        new InputStreamReader(AbstractCsvReader.getResourceAsStream(csvFileName))
    }

    private CSVFormat getFormat() {
        def format = CSVFormat.DEFAULT
        format = format.withDelimiter((char)';')
        hasHeader ? format.withFirstRecordAsHeader() : format
    }
}