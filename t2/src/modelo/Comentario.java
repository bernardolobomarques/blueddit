package modelo;

import java.time.LocalDateTime;

public class Comentario extends Conteudo {

    private Post post;

    /**
     * Construtor para criar um NOVO comentário.
     */
    public Comentario(String texto, Usuario autor, Post post) {
        super(autor, texto); // Chama o construtor de 2 argumentos da classe pai
        this.post = post;
    }

    /**
     * NOVO CONSTRUTOR para carregar um comentário do BANCO DE DADOS.
     */
    public Comentario(String texto, Usuario autor, Post post, LocalDateTime dataPublicacao, int upvote, int downvote) {
        super(autor, texto, dataPublicacao, upvote, downvote); // Chama o construtor de 5 argumentos da classe pai
        this.post = post;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @Override
    public String getTipo() {
        return "COMENTARIO";
    }
}
