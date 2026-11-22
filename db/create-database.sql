-- ==========================================================================
-- Criação do banco de dados do sistema de estoque (PostgreSQL)
-- Execute uma única vez, no pgAdmin ou no psql.
-- ==========================================================================

-- 1) Banco de dados
CREATE DATABASE estoque
    WITH ENCODING 'UTF8'
    TEMPLATE template0;

-- 2) (Opcional) usuário dedicado da aplicação
-- CREATE USER estoque_app WITH PASSWORD 'troque_esta_senha';
-- GRANT ALL PRIVILEGES ON DATABASE estoque TO estoque_app;

-- ==========================================================================
-- 3) Tabelas
-- As tabelas são criadas automaticamente pelo Hibernate
-- (spring.jpa.hibernate.ddl-auto=update) na primeira execução da aplicação.
-- O script abaixo é só para referência / conferência da modelagem.
-- ==========================================================================

CREATE TABLE IF NOT EXISTS categorias (
    id        BIGSERIAL PRIMARY KEY,
    nome      VARCHAR(80) NOT NULL,
    descricao VARCHAR(255),
    CONSTRAINT uk_categoria_nome UNIQUE (nome)
);

CREATE TABLE IF NOT EXISTS produtos (
    id           BIGSERIAL PRIMARY KEY,
    nome         VARCHAR(120) NOT NULL,
    categoria_id BIGINT NOT NULL,
    CONSTRAINT fk_produto_categoria FOREIGN KEY (categoria_id)
        REFERENCES categorias (id)
);

CREATE INDEX IF NOT EXISTS idx_produto_categoria ON produtos (categoria_id);
