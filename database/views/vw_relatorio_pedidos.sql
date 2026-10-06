CREATE OR REPLACE VIEW vw_relatorio_pedidos AS
SELECT 
    p.id AS pedido_id,
    c.nome AS cliente_nome,
    p.data_pedido,
    p.status,
    CASE 
        WHEN SUM(i.quantidade * i.preco_unitario) IS NULL THEN 0.00
        ELSE SUM(i.quantidade * i.preco_unitario)
    END AS total_calculado,
    p.valor_total AS total_registrado
FROM pedidos p
JOIN clientes c ON p.cliente_id = c.id
LEFT JOIN itens_pedido i ON p.id = i.pedido_id
GROUP BY p.id, c.nome, p.data_pedido, p.status, p.valor_total
ORDER BY p.id DESC;
