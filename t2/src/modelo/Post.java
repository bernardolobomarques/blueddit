package modelo;

import java.util.List;
import java.util.ArrayList;

public class Post extends Conteudo {

    private Sublueddit sublueddit;
    private List<Comentario> comentarios;

    public Post(Usuario usuario, Sublueddit sublueddit, String descricao) {
        super(usuario, descricao); // Chama o construtor da classe pai (Conteudo)
        if (usuario == null || sublueddit == null) {
            throw new IllegalArgumentException("Usuário e Sublueddit não podem ser nulos ao criar um Post.");
        }
        this.sublueddit = sublueddit;
        this.comentarios = new ArrayList<>();
    }

    // Getters e Setters específicos de Post
    public Sublueddit getSublueddit() { return sublueddit; }
    public void setSublueddit(Sublueddit sublueddit) { this.sublueddit = sublueddit; }
    public List<Comentario> getComentarios() { return comentarios; }
    public void adicionarComentario(Comentario comentario) {
        if (comentario != null) {
            this.comentarios.add(comentario);
        }
    }

    // Sobrescrevendo o método getTexto() para retornar a descrição do post
    @Override
    public String getTexto() {
        return super.getTexto();
    }

    @Override
    public String getTipo() {
        return "POST";
    }
}
