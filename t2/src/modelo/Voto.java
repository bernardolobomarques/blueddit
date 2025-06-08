package modelo;

public interface Voto {
    void upvote();
    void downvote();
    int getUpvoteCount();
    int getDownvoteCount();
}