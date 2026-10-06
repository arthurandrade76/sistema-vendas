CREATE TABLE itens_pedido (
    id SERIAL PRIMARY KEY,
    pedido_id INT NOT NULL,
    produto_id INT NOT NULL,
    quantidade INT NOT NULL CHECK (quantidade > 0),
    preco_unitario NUMERIC(10, 2) NOT NULL,
    CONSTRAINT fk_item_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_produto FOREIGN KEY (produto_id) REFERENCES produtos(id) ON DELETE RESTRICT
);
