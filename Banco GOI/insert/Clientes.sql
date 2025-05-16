INSERT INTO Cliente (Nome, Sobrenome, CPF, Telefone, CEP, Data_Nascimento, Email)
VALUES ('Gustavo', 'Mendes dos Santos', '5075263818', '11987654321', '01167219', '2005-01-01', 'gustavo@gmail.com');

UPDATE Cliente
SET Sobrenome = 'Mendes'
WHERE Nome = 'Gustavo' AND id = 9;
