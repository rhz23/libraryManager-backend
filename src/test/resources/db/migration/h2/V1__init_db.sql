CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telefone VARCHAR(15) NOT NULL,
    data_cadastro DATE NOT NULL DEFAULT CURRENT_DATE,
    ativo BOOLEAN DEFAULT TRUE
);

CREATE TABLE livros (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    autor VARCHAR(255) NOT NULL,
    isbn VARCHAR(13) NOT NULL UNIQUE,
    data_publicacao DATE NOT NULL,
    categoria VARCHAR(100) NOT NULL,
    disponivel BOOLEAN DEFAULT TRUE
);

CREATE TABLE emprestimos (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    livro_id BIGINT NOT NULL,
    data_emprestimo TIMESTAMP NOT NULL DEFAULT NOW(),
    data_devolucao TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (livro_id) REFERENCES livros(id)
);

CREATE INDEX idx_emprestimo_usuario ON emprestimos(usuario_id);
CREATE INDEX idx_emprestimo_livro ON emprestimos(livro_id);
CREATE INDEX idx_emprestimo_status ON emprestimos(status);
CREATE INDEX idx_livro_categoria ON livros(categoria);