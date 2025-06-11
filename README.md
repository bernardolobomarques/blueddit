# Projeto Blueddit

--------
## PARA RODAR É SÓ DAR CONTINAR MESMSO SEM EDITAR AS CONFIGURAÇÕES
--------


## Alunos
| Matrícula | Aluno |
|---|---|
| 202401709433 | Bernardo Lobo Marques |
| 202401500283 | Bernardo Moreira Guimarães Gonçalves |
| 202401569852 | Michel de Melo Guimarães |

## Visão Geral

Este projeto, denominado "Blueddit", simula uma plataforma de mídia social semelhante ao Reddit, focada em comunidades (sublueddits), posts e comentários. A aplicação é desenvolvida em Java e utiliza um banco de dados MySQL para persistência dos dados.

## Funcionalidades

O sistema Blueddit permite as seguintes operações:

* **Gerenciamento de Usuários**: Criar novos usuários e simular login.
* **Gerenciamento de Sublueddits**:
    * Visualizar sublueddits existentes.
    * Criar novos sublueddits.
    * Inscrever-se e desinscrever-se de sublueddits.
* **Gerenciamento de Posts**:
    * Visualizar posts dentro de um sublueddit.
    * Criar novos posts.
    * Interagir com posts (upvote/downvote).
* **Gerenciamento de Comentários**:
    * Adicionar comentários a posts.
    * Interagir com comentários (upvote/downvote).
* **Sistema de Votação**: Os usuários podem dar upvote ou downvote em posts e comentários.
* **Conexão com Banco de Dados**: A aplicação se conecta a um banco de dados MySQL para armazenar e recuperar informações.

## Estrutura do Projeto

O projeto está organizado da seguinte forma:

* `bd/ConexaoSQL.java`: Lida com a conexão ao banco de dados MySQL.
* `dao/`: Contém as classes DAO (Data Access Object) para interagir com o banco de dados.
    * `BaseDAO.java`: Interface base para operações CRUD.
    * `ComentarioDAO.java`: Gerencia operações CRUD para comentários.
    * `InscricaoDAO.java`: Gerencia inscrições de usuários em sublueddits.
    * `PostDAO.java`: Gerencia operações CRUD para posts.
    * `SubluedditDAO.java`: Gerencia operações CRUD para sublueddits.
    * `UsuarioDAO.java`: Gerencia operações CRUD para usuários.
    * `VotoDAO.java`: Gerencia o registro e atualização de votos em conteúdo.
* `modelo/`: Contém as classes de modelo que representam as entidades do sistema.
    * `Comentario.java`: Representa um comentário, estendendo `Conteudo`.
    * `Conteudo.java`: Classe abstrata base para `Post` e `Comentario`, implementa a interface `Voto`.
    * `Post.java`: Representa um post, estendendo `Conteudo`.
    * `Sublueddit.java`: Representa uma comunidade.
    * `Usuario.java`: Representa um usuário.
    * `Voto.java`: Interface para funcionalidade de votação.
* `script.sql`: Contém o script SQL para criar o banco de dados e as tabelas.
* `pom.xml`: Arquivo de configuração do Maven, incluindo a dependência para o conector MySQL.
* `Main.java`: A classe principal que executa a aplicação e o menu de interação.

## Configuração e Execução

### Pré-requisitos

* Java Development Kit (JDK) 21 ou superior.
* Maven.
* Servidor MySQL.

### Passos para Configuração

1.  **Configurar o Banco de Dados MySQL**:
    * Acesse seu servidor MySQL.
    * Execute o script `script.sql` para criar o banco de dados `blueddit_db` e suas tabelas. Certifique-se de que o usuário e senha configurados em `bd/ConexaoSQL.java` (usuário: `root`, senha: `admin`) tenham permissões adequadas ou atualize-os conforme sua configuração de banco de dados.

2.  **Configurar o Projeto Maven**:
    * Navegue até a pasta `t2` no terminal.
    * O arquivo `pom.xml` já está configurado para usar o conector MySQL.

3.  **Compilar e Executar a Aplicação**:
    * No terminal, na pasta `t2`, compile o projeto Maven:
        ```bash
        mvn clean install
        ```
    * Execute a aplicação:
        ```bash
        mvn exec:java -Dexec.mainClass="Main"
        ```

### Interação com a Aplicação

A aplicação será iniciada no console, apresentando um menu interativo para que o usuário possa selecionar um usuário para login e, em seguida, navegar pelas opções de sublueddits, posts e comentários.
