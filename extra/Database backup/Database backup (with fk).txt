CREATE TABLE `table_user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `gender` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `table_client` (
  `client_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `ride_requested` tinyint(4) DEFAULT NULL,
  `location` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `destination` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `corresponding_driver_user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`client_id`),
  FOREIGN KEY (`user_id`) REFERENCES table_user(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `table_driver` (
  `driver_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `license_plate` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ride_started` tinyint(4) DEFAULT NULL,
  `corresponding_client_user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`driver_id`),
  FOREIGN KEY (`user_id`) REFERENCES table_user(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `table_rides` (
  `ride_id` int(11) NOT NULL AUTO_INCREMENT,
  `location` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `destination` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `requesting_client_id` int(11) DEFAULT NULL,
  `accepted_driver_id` int(11) DEFAULT NULL,
  `ride_cancelled` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`ride_id`),
  FOREIGN KEY (`requesting_client_id`) REFERENCES table_user(`user_id`),
  FOREIGN KEY (`accepted_driver_id`) REFERENCES table_user(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

