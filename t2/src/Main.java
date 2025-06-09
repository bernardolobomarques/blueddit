import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import modelo.Comentario;
import modelo.Sublueddit;
import modelo.Post;
import modelo.Usuario;
import modelo.Conteudo; // Import
import dao.UsuarioDAO;
import dao.PostDAO;
import dao.ComentarioDAO;
import dao.SubluedditDAO;
import dao.InscricaoDAO; // Import
import dao.VotoDAO;       // Import
import bd.ConexaoSQL;

public class Main {

    private static UsuarioDAO usuarioDAO;
    private static SubluedditDAO subluedditDAO;
    private static PostDAO postDAO;
    private static ComentarioDAO comentarioDAO;
    private static InscricaoDAO inscricaoDAO; // DAO para N-N
    private static VotoDAO votoDAO;           // DAO para votos

    private static List<Sublueddit> sublueddits = new ArrayList<>();
    private static List<Usuario> usuarios = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static Usuario usuarioLogado = null; // Simula um login

    public static void main(String[] args) {
        Connection connection = null;
        try {
            connection = ConexaoSQL.recuperaConexao();
            usuarioDAO = new UsuarioDAO(connection);
            subluedditDAO = new SubluedditDAO(connection);
            postDAO = new PostDAO(connection);
            comentarioDAO = new ComentarioDAO(connection);
            inscricaoDAO = new InscricaoDAO(connection);
            votoDAO = new VotoDAO(connection);

            carregarDadosDoBanco();

            // Simulação de Login
            selecionarUsuarioLogado();
            if(usuarioLogado == null) {
                System.out.println("Nenhum usuário selecionado. Encerrando.");
                return;
            }

            executarMenuPrincipal();

        } finally {
            if (connection != null) {
                ConexaoSQL.fechaConexao(connection);
            }
            scanner.close();
        }
    }

    private static void selecionarUsuarioLogado(){
        System.out.println("--- Bem-vindo ao Blueddit! ---");
        if(usuarios.isEmpty()){
            System.out.println("Nenhum usuário cadastrado. Crie um primeiro.");
            criarNovoUsuario();
            if(usuarios.isEmpty()){ return; }
        }
        System.out.println("\nQuem está usando o sistema?");
        for (int i = 0; i < usuarios.size(); i++) {
            System.out.println((i + 1) + ". " + usuarios.get(i).getNome());
        }
        System.out.print("Escolha um usuário (digite o número): ");
        int escolha = scanner.nextInt();
        scanner.nextLine();

        if (escolha > 0 && escolha <= usuarios.size()) {
            usuarioLogado = usuarios.get(escolha - 1);
            System.out.println("Bem-vindo(a), " + usuarioLogado.getNome() + "!");
        }
    }


