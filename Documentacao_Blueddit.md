# Documentação e Justificação do Projeto Blueddit

## 1. Introdução

O Blueddit é uma réplica simplificada do Reddit, implementada como um sistema de crowdsourcing onde os usuários podem criar chats (análogos aos subreddits), enviar mensagens e votar (positivamente ou negativamente) em conteúdos compartilhados por outros usuários. O sistema foi desenvolvido utilizando a linguagem Java e segue os princípios da Programação Orientada a Objetos.

## 2. Arquitetura do Sistema

A arquitetura do Blueddit segue o padrão MVC (Model-View-Controller) simplificado, com as seguintes camadas:

- **Modelo (Model)**: Classes que representam as entidades do sistema e suas relações
- **Visão (View)**: Interface de linha de comando (CLI) para interação com o usuário
- **Controle (Controller)**: Implementado parcialmente dentro da CLI e também através dos DAOs

O sistema utiliza o padrão DAO (Data Access Object) para separar a lógica de acesso a dados da lógica de negócios.

## 3. Implementação dos Pilares da Orientação a Objetos

### 3.1 Abstração

A abstração foi implementada através da modelagem adequada das entidades do sistema:

- `User`: Representa um usuário do sistema com seus atributos e comportamentos
- `Chat`: Representa um espaço de discussão (similar a um subreddit)
- `Message`: Representa uma mensagem enviada por um usuário em um chat
- `Content`: Classe abstrata que define o comportamento base para conteúdos votáveis

### 3.2 Encapsulamento

O encapsulamento foi aplicado em todas as classes do modelo:

- Atributos declarados como `private`
- Métodos de acesso (getters e setters) controlam o acesso aos dados
- Implementação de métodos específicos para operações que manipulam o estado interno dos objetos
- Método `checkPassword()` na classe User que verifica a senha sem expor a senha real

### 3.3 Herança

A hierarquia de classes foi implementada da seguinte forma:

- `Content`: Classe abstrata base para conteúdos votáveis
  - `Message`: Estende a classe Content e implementa seus métodos abstratos

### 3.4 Polimorfismo

O polimorfismo foi implementado através de:

- **Sobrescrita**: Métodos como `toString()`, `equals()` e `hashCode()` foram sobrescritos nas classes do modelo
- **Interface**: A interface `Votable` define comportamentos que podem ser implementados por diferentes classes
- **Invocação polimórfica**: Através do uso de `Content` como tipo mais genérico para se referir a objetos de `Message`

## 4. Classes Abstratas e Interfaces

### 4.1 Classe Abstrata

A classe `Content` foi implementada como uma classe abstrata com:

- Métodos abstratos `getType()` e `getPreview()` que devem ser implementados pelas subclasses
- Métodos concretos como `upvote()`, `downvote()` e `getScore()` que implementam comportamento comum

### 4.2 Interface

A interface `Votable` define o contrato para classes que podem receber votos:

- Métodos `upvote()`, `downvote()`, `getScore()` e `getVoters()`
- Implementada pela classe abstrata `Content`, e consequentemente por todas as suas subclasses

## 5. Relacionamentos entre Classes

### 5.1 Diversidade de Cardinalidades

- **1:1**: Cada mensagem pertence a apenas um usuário e a apenas um chat
- **1:N**: Um usuário pode ter várias mensagens
- **N:N**: Vários usuários podem participar de vários chats

### 5.2 Diversidade de Direcionamento

- **Bidirecional**: A relação entre `User` e `Chat` é bidirecional, onde ambas as classes mantêm referências uma à outra
- **Unidirecional**: A relação entre `Message` e `User` é unidirecional, onde apenas a mensagem conhece o usuário

### 5.3 Composição e Agregação

- **Composição**: Um chat "possui" mensagens, sendo responsável por sua criação e ciclo de vida
- **Agregação**: Um chat "contém" referências a usuários, mas estes existem independentemente do chat

## 6. Uso de Collections

Várias estruturas de coleções foram utilizadas no projeto:

- **List**: Para armazenar mensagens em um chat (`List<Message>`)
- **Set**: Para armazenar usuários em um chat (`Set<User>`) e evitar duplicações
- **ArrayList**: Implementação concreta de `List` para armazenar mensagens e votos
- **HashSet**: Implementação concreta de `Set` para armazenar usuários de um chat

Operações implementadas:
- **Adição**: `addUser()`, `addMessage()`
- **Busca**: `findMessageById()`, `getChats()`
- **Remoção**: `removeUser()`, `removeMessage()`

## 7. Persistência de Dados

### 7.1 Conexão com Banco Relacional via JDBC

A conexão com o banco de dados foi implementada através da classe `DatabaseConnection`, que:

- Utiliza o driver JDBC para estabelecer a conexão
- Inicializa o banco de dados com as tabelas necessárias
- Utiliza o banco de dados H2 em memória para facilitar testes e desenvolvimento

### 7.2 Padrão DAO

O padrão DAO foi implementado através das classes:

- `UserDAO`: Gerencia a persistência de usuários
- `ChatDAO`: Gerencia a persistência de chats
- `MessageDAO`: Gerencia a persistência de mensagens

### 7.3 Operações CRUD

Todas as operações CRUD foram implementadas nos DAOs:

- **Create**: Métodos `insert()` em cada DAO
- **Read**: Métodos `findById()`, `findAll()`, `findByUser()`, etc.
- **Update**: Métodos `update()` em cada DAO
- **Delete**: Métodos `delete()` em cada DAO

## 8. Conclusão

O Blueddit implementa todos os requisitos solicitados para o projeto de Programação Orientada a Objetos, demonstrando a aplicação dos pilares da OO, uso de classes abstratas e interfaces, diversidade de relacionamentos, uso adequado de collections e persistência de dados com JDBC utilizando o padrão DAO.

O sistema permite que os usuários:
- Se cadastrem e façam login
- Criem chats e participem deles
- Enviem mensagens nos chats
- Votem positivamente ou negativamente em mensagens
- Visualizem suas próprias mensagens
- Gerenciem seu perfil

## 9. Instruções de Execução

1. Compilar o projeto usando Java 8 ou superior
2. Executar a classe `blueddit.Main` que contém o método `main()`
3. Seguir as instruções na interface de linha de comando

### Usuários pré-cadastrados:
- Username: admin / Senha: admin123
- Username: joao / Senha: senha123
- Username: maria / Senha: senha456
