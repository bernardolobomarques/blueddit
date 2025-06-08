package modelo;

public class Comentario {
    private String texto;
    private Usuario autor;
    private Post post;

    public Comentario(String texto, Usuario autor, Post post) {
        this.texto = texto;
        this.autor = autor;
        this.post = post;
    }

    public String getTexto() {
        return texto;
    }

    public Usuario getAutor() {
        return autor;
    }
}












