package jworkspace.weather;
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

import jworkspace.weather.model.Observation;
import jworkspace.weather.service.WeatherImporter;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author Anton Troshin
 */
public class ImportPipelineTest {

    private static final int TEST_DATA_SIZE = 63237;

    private static SessionFactory sessionFactory;

    private static Session session = null;

    @BeforeAll
    public static void before() {
        // set up the session factory
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(Observation.class);
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:./src/test/resources/db/mem");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create");
        sessionFactory = configuration.buildSessionFactory();
        session = sessionFactory.openSession();
    }

    @Test
    public void test() {
        int imported = WeatherImporter.loadWeatherData(session);
        Assertions.assertEquals(ImportPipelineTest.TEST_DATA_SIZE, imported);
    }

    @AfterAll
    public static void after() {
        session.close();
        sessionFactory.close();
    }
}

