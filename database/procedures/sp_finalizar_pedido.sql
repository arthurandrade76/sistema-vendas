CREATE OR REPLACE PROCEDURE sp_finalizar_pedido(p_pedido_id INT)
LANGUAGE plpgsql
AS $$
DECLARE
    item RECORD;
BEGIN

    FOR item IN 
        SELECT produto_id, quantidade 
        FROM itens_pedido 
        WHERE pedido_id = p_pedido_id
    LOOP
        UPDATE produtos 
        SET quantidade_estoque = quantidade_estoque - item.quantidade
        WHERE id = item.produto_id;
    END LOOP;

    UPDATE pedidos 
    SET status = 'CONCLUIDO' 
    WHERE id = p_pedido_id;

    COMMIT;
END;
$$;
