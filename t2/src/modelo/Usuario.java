package modelo;

import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private String nome;
    private List<Post> posts;

    public Usuario(String nome) {
        this.nome = nome;
        this.posts = new ArrayList<>();
    }

    public Post criarPost(Usuario usuario, String dataPublicada, String descricao, int upvote, int downvote, Comentario comentario) {
        Post novoPost = new Post(usuario, dataPublicada, descricao, 0, 0, comentario);
        posts.add(novoPost);
        return novoPost;
    }

    public Comentario comentar(Post post, String texto) {
        Comentario novoComentario = new Comentario(texto, this, post);
        post.adicionarComentario(novoComentario);
        return novoComentario;
    }

    public Post upvotePost(Post post) {
        post.upvote();
        return post;
    }

    public Post downvotePost(Post post) {
        post.downvote();
        return post;
    }

    public String getNome() {
        return nome;
    }
}