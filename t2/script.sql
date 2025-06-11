-- Cria a base de dados se ela ainda não existir
CREATE DATABASE IF NOT EXISTS blueddit_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Seleciona a base de dados para usar nos comandos seguintes
USE blueddit_db;

-- Tabela de Usuários
CREATE TABLE IF NOT EXISTS usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de Sublueddits (Comunidades)
CREATE TABLE IF NOT EXISTS sublueddit (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de Posts (CORRIGIDA)
CREATE TABLE IF NOT EXISTS post (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fk_usuario INT NOT NULL,
    fk_sublueddit INT NOT NULL,
    descricao TEXT NOT NULL,
    data_publicacao DATETIME NOT NULL, -- Corrigido para DATETIME
    upvote INT DEFAULT 0,
    downvote INT DEFAULT 0,
    FOREIGN KEY (fk_usuario) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (fk_sublueddit) REFERENCES sublueddit(id) ON DELETE CASCADE
);

-- Tabela de Comentários (CORRIGIDA)
CREATE TABLE IF NOT EXISTS comentario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fk_usuario INT NOT NULL,
    fk_post INT NOT NULL,
    texto TEXT NOT NULL,
    data_publicacao DATETIME NOT NULL, -- Coluna adicionada e corrigida para DATETIME
    upvote INT DEFAULT 0,
    downvote INT DEFAULT 0,
    FOREIGN KEY (fk_usuario) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (fk_post) REFERENCES post(id) ON DELETE CASCADE
);

-- Tabela para a relação N-N de Inscrições (Usuário <-> Sublueddit)
CREATE TABLE IF NOT EXISTS inscricao (
    fk_usuario INT NOT NULL,
    fk_sublueddit INT NOT NULL,
    data_inscricao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (fk_usuario, fk_sublueddit),
    FOREIGN KEY (fk_usuario) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (fk_sublueddit) REFERENCES sublueddit(id) ON DELETE CASCADE
);

-- Tabela para registrar os votos em Posts e Comentários
CREATE TABLE IF NOT EXISTS voto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fk_usuario INT NOT NULL,
    fk_post INT,
    fk_comentario INT,
    tipo_voto INT NOT NULL,
    data_voto TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- Garante que um usuário só pode votar uma vez em cada post
    UNIQUE KEY idx_usuario_post (fk_usuario, fk_post),
    -- Garante que um usuário só pode votar uma vez em cada comentário
    UNIQUE KEY idx_usuario_comentario (fk_usuario, fk_comentario),
    FOREIGN KEY (fk_usuario) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (fk_post) REFERENCES post(id) ON DELETE CASCADE,
    FOREIGN KEY (fk_comentario) REFERENCES comentario(id) ON DELETE CASCADE,
    -- Constraint para garantir que o voto seja ou 1 ou -1
    CONSTRAINT chk_tipo_voto CHECK (tipo_voto IN (1, -1)),
    -- Constraint para garantir que o voto esteja associado a um post OU a um comentário
    CONSTRAINT chk_conteudo_votado CHECK ((fk_post IS NOT NULL AND fk_comentario IS NULL) OR (fk_post IS NULL AND fk_comentario IS NOT NULL))
);
