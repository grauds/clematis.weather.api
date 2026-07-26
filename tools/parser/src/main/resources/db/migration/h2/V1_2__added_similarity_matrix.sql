CREATE TABLE `observation_similarities` (
    `station_id` int NOT NULL,
    `date_a` datetime NOT NULL,
    `date_b` datetime NOT NULL,
    `profile_name` varchar(30) NOT NULL,
    `distance` float NOT NULL,
    PRIMARY KEY (`station_id`, `date_a`, `date_b`, `profile_name`),
    KEY `idx_lookup_b` (`station_id`, `date_b`, `profile_name`),
    CONSTRAINT `fk_obs_a` FOREIGN KEY (`station_id`, `date_a`) REFERENCES `observations` (`weather_station_id`, `date`),
    CONSTRAINT `fk_obs_b` FOREIGN KEY (`station_id`, `date_b`) REFERENCES `observations` (`weather_station_id`, `date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
