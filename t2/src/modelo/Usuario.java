package modelo;

import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private int id;
    private String nome;
    private List<Post> posts;

    public Usuario(String nome) {
        this.nome = nome;
        this.posts = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Adiciona um comentário a um post. A criação do comentário agora é salva no banco pelo ComentarioDAO.
     * @param post O post a ser comentado.
     * @param texto O texto do comentário.
     * @return O objeto Comentario criado.
     */
    public Comentario comentar(Post post, String texto) {
        Comentario novoComentario = new Comentario(texto, this, post);
        post.adicionarComentario(novoComentario);
        // A persistência é feita na classe Main, que chama o DAO.
        return novoComentario;
    }

    /**
     * Aplica um voto (upvote/downvote) a um objeto votável.
     * @param votavel O objeto que implementa a interface Voto (ex: Post).
     * @param tipoVoto A string "upvote" ou "downvote".
     */
    public void votar(Voto votavel, String tipoVoto) {
        if ("upvote".equalsIgnoreCase(tipoVoto)) {
            votavel.upvote();
        } else if ("downvote".equalsIgnoreCase(tipoVoto)) {
            votavel.downvote();
        } else {
            System.out.println("Tipo de voto inválido.");
        }
        // A persistência da atualização é feita na classe Main, que chama o DAO.
    }

    public String getNome() {
        return nome;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void addPost(Post post) {
        if (post != null) {
            this.posts.add(post);
        }
    }
}
