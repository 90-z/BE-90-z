-- 기존 테이블 삭제 후 다시 생성

use 90z;

-- 외래키 제약조건 검사 비활성화
SET FOREIGN_KEY_CHECKS = 0;

-- 기존 테이블이 존재하면 삭제 (외래키 참조 순서에 따라 역순으로 삭제)
DROP TABLE IF EXISTS raffle_winner;        -- raffle 테이블을 참조
DROP TABLE IF EXISTS raffle;               -- participate 테이블을 참조
DROP TABLE IF EXISTS image;                -- recipe, user, participate 테이블을 참조
DROP TABLE IF EXISTS recipe_tag;           -- recipe 테이블을 참조
DROP TABLE IF EXISTS ingredients;          -- recipe 테이블을 참조
DROP TABLE IF EXISTS bookmark;             -- user, recipe 테이블을 참조
DROP TABLE IF EXISTS search_log;           -- user 테이블을 참조
DROP TABLE IF EXISTS participate;          -- user, mission 테이블을 참조
DROP TABLE IF EXISTS recipe;               -- user 테이블을 참조
DROP TABLE IF EXISTS mission;              -- 다른 테이블에서 참조됨
DROP TABLE IF EXISTS user;                 -- 다른 테이블에서 참조됨
DROP TABLE IF EXISTS tag;                  -- 독립적인 테이블
DROP TABLE IF EXISTS search_writing;       -- 독립적인 테이블

-- 사용자 테이블 생성
CREATE TABLE IF NOT EXISTS user (
                                    user_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '회원 번호',
                                    provider VARCHAR(255) NOT NULL COMMENT '카카오 아이디',
    nickname VARCHAR(255) NOT NULL COMMENT '닉네임',
    email VARCHAR(255) NOT NULL COMMENT '이메일',
    auth ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER' COMMENT 'USER, ADMIN',
    created_at DATETIME NOT NULL COMMENT '생성 일자'
    );

-- 미션 테이블 생성
CREATE TABLE IF NOT EXISTS mission (
                                       mission_code BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '미션번호',
                                       mission_content TEXT NOT NULL COMMENT '미션내용',
                                       mission_goal_count INT NOT NULL DEFAULT 1 COMMENT '미션 목표 횟수',
                                       start_date DATETIME NOT NULL COMMENT '시작일자',
                                       end_date DATETIME NOT NULL COMMENT '종료일자',
                                       created_at DATETIME NOT NULL COMMENT '생성일자'
);

-- 레시피 테이블 생성
CREATE TABLE IF NOT EXISTS recipe (
                                      recipe_code BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '레시피번호',
                                      recipe_name VARCHAR(255) NOT NULL COMMENT '레시피제목',
    recipe_content TEXT NOT NULL COMMENT '레시피내용',
    recipe_calories INT NOT NULL COMMENT '칼로리',
    recipe_cook_method VARCHAR(20) NOT NULL COMMENT '요리방식',
    recipe_people ENUM('SINGLE', 'DOUBLE', 'FAMILY') NOT NULL COMMENT 'SINGLE, DOUBLE, FAMILY',
    recipe_time INT NOT NULL COMMENT '소요시간',
    created_at DATETIME NOT NULL COMMENT '생성일자',
    user_id BIGINT NOT NULL COMMENT '회원 번호',
    FOREIGN KEY (user_id) REFERENCES user(user_id)
    );

-- 미션 참여 테이블 생성
CREATE TABLE IF NOT EXISTS participate (
                                           participate_code BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '미션참여번호',
                                           participate_status ENUM('PART_BEFORE', 'PART_IN', 'PART_COMPLETE') NOT NULL COMMENT 'PART_BEFORE, PART_IN, PART_COMPLETE',
    participate_count INT NOT NULL DEFAULT 0 COMMENT '미션 참여 횟수',
    user_id BIGINT NOT NULL COMMENT '회원 번호',
    mission_code BIGINT NOT NULL COMMENT '미션 번호',
    FOREIGN KEY (user_id) REFERENCES user(user_id),
    FOREIGN KEY (mission_code) REFERENCES mission(mission_code)
    );

-- 래플 테이블 생성
CREATE TABLE IF NOT EXISTS raffle (
                                      raffle_code BIGINT NOT NULL AUTO_INCREMENT COMMENT '래플번호',
                                      participate_code BIGINT NOT NULL COMMENT '미션참여번호',
                                      raffle_name VARCHAR(255) NOT NULL COMMENT '래플 이름',
    raffle_date DATETIME NOT NULL COMMENT '추첨일',
    created_at DATETIME NULL COMMENT '생성일자',
    PRIMARY KEY (raffle_code, participate_code),
    FOREIGN KEY (participate_code) REFERENCES participate(participate_code)
    );

