-- Insert Query com meu nome e dados ficticios
INSERT INTO Cliente (Nome, Sobrenome, CPF, Telefone, CEP, Data_Nascimento, Email)
VALUES ('Gustavo', 'Mendes dos Santos', '5075263818', '11987654321', '01167219', '2005-01-01', 'gustavo@gmail.com');

-- Conta do banco para transferências de bônus
INSERT INTO Cliente (Nome, Sobrenome, CPF, Telefone, CEP, Data_Nascimento, Email)
VALUES ('GOI', 'Bank Sistema', '66666666666', '08001234567', '01310100', '2000-01-01', 'sistema@goibank.com');

-- Update Query
UPDATE Cliente
SET Sobrenome = 'Mendes'
WHERE Nome = 'Gustavo' AND id = 9;
