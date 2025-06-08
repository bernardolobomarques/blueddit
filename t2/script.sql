CREATE DATABASE blueddit_db;
USE blueddit_db;

CREATE TABLE usuario (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL
);

CREATE TABLE sublueddit (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE post (
    id INT PRIMARY KEY AUTO_INCREMENT,
    fk_usuario INT NOT NULL,
    fk_sublueddit INT NOT NULL,
    data_publicacao VARCHAR(10) NOT NULL, -- ou DATE se você for usar LocalDate no banco
    descricao TEXT NOT NULL,
    upvote INT DEFAULT 0,
    downvote INT DEFAULT 0,
    FOREIGN KEY (fk_usuario) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (fk_sublueddit) REFERENCES sublueddit(id) ON DELETE CASCADE
);

CREATE TABLE comentario (
    id INT PRIMARY KEY AUTO_INCREMENT,
    fk_usuario INT NOT NULL,
    fk_post INT NOT NULL,
    texto TEXT NOT NULL,
    upvote INT DEFAULT 0, -- Se Comentario também for votável
    downvote INT DEFAULT 0, -- Se Comentario também for votável
    FOREIGN KEY (fk_usuario) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (fk_post) REFERENCES post(id) ON DELETE CASCADE
);