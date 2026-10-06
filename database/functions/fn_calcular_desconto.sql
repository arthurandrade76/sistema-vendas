CREATE OR REPLACE FUNCTION fn_calcular_desconto(p_valor NUMERIC)
RETURNS NUMERIC
LANGUAGE plpgsql
AS $$
BEGIN

    IF p_valor > 100.00 THEN
        RETURN ROUND(p_valor * 0.10, 2);
    ELSE
        RETURN 0.00;
    END IF;
END;
$$;
