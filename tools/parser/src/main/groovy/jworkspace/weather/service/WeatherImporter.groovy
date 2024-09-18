package jworkspace.weather.service

import jworkspace.weather.model.Observation
import jworkspace.weather.parser.WeatherParser
import org.hibernate.Session

/**
 * @author Anton Troshin
 */
class WeatherImporter {


    static int loadWeatherData(Session session) {

        if (session != null && session.isOpen()) {

            List<Observation> result = new WeatherParser(true, "27612.01.02.2005.01.02.2006.1.0.0.en.unic.00000000.csv").read()

            result.addAll(new WeatherParser(true, "27612.01.02.2006.01.02.2010.1.0.0.en.unic.00000000.csv").read())
            result.addAll(new WeatherParser(true, "27612.01.02.2010.01.02.2015.1.0.0.en.unic.00000000.csv").read())
            result.addAll(new WeatherParser(true, "27612.01.02.2015.01.02.2021.1.0.0.en.unic.00000000.csv").read())
            result.addAll(new WeatherParser(true, "27612.01.02.2021.01.02.2022.1.0.0.en.unic.00000000.csv").read())
            result.addAll(new WeatherParser(true, "27612.01.02.2022.01.02.2023.1.0.0.en.unic.00000000.csv").read())
            result.addAll(new WeatherParser(true, "27612.01.02.2023.01.02.2024.1.0.0.en.unic.00000000.csv").read())
            result.addAll(new WeatherParser(true, "27612.01.02.2024.18.09.2024.1.0.0.en.unic.00000000.csv").read())

            for (Observation observation : result) {
                Observation existing = session.find(Observation.class, observation.getKey())
                if (existing == null) {
                    session.saveOrUpdate(observation)
                }
            }

            return result.size()
        }
        return 0
    }
}