-- 이미지 테이블 생성
CREATE TABLE IF NOT EXISTS image (
                                     img_code BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '이미지번호',
                                     img_name VARCHAR(255) NOT NULL COMMENT '원본사진이름',
    img_type VARCHAR(255) NOT NULL COMMENT '파일타입',
    img_size VARCHAR(255) NOT NULL COMMENT '파일크기',
    img_category ENUM('USER', 'RECIPE', 'GIFT') NOT NULL COMMENT 'USER, RECIPE, GIFT',
    img_s3url VARCHAR(255) NOT NULL COMMENT 'S3 링크',
    recipe_code BIGINT NULL COMMENT '레시피번호',
    user_id BIGINT NULL COMMENT '회원번호',
    raffle_code BIGINT NULL COMMENT '래플번호',
    participate_code BIGINT NULL COMMENT '미션참여번호',
    FOREIGN KEY (recipe_code) REFERENCES recipe(recipe_code),
    FOREIGN KEY (user_id) REFERENCES user(user_id),
    FOREIGN KEY (participate_code) REFERENCES participate(participate_code)
    );

-- 검색 로그 테이블 생성
CREATE TABLE IF NOT EXISTS search_log (
                                          search_code BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '검색 기록 번호',
                                          search_keyword VARCHAR(255) NOT NULL COMMENT '검색키워드',
    created_at DATETIME NOT NULL COMMENT '생성 일자',
    user_id BIGINT NOT NULL COMMENT '회원 번호',
    FOREIGN KEY (user_id) REFERENCES user(user_id)
    );

-- 북마크 테이블 생성
CREATE TABLE IF NOT EXISTS bookmark (
                                        bookmark_code BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '북마크 번호',
                                        created_at DATETIME NOT NULL COMMENT '생성 일자',
                                        user_id BIGINT NOT NULL COMMENT '회원 번호',
                                        recipe_code BIGINT NOT NULL COMMENT '레시피번호',
                                        FOREIGN KEY (user_id) REFERENCES user(user_id),
    FOREIGN KEY (recipe_code) REFERENCES recipe(recipe_code)
    );

-- 재료 테이블 생성
CREATE TABLE IF NOT EXISTS ingredients (
                                           ingredients_code BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '재료번호',
                                           ingredients_name VARCHAR(255) NOT NULL COMMENT '재료',
    ingredients_count VARCHAR(255) NOT NULL COMMENT '재료 수량',
    recipe_code BIGINT NOT NULL COMMENT '레시피 번호',
    FOREIGN KEY (recipe_code) REFERENCES recipe(recipe_code)
    );

-- 레시피 태그 테이블 생성
CREATE TABLE IF NOT EXISTS recipe_tag (
                                          recipe_tag_code BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '레시피 태그 번호',
                                          recipe_tag_name VARCHAR(20) NOT NULL COMMENT '레시피 태그명',
    recipe_code BIGINT NOT NULL COMMENT '레시피번호',
    FOREIGN KEY (recipe_code) REFERENCES recipe(recipe_code)
    );

-- 래플 당첨자 테이블 생성
CREATE TABLE IF NOT EXISTS raffle_winner (
                                             winner_code BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '래플 당첨자 번호',
                                             winner_prize VARCHAR(255) NOT NULL COMMENT '당첨자 상품',
    winner_goods_status ENUM('BEFORE_RECEIPT', 'AFTER_RECEIPT') NOT NULL COMMENT 'BEFORE_RECEIPT, AFTER_RECEIPT',
    raffle_code BIGINT NOT NULL COMMENT '래플번호',
    participate_code BIGINT NOT NULL COMMENT '미션참여번호',
    FOREIGN KEY (raffle_code, participate_code) REFERENCES raffle(raffle_code, participate_code)
    );

-- 태그 테이블 생성
CREATE TABLE IF NOT EXISTS tag (
                                   tag_code BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '태그번호',
                                   tag_name VARCHAR(20) NOT NULL COMMENT '태그명'
    );

-- 검색 라이팅 테이블 생성
CREATE TABLE IF NOT EXISTS search_writing (
                                              search_writing_code BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '검색 라이팅 번호',
                                              search_writing_ment VARCHAR(255) NOT NULL COMMENT '검색 라이팅 멘트'
    );

INSERT INTO tag VALUES (1, '전자레인지');
INSERT INTO tag VALUES (2, '에어프라이어');
INSERT INTO tag VALUES (3, '프라이팬');
INSERT INTO tag VALUES (4, '냄비');
INSERT INTO tag VALUES (5, '오븐');
INSERT INTO tag VALUES (6, '️불 없이 요리');