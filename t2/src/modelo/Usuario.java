package modelo;

import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private int id;
    private String nome;
    private List<Post> posts;
    private List<Sublueddit> inscricoes;

    public Usuario(String nome) {
        this.nome = nome;
        this.posts = new ArrayList<>();
        this.inscricoes = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public List<Post> getPosts() { return posts; }
    public void addPost(Post post) { this.posts.add(post); }

    // Métodos para gerenciar inscrições
    public List<Sublueddit> getInscricoes() { return inscricoes; }
    public void setInscricoes(List<Sublueddit> inscricoes) { this.inscricoes = inscricoes; }
    public void inscrever(Sublueddit sublueddit) { this.inscricoes.add(sublueddit); }
    public void desinscrever(Sublueddit sublueddit) { this.inscricoes.remove(sublueddit); }
}
