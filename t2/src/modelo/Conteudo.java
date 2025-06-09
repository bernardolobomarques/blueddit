package modelo;

import java.time.LocalDateTime;

// 1. CLASSE ABSTRATA
public abstract class Conteudo implements Voto {

    private int id;
    private Usuario autor;
    private String texto;
    private LocalDateTime dataCriacao;
    private int upvotes;
    private int downvotes;

    public Conteudo(Usuario autor, String texto) {
        this.autor = autor;
        this.texto = texto;
        this.dataCriacao = LocalDateTime.now();
        this.upvotes = 0;
        this.downvotes = 0;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Usuario getAutor() { return autor; }
    public void setAutor(Usuario autor) { this.autor = autor; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    // Métodos da interface Voto
    @Override
    public void upvote() { this.upvotes++; }
    @Override
    public void downvote() { this.downvotes++; }
    @Override
    public int getUpvoteCount() { return this.upvotes; }
    @Override
    public int getDownvoteCount() { return this.downvotes; }

    // Métodos para o DAO poder setar os valores do banco
    public void setUpvoteCount(int count) { this.upvotes = count; }
    public void setDownvoteCount(int count) { this.downvotes = count; }

    // Método abstrato para obter o tipo de conteúdo (útil no VotoDAO)
    public abstract String getTipo();
}
