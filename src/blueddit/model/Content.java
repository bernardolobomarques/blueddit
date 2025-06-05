package blueddit.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Classe abstrata que representa um conteúdo criado por usuários
 * Implementa o pilar de abstração ao definir comportamentos comuns para diferentes tipos de conteúdo
 */
public abstract class Content implements Votable {
    protected int id;
    protected int userId;
    protected Date creationDate;
    protected List<Integer> upvoters;
    protected List<Integer> downvoters;

    public Content() {
        this.creationDate = new Date();
        this.upvoters = new ArrayList<>();
        this.downvoters = new ArrayList<>();
    }

    public Content(int id, int userId) {
        this();
        this.id = id;
        this.userId = userId;
    }

    // Métodos abstratos que deverão ser implementados por subclasses
    public abstract String getType();
    public abstract String getPreview();

    // Implementação dos métodos da interface Votable
    @Override
    public boolean upvote(int userId) {
        if (this.userId == userId || upvoters.contains(userId)) {
            return false; // Não pode votar no próprio conteúdo ou votar duas vezes
        }

        // Se já votou negativo, remove o downvote primeiro
        if (downvoters.contains(userId)) {
            downvoters.remove(Integer.valueOf(userId));
        }

        upvoters.add(userId);
        return true;
    }

    @Override
    public boolean downvote(int userId) {
        if (this.userId == userId || downvoters.contains(userId)) {
            return false; // Não pode votar no próprio conteúdo ou votar duas vezes
        }

        // Se já votou positivo, remove o upvote primeiro
        if (upvoters.contains(userId)) {
            upvoters.remove(Integer.valueOf(userId));
        }

        downvoters.add(userId);
        return true;
    }

    @Override
    public int getScore() {
        return upvoters.size() - downvoters.size();
    }

    @Override
    public List<Integer> getVoters() {
        List<Integer> allVoters = new ArrayList<>(upvoters);
        allVoters.addAll(downvoters);
        return allVoters;
    }

    // Getters e setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public List<Integer> getUpvoters() {
        return new ArrayList<>(upvoters);
    }

    public void setUpvoters(List<Integer> upvoters) {
        this.upvoters = new ArrayList<>(upvoters);
    }

    public List<Integer> getDownvoters() {
        return new ArrayList<>(downvoters);
    }

    public void setDownvoters(List<Integer> downvoters) {
        this.downvoters = new ArrayList<>(downvoters);
    }
}
