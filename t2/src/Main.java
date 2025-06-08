import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import modelo.Comentario;
import modelo.Sublueddit;
import modelo.Post;
import modelo.Usuario;
import modelo.Voto;
import dao.UsuarioDAO; // Importe seus DAOs
import dao.PostDAO;
import dao.ComentarioDAO;
import dao.SubluedditDAO;
import bd.ConexaoSQL; // Importe sua classe de conexão

public class Main {

    private static List<Sublueddit> sublueddits = new ArrayList<>();
    private static List<Usuario> usuarios = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Inicialização de alguns dados para exemplo
        // inicializarDadosExemplo();

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

        scanner.close();
    }

    private static void inicializarDadosExemplo() {
        // Exemplo de como usar os DAOs para persistir dados iniciais
        // Você precisaria de um try-catch-finally para cada bloco de DAO
        // para garantir que as conexões sejam fechadas.
        UsuarioDAO usuarioDao = null;
        SubluedditDAO subluedditDao = null;
        PostDAO postDao = null;
        ComentarioDAO comentarioDao = null;

        try {
            usuarioDao = new UsuarioDAO();
            subluedditDao = new SubluedditDAO();
            postDao = new PostDAO();
            comentarioDao = new ComentarioDAO();

            Usuario betoneira = new Usuario("betoneira");
            usuarioDao.salvar(betoneira);
            usuarios.add(betoneira); // Adiciona à lista em memória também

            Usuario Kitts = new Usuario("Kitts");
            usuarioDao.salvar(Kitts);
            usuarios.add(Kitts);

            Usuario trk = new Usuario("trk");
            usuarioDao.salvar(trk);
            usuarios.add(trk);

            Sublueddit bDC = new Sublueddit("DC");
            subluedditDao.salvar(bDC);
            sublueddits.add(bDC);

            Sublueddit bMarvel = new Sublueddit("Marvel");
            subluedditDao.salvar(bMarvel);
            sublueddits.add(bMarvel);

            String dataAtual = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            // CORREÇÃO: Utilizando o novo construtor de Post
            Post postDC1 = new Post(betoneira, bDC, dataAtual, "Superman é o mais forte do universo DC.", 0, 0);
            postDao.salvar(postDC1);
            bDC.adicionarPost(postDC1); // Adiciona à lista em memória do Sublueddit

            Post postDC2 = new Post(Kitts, bDC, dataAtual, "Batman ganha de todos os hérois existentes!", 0, 0);
            postDao.salvar(postDC2);
            bDC.adicionarPost(postDC2);

            Post postMarvel1 = new Post(trk, bMarvel, dataAtual, "Homem Aranha solta teia por outras partes?", 0, 0);
            postDao.salvar(postMarvel1);
            bMarvel.adicionarPost(postMarvel1);

            Post postMarvel2 = new Post(betoneira, bMarvel, dataAtual, "Talvez o Thanos estivesse certo... Alguém concorda comigo?", 0, 0);
            postDao.salvar(postMarvel2);
            bMarvel.adicionarPost(postMarvel2);

            Comentario com1 = new Comentario("Não, é o Batman!", Kitts, postDC1);
            comentarioDao.salvar(com1);
            Kitts.comentar(postDC1, com1.getTexto()); // Mantém a lógica de objeto em memória

            Comentario com2 = new Comentario("Concordo, ele é o melhor!", trk, postDC1);
            comentarioDao.salvar(com2);
            trk.comentar(postDC1, com2.getTexto());

            Comentario com3 = new Comentario("Talvez kkkk", betoneira, postMarvel1);
            comentarioDao.salvar(com3);
            betoneira.comentar(postMarvel1, com3.getTexto());

            System.out.println("Dados de exemplo inicializados e salvos no banco de dados.");

        } catch (RuntimeException e) {
            System.err.println("Erro durante a inicialização dos dados: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Garanta que as conexões sejam fechadas
            if (usuarioDao != null) usuarioDao.closeConnection();
            if (subluedditDao != null) subluedditDao.closeConnection();
            if (postDao != null) postDao.closeConnection();
            if (comentarioDao != null) comentarioDao.closeConnection();
        }
    }

    private static void criarNovoUsuario() {
        System.out.print("Digite o nome do novo usuário: ");
        String nomeUsuario = scanner.nextLine();
        Usuario novoUsuario = new Usuario(nomeUsuario);
        usuarios.add(novoUsuario);
        System.out.println("Usuário '" + nomeUsuario + "' criado com sucesso!");
    }

    private static void criarNovoSublueddit() {
        System.out.print("Digite o nome do novo sublueddit : b/");
        String nomeSublueddit = scanner.nextLine();
        Sublueddit novoSublueddit = new Sublueddit(nomeSublueddit);
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
        scanner.nextLine(); // Consumir a quebra de linha

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
            scanner.nextLine(); // Consumir a quebra de linha

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
        for (int i = 0; i < sublueddit.getPosts().size(); i++) {
            Post post = sublueddit.getPosts().get(i);
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

            if (escolhaPost > 0 && escolhaPost <= sublueddit.getPosts().size()) {
                Post postSelecionado = sublueddit.getPosts().get(escolhaPost - 1);
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
                    selecionarUsuarioParaVoto(post, null, "upvote");
                    break;
                case 2:
                    selecionarUsuarioParaVoto(post, null, "downvote");
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

    private static void selecionarUsuarioParaVoto(Post post, Comentario comentario, String tipoVoto) {
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
            if (post != null) {
                if (tipoVoto.equals("upvote")) {
                    usuarioVotante.upvotePost(post);
                    System.out.println("Upvote no post '" + post.getDescricao() + "' por " + usuarioVotante.getNome() + "!");
                } else if (tipoVoto.equals("downvote")) {
                    usuarioVotante.downvotePost(post);
                    System.out.println("Downvote no post '" + post.getDescricao() + "' por " + usuarioVotante.getNome() + "!");
                }
            }
            // Lógica para votar em comentários não está implementada nas classes Usuario,
            // mas poderia ser adicionada similarmente.
            // if (comentario != null) { ... }
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
        scanner.nextLine(); // Consumir a quebra de linha

        if (escolhaUsuario > 0 && escolhaUsuario <= usuarios.size()) {
            Usuario autor = usuarios.get(escolhaUsuario - 1);

            System.out.print("Digite a descrição do seu post: ");
            String descricao = scanner.nextLine();
            String dataAtual = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            Post novoPost = autor.criarPost(autor, dataAtual, descricao, 0, 0, null);
            sublueddit.adicionarPost(novoPost);
            System.out.println("Post criado com sucesso em r/" + sublueddit.getNome() + "!");
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
        scanner.nextLine(); // Consumir a quebra de linha

        if (escolhaUsuario > 0 && escolhaUsuario <= usuarios.size()) {
            Usuario autorComentario = usuarios.get(escolhaUsuario - 1);
            System.out.print("Digite seu comentário: ");
            String textoComentario = scanner.nextLine();
            autorComentario.comentar(post, textoComentario);
            System.out.println("Comentário adicionado com sucesso!");
        } else {
            System.out.println("Usuário inválido.");
        }
    }
}