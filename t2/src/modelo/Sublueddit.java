package modelo;

import java.util.ArrayList;
import java.util.List;

public class Sublueddit {
    private int id; // Adicionado
    private String nome;
    private List<Post> posts;

    public Sublueddit(String nome) {
        this.nome = nome;
        this.posts = new ArrayList<>();
    }

    // Construtor para o DAO
    public Sublueddit(int id, String nome) { // Adicionado
        this.id = id;
        this.nome = nome;
        this.posts = new ArrayList<>();
    }

    // Getters e Setters para 'id'
    public int getId() { // Adicionado
        return id;
    }

    public void setId(int id) { // Adicionado
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void adicionarPost(Post post) {
        this.posts.add(post);
    }
}