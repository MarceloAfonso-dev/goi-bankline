-- Script para criar conta do banco para transferências de bônus
-- Execute este SQL no seu banco de dados

-- O cliente GOI Bank Sistema (ID 12) já existe no banco
-- Inserindo apenas a conta para o sistema com saldo suficiente para bônus

INSERT INTO Conta (ID_Cliente, Numero_Conta, Senha, Saldo, Limite_Credito, Data_Criacao)
VALUES (
    12,              -- ID do cliente GOI Bank Sistema
    '9999999999',    -- Número da conta do sistema
    '123456',        -- Senha padrão
    1000000.00,      -- 1 milhão para garantir que sempre há saldo para bônus
    0.00,            -- Sem limite de crédito
    '2000-01-01'     -- Data de criação
);
