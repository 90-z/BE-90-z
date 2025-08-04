-- H2 테스트 데이터베이스용 스키마 (실제 MySQL 스키마와 100% 일치)

-- 기존 테이블이 존재하면 삭제 (외래키 때문에 순서 중요)
DROP TABLE IF EXISTS raffle_winner;
DROP TABLE IF EXISTS raffle;
DROP TABLE IF EXISTS recipe_tag;
DROP TABLE IF EXISTS ingredients;
DROP TABLE IF EXISTS bookmark;
DROP TABLE IF EXISTS search_log;
DROP TABLE IF EXISTS image;
DROP TABLE IF EXISTS participate;
DROP TABLE IF EXISTS recipe;
DROP TABLE IF EXISTS mission;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS tag;

-- 사용자 테이블 생성
CREATE TABLE user (
    user_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    provider VARCHAR(255) NOT NULL,
    nickname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    auth VARCHAR(10) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL
);

-- 레시피 테이블 생성
CREATE TABLE recipe (
    recipe_code BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    recipe_name VARCHAR(255) NOT NULL,
    recipe_content TEXT NOT NULL,
    recipe_calories INT NOT NULL,
    recipe_cook_method VARCHAR(20) NOT NULL,
    recipe_people VARCHAR(20) NOT NULL,
    recipe_time INT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);

-- 미션 테이블 생성
CREATE TABLE mission (
    mission_code BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    mission_content TEXT NOT NULL,
    mission_goal_count INT NOT NULL DEFAULT 1,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL
);

-- 미션 참여 테이블 생성
CREATE TABLE participate (
    participate_code BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    participate_status VARCHAR(20) NOT NULL,
    participate_count INT NOT NULL DEFAULT 0,
    user_id BIGINT NOT NULL,
    mission_code BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(user_id),
    FOREIGN KEY (mission_code) REFERENCES mission(mission_code)
);

-- 이미지 테이블 생성
CREATE TABLE image (
    img_code BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    img_name VARCHAR(255) NOT NULL,
    img_type VARCHAR(255) NOT NULL,
    img_size VARCHAR(255) NOT NULL,
    img_category VARCHAR(20) NOT NULL,
    img_s3url VARCHAR(255) NOT NULL,
    recipe_code BIGINT NULL,
    user_id BIGINT NULL,
    raffle_code BIGINT NULL,
    participate_code BIGINT NULL,
    FOREIGN KEY (recipe_code) REFERENCES recipe(recipe_code),
    FOREIGN KEY (user_id) REFERENCES user(user_id),
    FOREIGN KEY (participate_code) REFERENCES participate(participate_code)
);

-- 검색 로그 테이블 생성
CREATE TABLE search_log (
    search_code BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    search_keyword VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);

-- 북마크 테이블 생성
CREATE TABLE bookmark (
    bookmark_code BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    bookmark_status VARCHAR(10) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    recipe_code BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(user_id),
    FOREIGN KEY (recipe_code) REFERENCES recipe(recipe_code)
);

-- 재료 테이블 생성
CREATE TABLE ingredients (
    ingredients_code BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ingredients_name VARCHAR(255) NOT NULL,
    ingredients_count INT NOT NULL,
    recipe_code BIGINT NOT NULL,
    FOREIGN KEY (recipe_code) REFERENCES recipe(recipe_code)
);

-- 레시피 태그 테이블 생성
CREATE TABLE recipe_tag (
    recipe_tag_code BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    recipe_tag_name VARCHAR(20) NOT NULL,
    recipe_code BIGINT NOT NULL,
    FOREIGN KEY (recipe_code) REFERENCES recipe(recipe_code)
);

-- 태그 테이블 생성
CREATE TABLE tag (
    tag_code BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    tag_name VARCHAR(20) NOT NULL
);

-- 래플 테이블 생성 (AUTO_INCREMENT + Composite Primary Key)
CREATE TABLE raffle (
    raffle_code BIGINT NOT NULL AUTO_INCREMENT,
    participate_code BIGINT NOT NULL,
    raffle_name VARCHAR(255) NOT NULL,
    raffle_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NULL,
    PRIMARY KEY (raffle_code, participate_code),
    FOREIGN KEY (participate_code) REFERENCES participate(participate_code)
);

-- 래플 당첨자 테이블 생성
CREATE TABLE raffle_winner (
    winner_code BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    winner_prize VARCHAR(255) NOT NULL,
    winner_goods_status VARCHAR(20) NOT NULL,
    raffle_code BIGINT NOT NULL,
    participate_code BIGINT NOT NULL,
    FOREIGN KEY (raffle_code, participate_code) REFERENCES raffle(raffle_code, participate_code)
);