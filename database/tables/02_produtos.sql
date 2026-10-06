CREATE TABLE produtos (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    preco_unitario NUMERIC(10, 2) NOT NULL CHECK (preco_unitario > 0),
    quantidade_estoque INT NOT NULL DEFAULT 0 CHECK (quantidade_estoque >= 0)
);
