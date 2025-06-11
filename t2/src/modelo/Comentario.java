package modelo;

import java.time.LocalDateTime;

public class Comentario extends Conteudo {

    private Post post;


    public Comentario(String texto, Usuario autor, Post post) {
        super(autor, texto);
        this.post = post;
    }

    
    public Comentario(String texto, Usuario autor, Post post, LocalDateTime dataPublicacao, int upvote, int downvote) {
        super(autor, texto, dataPublicacao, upvote, downvote); 
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
