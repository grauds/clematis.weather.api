package jworkspace.images;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import jworkspace.weather.model.WeatherImage;
import jworkspace.weather.service.WeatherImagesImporter;

/**
 * Test image DB sync
 */
public class ImagesSyncTest {

    public static final String PATH = "/home/clematis/weather/images";
    public static final String HEIC_RESOURCE = "2022-07-11 09-58-21.HEIC";
    public static final String IMAGE_RESOURCE = "20191216_060646475_iOS.jpg";
    public static final String NOT_AN_IMAGE_DUMMY = "not_an_image.dummy";
    public static final String DIRECTORY = "directory";

    private static FileSystem fileSystem;

    private static SessionFactory sessionFactory;

    private static Session session = null;

    @BeforeAll
    public static void before() throws IOException {

        // setup file system with resources

        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        Path path = fileSystem.getPath(PATH);
        path = Files.createDirectories(path);
        Files.copy(
            Objects.requireNonNull(ImagesSyncTest.class.getResourceAsStream(HEIC_RESOURCE)),
            path.resolve(HEIC_RESOURCE)
        );
        Files.copy(
            Objects.requireNonNull(ImagesSyncTest.class.getResourceAsStream(IMAGE_RESOURCE)),
            path.resolve(IMAGE_RESOURCE)
        );
        Files.copy(
            Objects.requireNonNull(ImagesSyncTest.class.getResourceAsStream(NOT_AN_IMAGE_DUMMY)),
            path.resolve(NOT_AN_IMAGE_DUMMY)
        );
        Files.copy(
            Objects.requireNonNull(ImagesSyncTest.class.getResourceAsStream(DIRECTORY)),
            path.resolve(DIRECTORY)
        );

        // set up the session factory

        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();
        configuration.addAnnotatedClass(WeatherImage.class);
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:./src/test/resources/db/img");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create");
        sessionFactory = configuration.buildSessionFactory();
        session = sessionFactory.openSession();
    }

    @Test
    void countAndResolveFilesToDb() throws IOException {

        Path path = fileSystem.getPath(PATH);

        try (Stream<Path> stream = Files.list(path)) {
           Assertions.assertEquals(4, stream.count());
        }

        WeatherImagesImporter.loadWeatherImages(path, session);
    }

    @AfterAll
    public static void after() throws IOException {
        fileSystem.close();
        session.close();
        sessionFactory.close();
    }
}
