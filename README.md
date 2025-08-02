# ContasBot

Um bot do Telegram desenvolvido em Java para gerenciar contas e pedidos de clientes. Este bot permite registrar novos pedidos associados a clientes e consultar o histórico de pedidos de um cliente a partir de uma data específica.

## Funcionalidades

- **Registro de Pedidos**: Permite registrar produtos, quantidades, preços e datas para clientes específicos.
- **Consulta de Contas**: Visualize o histórico de pedidos de um cliente, incluindo o total gasto.
- **Gerenciamento de Clientes**: Cria automaticamente novos clientes ou utiliza clientes existentes ao registrar pedidos.

## Tecnologias Utilizadas

- Java
- [TelegramBots API](https://github.com/rubenlagus/TelegramBots)
- JDBC para conexão com banco de dados
- MySQL (como sistema de gerenciamento de banco de dados)

## Pré-requisitos

Antes de executar o ContasBot, certifique-se de ter o seguinte instalado e configurado:

- Java Development Kit (JDK) 8 ou superior
- Apache Maven (para gerenciamento de dependências, se o projeto for configurado com Maven)
- Servidor MySQL em execução
- Um bot do Telegram configurado com um `Bot Token` e `Bot Username`.

## Configuração do Banco de Dados

O bot espera se conectar a um banco de dados MySQL chamado `bdcontas`. Você precisará criar este banco de dados e as tabelas `clientes` e `pedidos`.

### Estrutura do Banco de Dados

```sql
CREATE DATABASE bdcontas;
USE bdcontas;

CREATE TABLE clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    produto VARCHAR(255) NOT NULL,
    quantidade INT NOT NULL,
    preco DECIMAL(10, 2) NOT NULL,
    data DATE NOT NULL,
    cliente_id INT,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);
```

Certifique-se de que as credenciais do banco de dados no arquivo `DBManager.java` estejam corretas:

```java
private static final String url = "jdbc:mysql://localhost:3306/bdcontas?useSSL=false&serverTimezone=UTC";
private static final String USUARIO = "root"; // Seu usuário do MySQL
private static final String SENHA = "";     // Sua senha do MySQL
```

## Como Compilar e Executar

1. **Clone o repositório (ou descompacte o projeto):**

   ```bash
   git clone <URL_DO_SEU_REPOSITORIO>
   cd ContasBot
   ```
   (Se você descompactou, navegue até a pasta raiz do projeto `ContasBot`)

2. **Configure o Bot Token e Username:**

   No arquivo `src/Bot.java`, atualize os métodos `getBotUsername()` e `getBotToken()` com as informações do seu bot do Telegram:

   ```java
   @Override
   public String getBotUsername() {
       return "SEU_BOT_USERNAME"; // Ex: MeuContasBot
   }

   @Override
   public String getBotToken() {
       return "SEU_BOT_TOKEN"; // Ex: 123456:ABC-DEF1234ghIJKLMnoPQRstUVwxyZ
   }
   ```

3. **Adicione as dependências:**

   O projeto utiliza a biblioteca `telegrambots`. O arquivo `telegrambots-6.8.0-jar-with-dependencies.jar` já está incluído na pasta `lib/`. Certifique-se de que ele esteja no classpath do seu projeto ao compilar e executar.

   Se estiver usando uma IDE como IntelliJ IDEA ou Eclipse, adicione este JAR como uma biblioteca externa ao seu projeto.

4. **Compile o projeto:**

   Se estiver usando uma IDE, compile o projeto através das opções da IDE. Se estiver usando o terminal, você pode compilar os arquivos `.java`:

   ```bash
   javac -cp "lib/*" src/Main.java src/Bot.java src/Entities/*.java src/DataBase/*.java src/Services/*.java -d out/production/ContasBot
   ```

5. **Execute o bot:**

   ```bash
   java -cp "out/production/ContasBot:lib/*" Main
   ```

## Como Usar o Bot

Após iniciar o bot, você pode interagir com ele no Telegram:

- `/registrar`: Inicia o fluxo para registrar um novo pedido. O bot irá pedir o nome do cliente, produto, quantidade, preço unitário e data.
- `/conta`: Inicia o fluxo para consultar os pedidos de um cliente. O bot irá pedir o nome do cliente e a data inicial para a consulta.

## Contribuição

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues ou pull requests para melhorias, correções de bugs ou novas funcionalidades.

## Licença

Este projeto está licenciado sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes. (Assumindo licença MIT, se houver um arquivo LICENSE diferente, por favor, ajuste.)


