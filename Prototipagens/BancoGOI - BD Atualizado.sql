create database BancoGOI; 
use BancoGOI; 

CREATE TABLE Instituicao(

    ID_instituicao INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Razao_Social VARCHAR(100) NOT NULL Unique,
    CNPJ VARCHAR(14) NOT NULL UNIQUE,
    CEP VARCHAR(8) NOT NULL,
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
    Saldo DECIMAL(14,2) NOT NULL ,
    Limite_Credito DECIMAL(14,2) NOT NULL,
    Data_Criacao DATE NOT NULL,
    FOREIGN KEY (ID_Cliente) REFERENCES Cliente(ID) ON DELETE CASCADE
);


CREATE TABLE Produtos (
    ID_Produto INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Id_Conta INTEGER NOT NULL,
    Servico ENUM('Cheque Especial', 'Cartão de Crédito') NOT NULL UNIQUE,
    FOREIGN KEY (Id_Conta) REFERENCES Conta(IdConta) ON DELETE CASCADE
);


CREATE TABLE Cheque_Especial (
    Id_Cheque INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Id_Conta INTEGER NOT NULL,
    Valor DECIMAL(14,2) NOT NULL,
    Limite_Debito DECIMAL(14,2) NOT NULL ,
    Status ENUM('pendente', 'aprovado', 'cancelado') DEFAULT 'aprovado',
    FOREIGN KEY (Id_Conta) REFERENCES Conta(IdConta) ON DELETE CASCADE
);


CREATE TABLE Cartao_Credito (

    Id_Cartao INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ID_Cliente INTEGER NOT NULL,
    Id_Conta INTEGER NOT NULL,
    Valor_Utilizado DECIMAL(14,2) NOT NULL ,
    Limite DECIMAL(14,2) NOT NULL ,
    FOREIGN KEY (ID_Cliente) REFERENCES Cliente(ID) ON DELETE CASCADE,
    FOREIGN KEY (Id_Conta) REFERENCES Conta(IdConta) ON DELETE CASCADE
);


CREATE TABLE Fatura_Cartao (
	
	ID_Fatura INTEGER NOT NULL AUTO_INCREMENT (PK),
	Id_Cartao INTEGER NOT NULL (FK),
	ID_Cliente INTEGER NOT NULL (FK),
	ID_Lancamentos INTEGER NOT NULL (FK),
	Competencia DATE NOT NULL,
	Data_Pagamento NOT NULL,
	Valor DECIMAL(14,2)
	Forma_Pagamento ENUM('Boleto', 'Pix', 'Saldo em conta') NOT NULL UNIQUE,
	-- Parcelamento de fatura? Nova tabela, etc
	Status ENUM('Pago', 'Pendente', 'Vencido', 'Gerando') NOT NULL,
	Valor_Pago DECIMAL(14,2) NOT NULL,
);

CREATE TABLE Lancamentos_Cartao (
	ID_Lancamento INTEGER NOT NULL AUTO_INCREMENT (PK),
	ID_Cartao INTEGER NOT NULL (FK),
	Descricao VARCHAR('200') NOT NULL,
	Valor DECIMAL(14,2) NOT NULL,
	Parcelas INTEGER NOT NULL,
	Data DATE Not null,
)




CREATE TABLE Servicos (
    ID_Servico INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Id_Conta INTEGER NOT NULL,
    Servico ENUM('Depositar', 'Transferir') NOT NULL UNIQUE,
    FOREIGN KEY (Id_Conta) REFERENCES Conta(IdConta) ON DELETE CASCADE
);


CREATE TABLE Deposito (
    Id_Deposito INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ID_Cliente INTEGER NOT NULL,
    Id_Conta INTEGER NOT NULL,
    Valor DECIMAL(14,2) NOT NULL ,
    Data_Deposito DATE NOT NULL,
    Tipo_Deposito ENUM('transferência', 'dinheiro', 'cheque', 'outros') NOT NULL,
    Status ENUM('pendente', 'cancelado', 'concluído') DEFAULT 'concluído',
    FOREIGN KEY (ID_Cliente) REFERENCES Cliente(ID) ON DELETE CASCADE,
    FOREIGN KEY (Id_Conta) REFERENCES Conta(IdConta) ON DELETE CASCADE
);


CREATE TABLE Transferencia (
    Id_Transferencia INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ID_Cliente INTEGER NOT NULL,
    Id_Conta INTEGER NOT NULL,
    Valor DECIMAL(14,2) NOT NULL,
    Data_Transferencia DATE NOT NULL,
    Tipo_Transferencia ENUM('dinheiro', 'cheque', 'outros') NOT NULL,
    Status ENUM('pendente', 'cancelado', 'concluído') DEFAULT 'concluído',
    FOREIGN KEY (ID_Cliente) REFERENCES Cliente(ID) ON DELETE CASCADE,
    FOREIGN KEY (Id_Conta) REFERENCES Conta(IdConta) ON DELETE CASCADE
);