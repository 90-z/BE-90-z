-- H2용 테스트 스키마 생성 (실제 MariaDB 스키마와 일치)

CREATE TABLE IF NOT EXISTS `user` (
    user_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    provider VARCHAR(255) NOT NULL,
    nickname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    auth VARCHAR(20) NOT NULL DEFAULT 'user',
    created_at DATETIME NOT NULL,
    gender VARCHAR(10) NOT NULL,
    birth INT NOT NULL
);

CREATE TABLE IF NOT EXISTS recipe (
    recipe_code BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    recipe_name VARCHAR(255) NOT NULL,
    recipe_made TEXT NOT NULL,
    recipe_content TEXT NOT NULL,
    created_at DATETIME NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES `user`(user_id)
);

CREATE TABLE IF NOT EXISTS mission (
    mission_code BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    mission_name VARCHAR(255) NOT NULL,
    mission_content TEXT NOT NULL,
    mission_status VARCHAR(20) NOT NULL,
    mission_max INT NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    created_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS participate (
    participate_code BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    participate_status VARCHAR(20) NOT NULL,
    user_id BIGINT NOT NULL,
    mission_code BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES `user`(user_id),
    FOREIGN KEY (mission_code) REFERENCES mission(mission_code)
);

CREATE TABLE IF NOT EXISTS raffle (
    raffle_code BIGINT NOT NULL,
    participate_code BIGINT NOT NULL,
    raffle_name VARCHAR(255) NOT NULL,
    raffle_prize_cont TEXT NULL,
    raffle_winner INT NOT NULL DEFAULT 1,
    raffle_date DATETIME NOT NULL,
    created_at DATETIME NULL,
    PRIMARY KEY (raffle_code, participate_code),
    FOREIGN KEY (participate_code) REFERENCES participate(participate_code)
);

CREATE TABLE IF NOT EXISTS image (
    img_code BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    img_name VARCHAR(255) NOT NULL,
    img_type VARCHAR(255) NOT NULL,
    img_size VARCHAR(255) NOT NULL,
    img_category VARCHAR(20) NOT NULL,
    recipe_code BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    raffle_code BIGINT NOT NULL,
    participate_code BIGINT NOT NULL,
    FOREIGN KEY (recipe_code) REFERENCES recipe(recipe_code),
    FOREIGN KEY (user_id) REFERENCES `user`(user_id),
    FOREIGN KEY (raffle_code, participate_code) REFERENCES raffle(raffle_code, participate_code)
);

CREATE TABLE IF NOT EXISTS bookmark (
    bookmark_code BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    bookmark_status VARCHAR(20) NOT NULL,
    created_ate DATETIME NOT NULL,
    user_id BIGINT NOT NULL,
    recipe_code BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES `user`(user_id),
    FOREIGN KEY (recipe_code) REFERENCES recipe(recipe_code)
);

CREATE TABLE IF NOT EXISTS ingredients (
    ingredients_code BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ingredients_name VARCHAR(255) NOT NULL,
    ingredients_count INT NOT NULL,
    recipe_code BIGINT NOT NULL,
    FOREIGN KEY (recipe_code) REFERENCES recipe(recipe_code)
);

CREATE TABLE IF NOT EXISTS recipe_tag (
    recipe_tag_code BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    recipe_tag_name VARCHAR(20) NOT NULL,
    recipe_code BIGINT NOT NULL,
    FOREIGN KEY (recipe_code) REFERENCES recipe(recipe_code)
);

CREATE TABLE IF NOT EXISTS raffle_winner (
    winner_code BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    winner_prize VARCHAR(255) NOT NULL,
    raffle_code BIGINT NOT NULL,
    participate_code BIGINT NOT NULL,
    FOREIGN KEY (raffle_code, participate_code) REFERENCES raffle(raffle_code, participate_code)
);

-- SimpleRaffle 테이블 추가 (테스트용)
CREATE TABLE IF NOT EXISTS simple_raffle (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    raffle_name VARCHAR(255) NOT NULL,
    raffle_prize_cont TEXT NULL,
    raffle_winner INT NOT NULL DEFAULT 1,
    raffle_date DATETIME NOT NULL,
    created_at DATETIME NULL
);