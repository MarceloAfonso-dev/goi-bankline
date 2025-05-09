
CREATE DATABASE BancoGOI; 
USE BancoGOI; 

CREATE TABLE Instituicao (
    ID_instituicao INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Razao_Social VARCHAR(100) NOT NULL UNIQUE,
    CNPJ VARCHAR(14) NOT NULL UNIQUE,
    CEP VARCHAR(8) NOT NULL
);

CREATE TABLE Cliente (
    ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY, 
    Nome VARCHAR(100) NOT NULL,
    Sobrenome VARCHAR(250) NOT NULL,
    CPF VARCHAR(11) NOT NULL UNIQUE,  
    Telefone VARCHAR(11) NOT NULL,
    CEP VARCHAR(8) NOT NULL,
    Data_Nascimento DATE NOT NULL
);

CREATE TABLE Conta (
    IdConta INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ID_Cliente INTEGER NOT NULL,  
    Numero_Conta VARCHAR(10) NOT NULL UNIQUE,
    Senha VARCHAR(8) NOT NULL,
    Saldo DECIMAL(14,2) NOT NULL,
    Limite_Credito DECIMAL(14,2) NOT NULL,
    Data_Criacao DATE NOT NULL,
    FOREIGN KEY (ID_Cliente) REFERENCES Cliente(ID) ON DELETE CASCADE  
);

CREATE TABLE Produtos (
    ID_Produto INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Id_Conta INTEGER NOT NULL,
    Servico ENUM('Cheque Especial', 'Cartão de Crédito', 'CDB') NOT NULL UNIQUE,
    FOREIGN KEY (Id_Conta) REFERENCES Conta(IdConta) ON DELETE CASCADE
);

CREATE TABLE Cheque_Especial (
    Id_Cheque INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Id_Conta INTEGER NOT NULL,
    Valor DECIMAL(14,2) NOT NULL,
    Limite_Cheque DECIMAL(14,2) NOT NULL,
    Subtotal DECIMAL(14,2),
    Situacao_Pagamento ENUM('pendente', 'aprovado', 'cancelado') DEFAULT 'aprovado',
    FOREIGN KEY (Id_Conta) REFERENCES Conta(IdConta) ON DELETE CASCADE
);

CREATE TABLE Cartao_Credito (
    Id_Cartao INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Id_Conta INTEGER NOT NULL,
    Valor_Utilizado DECIMAL(14,2) NOT NULL,
    Limite DECIMAL(14,2) NOT NULL,
    Status_Bloqueio BOOLEAN DEFAULT FALSE,
    Status_Cartao ENUM('ativo', 'inativo', 'bloqueado') NOT NULL,
    FOREIGN KEY (Id_Conta) REFERENCES Conta(IdConta) ON DELETE CASCADE
);

CREATE TABLE Fatura_Cartao (
    ID_Fatura INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Id_Cartao INTEGER NOT NULL,
    Competencia DATE NOT NULL,
    Data_Pagamento DATE NOT NULL,
    Vencimento DATE NOT NULL,
    Valor DECIMAL(14,2),
    Forma_Pagamento ENUM('Saldo em conta') NOT NULL UNIQUE,
    Subtotal DECIMAL(14,2),
    Status ENUM('Pago', 'Pendente', 'Vencido') NOT NULL,
    Valor_Pago DECIMAL(14,2) NOT NULL,
    FOREIGN KEY (Id_Cartao) REFERENCES Cartao_Credito(Id_Cartao) ON DELETE CASCADE
);

CREATE TABLE Lancamentos_Cartao (
    ID_Lancamento INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Id_Cartao INTEGER NOT NULL,
    Descricao VARCHAR(200) NOT NULL,
    Valor DECIMAL(14,2) NOT NULL,
    Parcelas INTEGER NOT NULL,
    Data DATE NOT NULL,
    FOREIGN KEY (Id_Cartao) REFERENCES Cartao_Credito(Id_Cartao) ON DELETE CASCADE
);

