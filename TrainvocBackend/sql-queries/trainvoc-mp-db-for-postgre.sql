-- Oda tablosu
CREATE TABLE game_room
(
    room_code              VARCHAR(32) PRIMARY KEY,
    current_question_index INT,
    started                BOOLEAN,
    host_id                VARCHAR(50),
    question_duration      INT,
    option_count           INT,
    level                  VARCHAR(10),
    total_question_count   INT
);

-- Oyuncu tablosu
CREATE TABLE player
(
    id                VARCHAR(50) PRIMARY KEY,
    room_code         VARCHAR(32) REFERENCES game_room (room_code),
    name              VARCHAR(64),
    score             INT,
    correct_count     INT,
    wrong_count       INT,
    total_answer_time BIGINT
);

-- Soru tablosu
CREATE TABLE question
(
    id            VARCHAR(32) PRIMARY KEY,
    text          VARCHAR(255),
    options       TEXT[], -- PostgreSQL array type
    correct_index INT
);

-- Oyuncu cevap tablosu
CREATE TABLE player_answer
(
    id                    SERIAL PRIMARY KEY,
    player_id             VARCHAR(50) REFERENCES player (id),
    question_id           VARCHAR(32),
    selected_option_index INT,
    correct               BOOLEAN,
    answer_time_ms        BIGINT
);

-- Basit bir soru ekle
INSERT INTO question (id, text, options, correct_index)
VALUES ('q1', 'Translate: Apple', ARRAY ['Elma', 'Armut', 'Karpuz', 'Muz'], 0);

-- Oda ve oyuncu ekle
INSERT INTO game_room (room_code, current_question_index, started, host_id, question_duration, option_count, level,
                       total_question_count)
VALUES ('ABCDE', 0, FALSE, 'host1', 60, 4, 'A1', 5);

INSERT INTO player (id, room_code, name, score, correct_count, wrong_count, total_answer_time)
VALUES ('host1', 'ABCDE', 'Ahmet', 0, 0, 0, 0);
