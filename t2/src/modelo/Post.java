package modelo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Post extends Conteudo {

    private Sublueddit sublueddit;
    private List<Comentario> comentarios;

    
    public Post(Usuario usuario, Sublueddit sublueddit, String descricao) {
        super(usuario, descricao); 
        if (usuario == null || sublueddit == null) {
            throw new IllegalArgumentException("Usuário e Sublueddit não podem ser nulos ao criar um Post.");
        }
        this.sublueddit = sublueddit;
        this.comentarios = new ArrayList<>();
    }


    public Post(Usuario usuario, Sublueddit sublueddit, String descricao, LocalDateTime dataPublicacao, int upvote, int downvote) {
        super(usuario, descricao, dataPublicacao, upvote, downvote); 
        if (usuario == null || sublueddit == null) {
            throw new IllegalArgumentException("Usuário e Sublueddit não podem ser nulos ao criar um Post.");
        }
        this.sublueddit = sublueddit;
        this.comentarios = new ArrayList<>();
    }


    public Sublueddit getSublueddit() { return sublueddit; }
    public void setSublueddit(Sublueddit sublueddit) { this.sublueddit = sublueddit; }
    public List<Comentario> getComentarios() { return comentarios; }
    public void adicionarComentario(Comentario comentario) {
        if (comentario != null) {
            this.comentarios.add(comentario);
        }
    }

    @Override
    public String getTipo() {
        return "POST";
    }
}
