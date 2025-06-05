package blueddit.ui;

import blueddit.dao.ChatDAO;
import blueddit.dao.MessageDAO;
import blueddit.dao.UserDAO;
import blueddit.model.Chat;
import blueddit.model.Message;
import blueddit.model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * Interface de linha de comando para o Blueddit
 * Permite interação do usuário com o sistema
 */
public class BluedditCLI {
    private Connection connection;
    private UserDAO userDAO;
    private ChatDAO chatDAO;
    private MessageDAO messageDAO;
    private Scanner scanner;
    private User currentUser;

    /**
     * Construtor que inicializa os DAOs e estabelece suas relações
     * @param connection Conexão com o banco de dados
     */
    public BluedditCLI(Connection connection) {
        this.connection = connection;
        this.userDAO = new UserDAO(connection);
        this.chatDAO = new ChatDAO(connection);
        this.messageDAO = new MessageDAO(connection);
        this.scanner = new Scanner(System.in);

        // Estabelece as relações entre os DAOs para evitar dependência circular
        userDAO.setChatDAO(chatDAO);
        chatDAO.setUserDAO(userDAO);
        chatDAO.setMessageDAO(messageDAO);
        messageDAO.setUserDAO(userDAO);
    }

    /**
     * Inicia a interface de usuário
     */
    public void start() {
        System.out.println("Bem-vindo ao Blueddit!");
        System.out.println("=======================");

        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }

    /**
     * Exibe o menu de login
     */
    private void showLoginMenu() {
        System.out.println("\n=== MENU DE ACESSO ===");
        System.out.println("1. Login");
        System.out.println("2. Cadastrar");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");

        int option = readIntOption();

        switch (option) {
            case 1:
                login();
                break;
            case 2:
                register();
                break;
            case 0:
                try {
                    // Fecha a conexão com o banco de dados antes de sair
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Erro ao fechar conexão: " + e.getMessage());
                }
                System.out.println("Obrigado por usar o Blueddit! Até logo!");
                System.exit(0);
                break;
            default:
                System.out.println("Opção inválida!");
        }
    }

