USE quiz;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for leaderboards
-- ----------------------------
DROP TABLE IF EXISTS `leaderboards`;
CREATE TABLE `leaderboards` (
                                `id` int NOT NULL AUTO_INCREMENT,
                                `score` int DEFAULT NULL,
                                `quiz_id` int DEFAULT NULL,
                                `user_id` int DEFAULT NULL,
                                `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                                `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

-- ----------------------------
-- Records of leaderboards
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for quizzes
-- ----------------------------
DROP TABLE IF EXISTS `quizzes`;
CREATE TABLE `quizzes` (
                           `id` int NOT NULL AUTO_INCREMENT,
                           `topic` varchar(255) COLLATE utf8mb3_unicode_ci DEFAULT NULL,
                           `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                           `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

-- ----------------------------
-- Records of quizzes
-- ----------------------------
BEGIN;
INSERT INTO `quizzes` (`id`, `topic`, `created_at`, `updated_at`) VALUES (1, 'English', '2024-12-22 14:47:11', '2024-12-22 14:47:11');
INSERT INTO `quizzes` (`id`, `topic`, `created_at`, `updated_at`) VALUES (2, 'Mathematics', '2024-12-23 05:23:21', '2024-12-23 05:23:21');
INSERT INTO `quizzes` (`id`, `topic`, `created_at`, `updated_at`) VALUES (3, 'Physics', '2024-12-24 13:36:39', '2024-12-24 13:36:39');
COMMIT;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
                         `id` int NOT NULL AUTO_INCREMENT,
                         `username` varchar(255) COLLATE utf8mb3_unicode_ci DEFAULT NULL,
                         `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                         `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

-- ----------------------------
-- Records of users
-- ----------------------------
BEGIN;
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
