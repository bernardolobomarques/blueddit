package modelo;

public class Comentario extends Conteudo {

    private Post post; // Comentário pertence a um Post

    public Comentario(String texto, Usuario autor, Post post) {
        super(autor, texto); // Chama o construtor da classe pai (Conteudo)
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
