package jworkspace.weather.similarity

import jworkspace.weather.model.Observation
import jworkspace.weather.parser.WeatherParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ObservationSimilarityTest {

    @Test
    void testTriviallySimilar() {
        List<Observation> result = read("observation1.csv")
        Assertions.assertEquals(1, result.size())
        Assertions.assertEquals(
                0.0,
                ObservationSimilarityCalculator.between(result.get(0), result.get(0), SimilarityWeights.PHOTO)
        )
    }

    private List<Observation> read(String name) {
        List<Observation> result = new WeatherParser(true, name) {
            protected InputStreamReader getReader() {
                new InputStreamReader(ObservationSimilarityTest.getResourceAsStream(csvFileName), "UTF-16BE")
            }
        }.read()
        result
    }

    @Test
    void testAlmostSimilar2() {
        List<Observation> result = read("observation2.csv")
        Assertions.assertEquals(2, result.size())
        Assertions.assertEquals(
                0.1655913435549812,
                ObservationSimilarityCalculator.between(result.get(0), result.get(1), SimilarityWeights.PHOTO)
        )
        Assertions.assertEquals(
                0.1655913435549812,
                ObservationSimilarityCalculator.between(result.get(1), result.get(0), SimilarityWeights.PHOTO)
        )
    }

    @Test
    void testDifferent() {
        List<Observation> result = read("observation3.csv")
        Assertions.assertEquals(2, result.size())
        Assertions.assertEquals(
                0.8579980398225965,
                ObservationSimilarityCalculator.between(result.get(0), result.get(1), SimilarityWeights.PHOTO)
        )
    }
}