CREATE TABLE Transferencia (
    Id_Transferencia INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Id_Conta INTEGER NOT NULL,
    Id_Cartao INTEGER,
    Valor DECIMAL(14,2) NOT NULL,
    Data_Transferencia DATE NOT NULL,
    Tipo_Transferencia ENUM('dinheiro', 'Cartão de Crédito') NOT NULL,
    Status ENUM('pendente', 'cancelado', 'concluído') DEFAULT 'concluído',
    FOREIGN KEY (Id_Conta) REFERENCES Conta(IdConta) ON DELETE CASCADE,
    FOREIGN KEY (Id_Cartao) REFERENCES Cartao_Credito(Id_Cartao) ON DELETE CASCADE
);

CREATE TABLE Boleto (
    Id_Boleto INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Id_Conta INTEGER NOT NULL,
    Valor DECIMAL(14,2) NOT NULL,
    Data_Emissao DATE NOT NULL,
    Data_Vencimento DATE NOT NULL,
    Status ENUM('pendente', 'pago', 'cancelado') DEFAULT 'pendente',
    FOREIGN KEY (Id_Conta) REFERENCES Conta(IdConta) ON DELETE CASCADE
);

CREATE TABLE Investimentos (
    Id_Investimento INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Id_Conta INTEGER NOT NULL,
    Tipo_Investimento ENUM('CDB') NOT NULL,
    Valor DECIMAL(14,2) NOT NULL,
    Data_Investimento DATE NOT NULL,
    FOREIGN KEY (Id_Conta) REFERENCES Conta(IdConta) ON DELETE CASCADE
);

CREATE TABLE Aportes (
    Id_Aporte INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Id_Investimento INTEGER NOT NULL,
    Valor DECIMAL(14,2) NOT NULL,
    Data_Aporte DATE NOT NULL,
    FOREIGN KEY (Id_Investimento) REFERENCES Investimentos(Id_Investimento) ON DELETE CASCADE
);

CREATE TABLE Resgates (
    Id_Resgate INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Id_Investimento INTEGER NOT NULL,
    Valor DECIMAL(14,2) NOT NULL,
    Data_Resgate DATE NOT NULL,
    FOREIGN KEY (Id_Investimento) REFERENCES Investimentos(Id_Investimento) ON DELETE CASCADE
);

CREATE TABLE CDBs (
    Id_CDB INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Nome VARCHAR(100) NOT NULL UNIQUE,
    Taxa_Juros DECIMAL(5,2) NOT NULL,
    Prazo INTEGER NOT NULL
);

CREATE TABLE Rendimento (
    Id_Rendimento INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Id_Investimento INTEGER NOT NULL,
    Valor DECIMAL(14,2) NOT NULL,
    Data_Rendimento DATE NOT NULL,
    FOREIGN KEY (Id_Investimento) REFERENCES Investimentos(Id_Investimento) ON DELETE CASCADE
);

CREATE TABLE Juros (
    Id_Juros INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Id_CDB INTEGER NOT NULL,
    Taxa DECIMAL(5,2) NOT NULL,
    Data_Aplicacao DATE NOT NULL,
    FOREIGN KEY (Id_CDB) REFERENCES CDBs(Id_CDB) ON DELETE CASCADE
);

CREATE TABLE CDI (
    Id_CDI INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Data_Referencia DATE NOT NULL,
    Taxa_CDI DECIMAL(5,2) NOT NULL
);

CREATE TABLE Extrato_Conta (
    Id_Extrato INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Id_Conta INTEGER NOT NULL,
    Descricao VARCHAR(200) NOT NULL,
    Valor DECIMAL(14,2) NOT NULL,
    Data DATE NOT NULL,
    FOREIGN KEY (Id_Conta) REFERENCES Conta(IdConta) ON DELETE CASCADE
);

CREATE TABLE Tributos (
    Id_Tributo INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Nome VARCHAR(100) NOT NULL UNIQUE,
    Valor DECIMAL(14,2) NOT NULL,
    Data_Vencimento DATE NOT NULL
);