    private static void executarMenuPrincipal() {
        int opcaoPrincipal;
        do {
            System.out.println("\n--- Menu Principal (Logado como: " + usuarioLogado.getNome() + ") ---");
            System.out.println("1. Ver Sublueddits");
            System.out.println("2. Criar novo Sublueddit");
            System.out.println("3. Criar novo Usuário");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");
            opcaoPrincipal = scanner.nextInt();
            scanner.nextLine();

            switch (opcaoPrincipal) {
                case 1:
                    menuVerSublueddits();
                    break;
                case 2:
                    criarNovoSublueddit();
                    break;
                case 3:
                    criarNovoUsuario();
                    break;
                case 0:
                    System.out.println("Saindo do programa. Até mais!");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcaoPrincipal != 0);
    }

    private static void menuVerSublueddits() {
        if (sublueddits.isEmpty()) {
            System.out.println("Nenhum sublueddit disponível. Crie um primeiro.");
            return;
        }

        System.out.println("\n--- Sublueddits Disponíveis ---");
        for (int i = 0; i < sublueddits.size(); i++) {
            System.out.println((i + 1) + ". b/" + sublueddits.get(i).getNome());
        }
        System.out.print("Escolha um sublueddit para entrar (ou 0 para voltar): ");
        int escolha = scanner.nextInt();
        scanner.nextLine();

        if (escolha > 0 && escolha <= sublueddits.size()) {
            Sublueddit subluedditSelecionado = sublueddits.get(escolha - 1);
            menuSublueddit(subluedditSelecionado);
        }
    }

    private static void menuSublueddit(Sublueddit sublueddit) {
        int opcao;
        do {
            boolean isIncrito = usuarioLogado.getInscricoes().stream().anyMatch(s -> s.getId() == sublueddit.getId());
            System.out.println("\n--- b/" + sublueddit.getNome() + (isIncrito ? " (Inscrito)" : "") + " ---");
            System.out.println("1. Ver Posts");
            System.out.println("2. Criar Post");
            if(isIncrito) System.out.println("3. Desinscrever-se");
            else System.out.println("3. Inscrever-se");
            System.out.println("0. Voltar");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    verPosts(sublueddit);
                    break;
                case 2:
                    criarPost(sublueddit);
                    break;
                case 3:
                    if(isIncrito) {
                        inscricaoDAO.desinscrever(usuarioLogado, sublueddit);
                        usuarioLogado.desinscrever(sublueddit);
                    } else {
                        inscricaoDAO.inscrever(usuarioLogado, sublueddit);
                        usuarioLogado.inscrever(sublueddit);
                    }
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private static void verPosts(Sublueddit sublueddit) {
        // ... (lógica para exibir posts, similar à anterior)
    }

    private static void menuInteracaoPost(Post post) {
        // ... (lógica para interagir com post)
        // A principal mudança será chamar o menu de interação de conteúdo
        menuInteracaoConteudo(post);
    }

    private static void menuInteracaoConteudo(Conteudo conteudo) {
        int opcao;
        do {
            System.out.println("\n--- Interagindo com Conteúdo ---");
            if (conteudo instanceof Post) {
                System.out.println("Tipo: Post");
            } else {
                System.out.println("Tipo: Comentário");
            }
            System.out.println("Autor: " + conteudo.getAutor().getNome());
            System.out.println("Texto: " + conteudo.getTexto());
            System.out.println("Votos: [" + conteudo.getUpvoteCount() + "▲ " + conteudo.getDownvoteCount() + "▼]");

            if (conteudo instanceof Post) {
                Post post = (Post) conteudo;
                System.out.println("\nComentários:");
                if (post.getComentarios().isEmpty()) {
                    System.out.println("  Nenhum comentário ainda.");
                } else {
                    for (int i = 0; i < post.getComentarios().size(); i++) {
                        Comentario c = post.getComentarios().get(i);
                        System.out.println("  " + (i + 1) + ". " + c.getTexto() + " (Por: " + c.getAutor().getNome() + ") ["+ c.getUpvoteCount() +"▲ "+ c.getDownvoteCount() +"▼]");
                    }
                }
                System.out.println("\nOpções:");
                System.out.println("1. Dar Upvote");
                System.out.println("2. Dar Downvote");
                System.out.println("3. Adicionar Comentário");
                System.out.println("4. Interagir com um Comentário");
                System.out.println("0. Voltar");

            } else { // É um comentário
                System.out.println("\nOpções:");
                System.out.println("1. Dar Upvote");
                System.out.println("2. Dar Downvote");
                System.out.println("0. Voltar");
            }

            System.out.print("Escolha: ");
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch(opcao) {
                case 1: // UPVOTE
                    votarNoConteudo(conteudo, 1);
                    break;
                case 2: // DOWNVOTE
                    votarNoConteudo(conteudo, -1);
                    break;
                case 3: // Adicionar Comentário (só para Post)
                    if (conteudo instanceof Post) {
                        adicionarComentario((Post) conteudo);
                    } else {
                        System.out.println("Opção inválida.");
                    }
                    break;
                case 4: // Interagir com Comentário (só para Post)
                    if (conteudo instanceof Post) {
                        System.out.print("Digite o número do comentário: ");
                        int comIndex = scanner.nextInt() - 1;
                        scanner.nextLine();
                        Post post = (Post) conteudo;
                        if(comIndex >= 0 && comIndex < post.getComentarios().size()){
                            menuInteracaoConteudo(post.getComentarios().get(comIndex));
                        } else {
                            System.out.println("Comentário inválido.");
                        }
                    } else {
                        System.out.println("Opção inválida.");
                    }
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida.");
            }

        } while(opcao != 0);
    }

    private static void votarNoConteudo(Conteudo conteudo, int tipoVoto) {
        votoDAO.registrarVoto(usuarioLogado, conteudo, tipoVoto);
        // Após registrar o voto, atualiza a contagem no banco e no objeto
        votoDAO.atualizarContagemVotos(conteudo);
        System.out.println("Voto registrado com sucesso!");
    }

    private static void adicionarComentario(Post post) {
        System.out.print("Digite seu comentário: ");
        String texto = scanner.nextLine();
        Comentario novoComentario = new Comentario(texto, usuarioLogado, post);
        comentarioDAO.salvar(novoComentario); // Salva no banco
        post.adicionarComentario(novoComentario); // Adiciona na lista em memória
        System.out.println("Comentário adicionado!");
    }

    private static void criarPost(Sublueddit sublueddit) {
        System.out.print("Digite a descrição do seu post: ");
        String descricao = scanner.nextLine();
        Post novoPost = new Post(usuarioLogado, sublueddit, descricao);
        postDAO.salvar(novoPost);
        sublueddit.adicionarPost(novoPost);
        System.out.println("Post criado com sucesso em b/" + sublueddit.getNome() + "!");
    }

    private static void criarNovoUsuario() {
        System.out.print("Digite o nome do novo usuário: ");
        String nomeUsuario = scanner.nextLine();
        Usuario novoUsuario = new Usuario(nomeUsuario);
        usuarioDAO.salvar(novoUsuario);
        usuarios.add(novoUsuario);
        System.out.println("Usuário '" + nomeUsuario + "' criado com sucesso!");
    }

    private static void criarNovoSublueddit() {
        System.out.print("Digite o nome do novo sublueddit: b/");
        String nomeSublueddit = scanner.nextLine();
        Sublueddit novoSublueddit = new Sublueddit(nomeSublueddit);
        subluedditDAO.salvar(novoSublueddit);
        sublueddits.add(novoSublueddit);
        System.out.println("Sublueddit 'b/" + nomeSublueddit + "' criado com sucesso!");
    }

    private static void carregarDadosDoBanco() {
        System.out.println("Carregando dados do banco...");
        // A lógica de Eager Loading nos DAOs de Usuário e Sublueddit já carrega tudo
        usuarios = (List<Usuario>)(List<?>) usuarioDAO.listarTodosEagerLoading();
        sublueddits = (List<Sublueddit>)(List<?>) subluedditDAO.listarTodosEagerLoading();

        if (usuarios.isEmpty()) {
            System.out.println("Banco de dados vazio. Populando com dados de exemplo...");
            // ... (a lógica de popular dados iniciais pode ser mantida aqui)
        }
        System.out.println("Dados carregados com sucesso!");
    }
}
