package modelo;

import java.util.ArrayList;
import java.util.List;

public class Sublueddit {
    private int id;
    private String nome;
    private List<Post> posts;
    private List<Usuario> inscritos; // Relação N-N

    public Sublueddit(String nome) {
        this.nome = nome;
        this.posts = new ArrayList<>();
        this.inscritos = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public List<Post> getPosts() { return posts; }
    public void adicionarPost(Post post) { this.posts.add(post); }

    // Métodos para gerenciar inscritos
    public List<Usuario> getInscritos() { return inscritos; }
    public void setInscritos(List<Usuario> inscritos) { this.inscritos = inscritos; }
}
