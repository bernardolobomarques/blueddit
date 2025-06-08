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

    // Métodos para upvote/downvote em posts e comentários (opcionalmente pode ser na main)
    // Para simplificar no CLI, vamos lidar com a seleção e a lógica de voto na Main.
    // Mas se quiser encapsular mais, poderia ter aqui:
    // public void upvotePost(Post post) { ... }
    // public void downvotePost(Post post) { ... }
    // public void upvoteComentario(Comentario comentario) { ... } // Requer acesso ao comentário
    // public void downvoteComentario(Comentario comentario) { ... } // Requer acesso ao comentário
}
