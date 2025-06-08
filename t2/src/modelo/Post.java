package modelo;

import java.util.List;
import java.util.ArrayList;

public class Post implements Voto { // Implementa Voto
    private int id; // Adicionado
    private Usuario usuario;
    private Sublueddit sublueddit; // Adicionado
    private String dataPublicada;
    private String descricao;
    private int upvote;
    private int downvote;
    private List<Comentario> comentarios;

    // Construtor original - pode ser ajustado ou manter por compatibilidade
    // Idealmente, você passaria o Sublueddit aqui também.
    public Post(Usuario usuario, String dataPublicada, String descricao, int upvote, int downvote, Comentario comentario) {
        this.usuario = usuario;
        this.dataPublicada = dataPublicada;
        this.descricao = descricao;
        this.upvote = upvote;
        this.downvote = downvote;
        this.comentarios = new ArrayList<>();
        // this.sublueddit = null; // Precisa ser setado
    }

    // Novo construtor para uso com DAOs (com ID e Sublueddit)
    public Post(int id, Usuario usuario, Sublueddit sublueddit, String dataPublicada, String descricao, int upvote, int downvote) { // Adicionado
        this.id = id;
        this.usuario = usuario;
        this.sublueddit = sublueddit;
        this.dataPublicada = dataPublicada;
        this.descricao = descricao;
        this.upvote = upvote;
        this.downvote = downvote;
        this.comentarios = new ArrayList<>();
    }

    // Getters e Setters para 'id'
    public int getId() { // Adicionado
        return id;
    }

    public void setId(int id) { // Adicionado
        this.id = id;
    }

    // Getters e Setters para 'sublueddit'
    public Sublueddit getSublueddit() { // Adicionado
        return sublueddit;
    }

    public void setSublueddit(Sublueddit sublueddit) { // Adicionado
        this.sublueddit = sublueddit;
    }

    public void adicionarComentario(Comentario comentario) {
        comentarios.add(comentario);
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getDescricao() {
        return descricao;
    }

    // Métodos da interface Voto
    @Override
    public void upvote() {
        this.upvote++;
    }

    @Override
    public void downvote() {
        this.downvote++;
    }

    @Override
    public int getUpvoteCount() {
        return this.upvote;
    }

    @Override
    public int getDownvoteCount() {
        return this.downvote;
    }

    // Os métodos originais getUpvote/getDownvote podem ser mantidos para compatibilidade,
    // mas a interface Voto já fornece o getUpvoteCount/getDownvoteCount.
    // O ideal seria usar apenas os da interface.
    public int getUpvote() {
        return upvote;
    }

    public void setUpvote(int upvote) { // Corrigido o nome do parâmetro de 'upvaote' para 'upvote'
        this.upvote = upvote;
    }

    public int getDownvote() {
        return downvote;
    }

    public void setDownvote(int downvote) {
        this.downvote = downvote;
    }

    public String getDataPublicada() {
        return dataPublicada;
    }
}