package modelo;

public class Comentario {
    private int id;
    private String texto;
    private Usuario autor;
    private Post post;

    public Comentario(String texto, Usuario autor, Post post) {
        this.texto = texto;
        this.autor = autor;
        this.post = post;
    }

    public Comentario(String texto, Usuario autor, Post post, int id) {
        this.texto = texto;
        this.autor = autor;
        this.post = post;
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public Usuario getAutor() {
        return autor;
    }

    public Post getPost() {
        return post;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}












