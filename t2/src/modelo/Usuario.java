package modelo;

import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private int id; // Adicionado
    private String nome;
    private List<Post> posts;

    public Usuario(String nome) {
        this.nome = nome;
        this.posts = new ArrayList<>();
    }

    // Construtor para o DAO
    public Usuario(int id, String nome) { // Adicionado
        this.id = id;
        this.nome = nome;
        this.posts = new ArrayList<>();
    }

    // Getters e Setters para 'id'
    public int getId() { // Adicionado
        return id;
    }

    public void setId(int id) { // Adicionado
        this.id = id;
    }

    public Post criarPost(Usuario usuario, String dataPublicada, String descricao, int upvote, int downvote, Comentario comentario) {
        // Este métodos precisa ser ajustado para incluir Sublueddit no Post
        // Por agora, vamos manter o construtor original do Post aqui, mas ele será alterado abaixo.
        Post novoPost = new Post(usuario, dataPublicada, descricao, 0, 0, comentario); // Comentario aqui deve ser null ou omitido
        posts.add(novoPost);
        return novoPost;
    }

    public Comentario comentar(Post post, String texto) {
        Comentario novoComentario = new Comentario(texto, this, post);
        post.adicionarComentario(novoComentario);
        return novoComentario;
    }

    public void votar(Voto votavel, String tipoVoto) { // Adicionado (da resposta anterior sobre a interface Voto)
        if ("upvote".equalsIgnoreCase(tipoVoto)) {
            votavel.upvote();
        } else if ("downvote".equalsIgnoreCase(tipoVoto)) {
            votavel.downvote();
        } else {
            System.out.println("Tipo de voto inválido.");
        }
    }

    public Post upvotePost(Post post) {
        votar(post, "upvote");
        return post;
    }

    public Post downvotePost(Post post) {
        votar(post, "downvote");
        return post;
    }

    public String getNome() {
        return nome;
    }

    public List<Post> getPosts() {
        return posts;
    }
}