    /**
     * Realiza o login de um usuário
     */
    private void login() {
        System.out.print("Nome de usuário: ");
        String username = scanner.nextLine();
        System.out.print("Senha: ");
        String password = scanner.nextLine();

        try {
            User user = userDAO.authenticate(username, password);
            if (user != null) {
                currentUser = user;
                System.out.println("Login realizado com sucesso! Bem-vindo, " + user.getName() + "!");
            } else {
                System.out.println("Nome de usuário ou senha incorretos!");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fazer login: " + e.getMessage());
        }
    }

    /**
     * Realiza o cadastro de um novo usuário
     */
    private void register() {
        System.out.println("\n=== CADASTRO DE USUÁRIO ===");
        System.out.print("Nome de usuário: ");
        String username = scanner.nextLine();
        System.out.print("Nome completo: ");
        String name = scanner.nextLine();
        System.out.print("Senha: ");
        String password = scanner.nextLine();

        try {
            User existingUser = userDAO.findByUsername(username);
            if (existingUser != null) {
                System.out.println("Este nome de usuário já está em uso!");
                return;
            }

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setName(name);
            newUser.setPassword(password);

            userDAO.insert(newUser);
            System.out.println("Usuário cadastrado com sucesso! Agora você pode fazer login.");
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar usuário: " + e.getMessage());
        }
    }

    /**
     * Exibe o menu principal
     */
    private void showMainMenu() {
        System.out.println("\n=== MENU PRINCIPAL ===");
        System.out.println("1. Ver chats disponíveis");
        System.out.println("2. Criar novo chat");
        System.out.println("3. Minhas mensagens");
        System.out.println("4. Meu perfil");
        System.out.println("0. Sair / Logout");
        System.out.print("Escolha uma opção: ");

        int option = readIntOption();

        switch (option) {
            case 1:
                showChats();
                break;
            case 2:
                createChat();
                break;
            case 3:
                showMyMessages();
                break;
            case 4:
                showProfile();
                break;
            case 0:
                currentUser = null;
                System.out.println("Logout realizado com sucesso!");
                break;
            default:
                System.out.println("Opção inválida!");
        }
    }

    /**
     * Exibe a lista de chats disponíveis
     */
    private void showChats() {
        try {
            List<Chat> chats = chatDAO.findAll();

            if (chats.isEmpty()) {
                System.out.println("Não há chats disponíveis.");
                return;
            }

            System.out.println("\n=== CHATS DISPONÍVEIS ===");
            int count = 1;
            for (Chat chat : chats) {
                System.out.println(count++ + ". " + chat.getTitle() + " - Criado em: " + chat.getDateOfCreation());
            }

            System.out.print("\nSelecione um chat (0 para voltar): ");
            int option = readIntOption();

            if (option > 0 && option <= chats.size()) {
                viewChat(chats.get(option - 1).getId());
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar chats: " + e.getMessage());
        }
    }

    /**
     * Exibe detalhes de um chat específico e suas mensagens
     */
    private void viewChat(int chatId) {
        try {
            Chat chat = chatDAO.findById(chatId);
            if (chat == null) {
                System.out.println("Chat não encontrado!");
                return;
            }

            while (true) {
                System.out.println("\n=== " + chat.getTitle() + " ===");
                List<Message> messages = messageDAO.findByChat(chatId);

                if (messages.isEmpty()) {
                    System.out.println("Este chat ainda não tem mensagens.");
                } else {
                    for (int i = 0; i < messages.size(); i++) {
                        Message message = messages.get(i);
                        try {
                            User author = userDAO.findById(message.getUserId());
                            String authorName = author != null ? author.getUsername() : "Usuário desconhecido";
                            System.out.println((i + 1) + ". [" + message.getScore() + "] " + authorName + ": " + message.getContent());
                        } catch (SQLException e) {
                            System.out.println((i + 1) + ". [" + message.getScore() + "] Usuário desconhecido: " + message.getContent());
                        }
                    }
                }

                System.out.println("\n=== OPÇÕES ===");
                System.out.println("1. Enviar nova mensagem");
                System.out.println("2. Votar positivamente em uma mensagem");
                System.out.println("3. Votar negativamente em uma mensagem");
                System.out.println("0. Voltar ao menu principal");
                System.out.print("Escolha uma opção: ");

                int option = readIntOption();

                switch (option) {
                    case 1:
                        sendMessage(chat);
                        break;
                    case 2:
                        upvoteMessage(messages);
                        break;
                    case 3:
                        downvoteMessage(messages);
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("Opção inválida!");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao acessar chat: " + e.getMessage());
        }
    }

    /**
     * Permite ao usuário enviar uma nova mensagem em um chat
     */
    private void sendMessage(Chat chat) {
        System.out.print("Digite sua mensagem: ");
        String content = scanner.nextLine();

        if (content.trim().isEmpty()) {
            System.out.println("A mensagem não pode ser vazia!");
            return;
        }

        try {
            Message message = new Message();
            message.setChatId(chat.getId());
            message.setUserId(currentUser.getId());
            message.setContent(content);

            messageDAO.insert(message);

            // Adicionar a mensagem ao chat (para manter o modelo de domínio consistente)
            chat.addMessage(message);
            chatDAO.update(chat);

            System.out.println("Mensagem enviada com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao enviar mensagem: " + e.getMessage());
        }
    }

    /**
     * Permite ao usuário votar positivamente em uma mensagem
     */
    private void upvoteMessage(List<Message> messages) {
        if (messages.isEmpty()) {
            System.out.println("Não há mensagens para votar!");
            return;
        }

        System.out.print("Digite o número da mensagem para votar positivamente: ");
        int option = readIntOption();

        if (option > 0 && option <= messages.size()) {
            try {
                Message message = messages.get(option - 1);

                // Não permitir que o usuário vote na própria mensagem
                if (message.getUserId() == currentUser.getId()) {
                    System.out.println("Você não pode votar na sua própria mensagem!");
                    return;
                }

                messageDAO.upvote(message.getId(), currentUser.getId());
                System.out.println("Voto positivo registrado com sucesso!");
            } catch (SQLException e) {
                System.err.println("Erro ao votar na mensagem: " + e.getMessage());
            }
        } else {
            System.out.println("Opção inválida!");
        }
    }

    /**
     * Permite ao usuário votar negativamente em uma mensagem
     */
    private void downvoteMessage(List<Message> messages) {
        if (messages.isEmpty()) {
            System.out.println("Não há mensagens para votar!");
            return;
        }

        System.out.print("Digite o número da mensagem para votar negativamente: ");
        int option = readIntOption();

        if (option > 0 && option <= messages.size()) {
            try {
                Message message = messages.get(option - 1);

                // Não permitir que o usuário vote na própria mensagem
                if (message.getUserId() == currentUser.getId()) {
                    System.out.println("Você não pode votar na sua própria mensagem!");
                    return;
                }

                messageDAO.downvote(message.getId(), currentUser.getId());
                System.out.println("Voto negativo registrado com sucesso!");
            } catch (SQLException e) {
                System.err.println("Erro ao votar na mensagem: " + e.getMessage());
            }
        } else {
            System.out.println("Opção inválida!");
        }
    }

    /**
     * Mostra as mensagens criadas pelo usuário atual
     */
    private void showMyMessages() {
        try {
            List<Message> messages = messageDAO.findByUser(currentUser.getId());

            if (messages.isEmpty()) {
                System.out.println("Você ainda não enviou nenhuma mensagem.");
                return;
            }

            System.out.println("\n=== MINHAS MENSAGENS ===");
            for (int i = 0; i < messages.size(); i++) {
                Message message = messages.get(i);
                System.out.println((i + 1) + ". [" + message.getScore() + " pontos] " + message.getContent());
            }

            System.out.print("\nPressione ENTER para voltar");
            scanner.nextLine();

        } catch (SQLException e) {
            System.err.println("Erro ao buscar mensagens: " + e.getMessage());
        }
    }

    /**
     * Permite ao usuário criar um novo chat
     */
    private void createChat() {
        try {
            // Lista de usuários para adicionar ao chat
            List<User> users = userDAO.findAll();
            System.out.println("\n=== CRIAR NOVO CHAT ===");
            System.out.print("Digite um título para o chat: ");
            String title = scanner.nextLine();

            System.out.println("\nUsuários disponíveis:");
            int count = 1;
            for (User user : users) {
                if (user.getId() != currentUser.getId()) {
                    System.out.println(count++ + ". " + user.getUsername() + " - " + user.getName());
                }
            }

            System.out.print("\nSelecione o usuário para adicionar ao chat (0 para cancelar): ");
            int option = readIntOption();

            if (option <= 0 || option >= count) {
                System.out.println("Operação cancelada.");
                return;
            }

            // Ajuste para o índice correto após pular o usuário atual na listagem
            int userIndex = 0;
            int selectedIndex = 0;
            for (User user : users) {
                if (user.getId() != currentUser.getId()) {
                    selectedIndex++;
                    if (selectedIndex == option) {
                        break;
                    }
                }
                userIndex++;
            }

            User selectedUser = users.get(userIndex);

            // Criando o chat
            Chat newChat = new Chat();
            newChat.setTitle(title);
            newChat.addUser(currentUser);
            newChat.addUser(selectedUser);

            chatDAO.insert(newChat);
            System.out.println("Chat criado com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao criar chat: " + e.getMessage());
        }
    }

    /**
     * Exibe o perfil do usuário atual
     */
    private void showProfile() {
        System.out.println("\n=== MEU PERFIL ===");
        System.out.println("ID: " + currentUser.getId());
        System.out.println("Nome de usuário: " + currentUser.getUsername());
        System.out.println("Nome: " + currentUser.getName());

        System.out.println("\nChats participantes:");
        Set<Chat> userChats = currentUser.getChats();
        if (userChats.isEmpty()) {
            System.out.println("Você não participa de nenhum chat.");
        } else {
            int count = 1;
            for (Chat chat : userChats) {
                System.out.println(count++ + ". " + chat.getTitle());
            }
        }

        System.out.println("\n1. Alterar senha");
        System.out.println("0. Voltar");
        System.out.print("Escolha uma opção: ");

        int option = readIntOption();

        switch (option) {
            case 1:
                changePassword();
                break;
            case 0:
                return;
            default:
                System.out.println("Opção inválida!");
        }
    }

    /**
     * Permite ao usuário alterar sua senha
     */
    private void changePassword() {
        System.out.print("Digite sua senha atual: ");
        String currentPassword = scanner.nextLine();

        if (!currentUser.checkPassword(currentPassword)) {
            System.out.println("Senha atual incorreta!");
            return;
        }

        System.out.print("Digite a nova senha: ");
        String newPassword = scanner.nextLine();
        System.out.print("Confirme a nova senha: ");
        String confirmPassword = scanner.nextLine();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("As senhas não conferem!");
            return;
        }

        try {
            currentUser.setPassword(newPassword);
            userDAO.update(currentUser);
            System.out.println("Senha alterada com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao alterar senha: " + e.getMessage());
        }
    }

    /**
     * Lê uma opção numérica do usuário
     * @return A opção numérica ou -1 em caso de entrada inválida
     */
    private int readIntOption() {
        try {
            String input = scanner.nextLine();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
