package blueddit.model;

import java.util.List;

/**
 * Interface que define as operações de votação para conteúdos
 * Implementa o pilar do polimorfismo através de interfaces
 */
public interface Votable {
    /**
     * Adiciona um voto positivo
     * @param userId ID do usuário que votou
     * @return true se o voto foi registrado, false caso contrário
     */
    boolean upvote(int userId);

    /**
     * Adiciona um voto negativo
     * @param userId ID do usuário que votou
     * @return true se o voto foi registrado, false caso contrário
     */
    boolean downvote(int userId);

    /**
     * Retorna a pontuação atual (upvotes - downvotes)
     * @return pontuação atual
     */
    int getScore();

    /**
     * Retorna a lista de IDs de usuários que votaram
     * @return lista de IDs de usuários votantes
     */
    List<Integer> getVoters();
}
