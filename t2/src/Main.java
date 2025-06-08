import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import modelo.Comentario;
import modelo.Sublueddit;
import modelo.Post;
import modelo.Usuario;
import dao.UsuarioDAO;
import dao.PostDAO;
import dao.ComentarioDAO;
import dao.SubluedditDAO;
import bd.ConexaoSQL; // Importe sua classe de conexão

public class Main {

    // DAOs para acesso ao banco de dados
    private static UsuarioDAO usuarioDAO;
    private static SubluedditDAO subluedditDAO;
    private static PostDAO postDAO;
    private static ComentarioDAO comentarioDAO;

    // Listas em memória que refletem os dados do banco.
    private static List<Sublueddit> sublueddits = new ArrayList<>();
    private static List<Usuario> usuarios = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // --- GESTÃO CENTRALIZADA DA CONEXÃO ---
        Connection connection = null;
        try {
            // 1. Cria uma única conexão para toda a aplicação
            connection = ConexaoSQL.recuperaConexao();

            // 2. Inicializa todos os DAOs com a mesma conexão
            usuarioDAO = new UsuarioDAO(connection);
            subluedditDAO = new SubluedditDAO(connection);
            postDAO = new PostDAO(connection);
            comentarioDAO = new ComentarioDAO(connection);

            // 3. Roda a aplicação
            carregarDadosDoBanco();
            executarMenuPrincipal();

        } finally {
            // 4. Fecha a conexão única no final de tudo
            if (connection != null) {
                ConexaoSQL.fechaConexao(connection);
            }
            scanner.close();
        }
    }

    private static void executarMenuPrincipal() {
        int opcaoPrincipal;
        do {
            System.out.println("\n--- Menu Principal ---");
            System.out.println("1. Entrar em um Sublueddit");
            System.out.println("2. Criar novo Sublueddit");
            System.out.println("3. Criar novo Usuário");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");
            opcaoPrincipal = scanner.nextInt();
            scanner.nextLine(); // Consumir a quebra de linha

            switch (opcaoPrincipal) {
                case 1:
                    entrarSublueddit();
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

    /**
     * Carrega todos os usuários e sublueddits (com seus posts e comentários) do banco.
     * Se o banco estiver vazio, chama o método para popular com dados iniciais.
     */
    private static void carregarDadosDoBanco() {
        System.out.println("Carregando dados do banco...");
        usuarios = (List<Usuario>)(List<?>) usuarioDAO.listarTodosEagerLoading();
        sublueddits = (List<Sublueddit>)(List<?>) subluedditDAO.listarTodosEagerLoading();

        if (usuarios.isEmpty() && sublueddits.isEmpty()) {
            System.out.println("Banco de dados vazio. Populando com dados de exemplo...");
            popularDadosIniciais();
            // Recarrega os dados após popular.
            usuarios = (List<Usuario>)(List<?>) usuarioDAO.listarTodosEagerLoading();
            sublueddits = (List<Sublueddit>)(List<?>) subluedditDAO.listarTodosEagerLoading();
        }
        System.out.println("Dados carregados com sucesso!");
    }

    // O resto da classe Main continua igual...

    private static void popularDadosIniciais() {
        try {
            Usuario betoneira = new Usuario("betoneira");
            usuarioDAO.salvar(betoneira);

            Usuario kitts = new Usuario("Kitts");
            usuarioDAO.salvar(kitts);

            Usuario trk = new Usuario("trk");
            usuarioDAO.salvar(trk);

            Sublueddit bDC = new Sublueddit("DC");
            subluedditDAO.salvar(bDC);

            Sublueddit bMarvel = new Sublueddit("Marvel");
            subluedditDAO.salvar(bMarvel);

            String dataAtual = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            Post postDC1 = new Post(betoneira, bDC, dataAtual, "Superman é o mais forte do universo DC.", 0, 0);
            postDAO.salvar(postDC1);

            Post postDC2 = new Post(kitts, bDC, dataAtual, "Batman ganha de todos os hérois existentes!", 0, 0);
            postDAO.salvar(postDC2);

            Post postMarvel1 = new Post(trk, bMarvel, dataAtual, "Homem Aranha solta teia por outras partes?", 0, 0);
            postDAO.salvar(postMarvel1);

            Post postMarvel2 = new Post(betoneira, bMarvel, dataAtual, "Talvez o Thanos estivesse certo... Alguém concorda comigo?", 0, 0);
            postDAO.salvar(postMarvel2);

            Comentario com1 = new Comentario("Não, é o Batman!", kitts, postDC1);
            comentarioDAO.salvar(com1);

            Comentario com2 = new Comentario("Concordo, ele é o melhor!", trk, postDC1);
            comentarioDAO.salvar(com2);

            Comentario com3 = new Comentario("Talvez kkkk", betoneira, postMarvel1);
            comentarioDAO.salvar(com3);

            System.out.println("Dados de exemplo inicializados e salvos no banco de dados.");

        } catch (RuntimeException e) {
            System.err.println("Erro durante a inicialização dos dados: " + e.getMessage());
            e.printStackTrace();
        }
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

    private static void entrarSublueddit() {
        if (sublueddits.isEmpty()) {
            System.out.println("Nenhum sublueddit disponível. Crie um primeiro.");
            return;
        }

        System.out.println("\n--- Sublueddits Disponíveis ---");
        for (int i = 0; i < sublueddits.size(); i++) {
            System.out.println((i + 1) + ". b/" + sublueddits.get(i).getNome());
        }
        System.out.print("Escolha um sublueddit (digite o número): ");
        int escolhaSublueddit = scanner.nextInt();
        scanner.nextLine();

        if (escolhaSublueddit > 0 && escolhaSublueddit <= sublueddits.size()) {
            Sublueddit subluedditSelecionado = sublueddits.get(escolhaSublueddit - 1);
            menuSublueddit(subluedditSelecionado);
        } else {
            System.out.println("Opção inválida de sublueddit.");
        }
    }

    private static void menuSublueddit(Sublueddit sublueddit) {
        int opcaoSublueddit;
        do {
            System.out.println("\n--- b/" + sublueddit.getNome() + " ---");
            System.out.println("1. Ver Posts");
            System.out.println("2. Criar Post");
            System.out.println("3. Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");
            opcaoSublueddit = scanner.nextInt();
            scanner.nextLine();

            switch (opcaoSublueddit) {
                case 1:
                    verPosts(sublueddit);
                    break;
                case 2:
                    criarPost(sublueddit);
                    break;
                case 3:
                    System.out.println("Voltando ao Menu Principal...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcaoSublueddit != 3);
    }

    private static void verPosts(Sublueddit sublueddit) {
        if (sublueddit.getPosts().isEmpty()) {
            System.out.println("Nenhum post neste sublueddit ainda.");
            return;
        }

        System.out.println("\n--- Posts em b/" + sublueddit.getNome() + " ---");
        List<Post> postsDoSublueddit = sublueddit.getPosts();
        for (int i = 0; i < postsDoSublueddit.size(); i++) {
            Post post = postsDoSublueddit.get(i);
            System.out.println((i + 1) + ". [" + post.getUpvoteCount() + "▲ " + post.getDownvoteCount() + "▼] " +
                    post.getDescricao() + " (Por: " + post.getUsuario().getNome() + ")");
            System.out.println("   Comentários (" + post.getComentarios().size() + ")");
        }

        System.out.println("-------------------------------------");
        System.out.println("1. Interagir com um Post (Upvote/Downvote/Comentar)");
        System.out.println("2. Voltar");
        System.out.print("Escolha uma opção: ");
        int escolhaPostOpcao = scanner.nextInt();
        scanner.nextLine();

        if (escolhaPostOpcao == 1) {
            System.out.print("Digite o número do Post para interagir: ");
            int escolhaPost = scanner.nextInt();
            scanner.nextLine();

            if (escolhaPost > 0 && escolhaPost <= postsDoSublueddit.size()) {
                Post postSelecionado = postsDoSublueddit.get(escolhaPost - 1);
                menuInteracaoPost(postSelecionado);
            } else {
                System.out.println("Número de post inválido.");
            }
        }
    }

    private static void menuInteracaoPost(Post post) {
        int opcaoInteracao;
        do {
            System.out.println("\n--- Interagindo com Post ---");
            System.out.println("Título: " + post.getDescricao());
            System.out.println("Autor: " + post.getUsuario().getNome());
            System.out.println("Upvotes: " + post.getUpvoteCount() + " | Downvotes: " + post.getDownvoteCount());
            System.out.println("\nComentários:");
            if (post.getComentarios().isEmpty()) {
                System.out.println("  Nenhum comentário ainda.");
            } else {
                for (int i = 0; i < post.getComentarios().size(); i++) {
                    Comentario comentario = post.getComentarios().get(i);
                    System.out.println("  " + (i + 1) + ". " + comentario.getTexto() + " (Por: " + comentario.getAutor().getNome() + ")");
                }
            }

            System.out.println("\n--- Opções de Interação ---");
            System.out.println("1. Dar Upvote no Post");
            System.out.println("2. Dar Downvote no Post");
            System.out.println("3. Adicionar Comentário");
            System.out.println("4. Voltar");
            System.out.print("Escolha uma opção: ");
            opcaoInteracao = scanner.nextInt();
            scanner.nextLine();

            switch (opcaoInteracao) {
                case 1:
                    selecionarUsuarioParaVoto(post, "upvote");
                    break;
                case 2:
                    selecionarUsuarioParaVoto(post, "downvote");
                    break;
                case 3:
                    adicionarComentario(post);
                    break;
                case 4:
                    System.out.println("Voltando aos posts...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcaoInteracao != 4);
    }

    private static void selecionarUsuarioParaVoto(Post post, String tipoVoto) {
        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário disponível para votar.");
            return;
        }

        System.out.println("\n--- Selecione o Usuário que está votando ---");
        for (int i = 0; i < usuarios.size(); i++) {
            System.out.println((i + 1) + ". " + usuarios.get(i).getNome());
        }
        System.out.print("Escolha um usuário (digite o número): ");
        int escolhaUsuario = scanner.nextInt();
        scanner.nextLine();

        if (escolhaUsuario > 0 && escolhaUsuario <= usuarios.size()) {
            Usuario usuarioVotante = usuarios.get(escolhaUsuario - 1);
            if (tipoVoto.equals("upvote")) {
                post.upvote();
                System.out.println("Upvote no post '" + post.getDescricao() + "' por " + usuarioVotante.getNome() + "!");
            } else if (tipoVoto.equals("downvote")) {
                post.downvote();
                System.out.println("Downvote no post '" + post.getDescricao() + "' por " + usuarioVotante.getNome() + "!");
            }

            postDAO.atualizar(post);
            System.out.println("Voto salvo no banco de dados.");

        } else {
            System.out.println("Usuário inválido.");
        }
    }

    private static void criarPost(Sublueddit sublueddit) {
        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário disponível para criar posts. Crie um primeiro.");
            return;
        }

        System.out.println("\n--- Selecione o Usuário para criar o Post ---");
        for (int i = 0; i < usuarios.size(); i++) {
            System.out.println((i + 1) + ". " + usuarios.get(i).getNome());
        }
        System.out.print("Escolha um usuário (digite o número): ");
        int escolhaUsuario = scanner.nextInt();
        scanner.nextLine();

        if (escolhaUsuario > 0 && escolhaUsuario <= usuarios.size()) {
            Usuario autor = usuarios.get(escolhaUsuario - 1);

            System.out.print("Digite a descrição do seu post: ");
            String descricao = scanner.nextLine();
            String dataAtual = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            Post novoPost = new Post(autor, sublueddit, dataAtual, descricao, 0, 0);

            postDAO.salvar(novoPost);

            sublueddit.adicionarPost(novoPost);

            System.out.println("Post criado com sucesso em b/" + sublueddit.getNome() + "!");
        } else {
            System.out.println("Usuário inválido.");
        }
    }

    private static void adicionarComentario(Post post) {
        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário disponível para comentar. Crie um primeiro.");
            return;
        }

        System.out.println("\n--- Selecione o Usuário para adicionar o Comentário ---");
        for (int i = 0; i < usuarios.size(); i++) {
            System.out.println((i + 1) + ". " + usuarios.get(i).getNome());
        }
        System.out.print("Escolha um usuário (digite o número): ");
        int escolhaUsuario = scanner.nextInt();
        scanner.nextLine();

        if (escolhaUsuario > 0 && escolhaUsuario <= usuarios.size()) {
            Usuario autorComentario = usuarios.get(escolhaUsuario - 1);
            System.out.print("Digite seu comentário: ");
            String textoComentario = scanner.nextLine();

            Comentario novoComentario = new Comentario(textoComentario, autorComentario, post);

            comentarioDAO.salvar(novoComentario);

            post.adicionarComentario(novoComentario);

            System.out.println("Comentário adicionado com sucesso!");
        } else {
            System.out.println("Usuário inválido.");
        }
    }
}
