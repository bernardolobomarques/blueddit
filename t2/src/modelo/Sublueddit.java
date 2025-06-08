package modelo;

import java.util.ArrayList;
import java.util.List;

public class Sublueddit {
    private String nome;
    private List<Post> posts;

    public Sublueddit(String nome) {
        this.nome = nome;
        this.posts = new ArrayList<>();
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
