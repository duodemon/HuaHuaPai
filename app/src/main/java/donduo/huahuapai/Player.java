package donduo.huahuapai;
import java.util.ArrayList;

public class Player {
    private CardList hand;
    private String name;
    private boolean isComputer;
    private int id;
    private int numberOfCardsCountedTowardsLimit;

    public CardList getHand() {
        return hand;
    }

    Player (String name, boolean isComputer, int id) {
        hand = new CardList();
        numberOfCardsCountedTowardsLimit = 0;
        this.isComputer = isComputer;
        this.id = id;
        if (name != null) {
            this.name = name;
        }
        else {
            this.name = "";
        }
    }

    public void increaseNumberOfCardsCountedTowardsLimit(int numberOfCards) {
        numberOfCardsCountedTowardsLimit += numberOfCards;
    }

    public void addCard (Card card) {
        card.setBelongsToPlayerId(id);
        hand.addCards(card);
    }

    public void playCards (Card card) {
        hand.removeCards(card);
    }

    public void playCards (CardList cards) {
        hand.removeCards(cards);
    }

    public void clearHand() {
        hand.removeAllCards();
    }

    public void resetNumberOfCardsCountedTowardsLimit() {
        numberOfCardsCountedTowardsLimit = 0;
    }

    public String getName() {
        return name;
    }

    public boolean isComputer() {
        return isComputer;
    }

    public int getNumberOfCardsCountedTowardsLimit () {
        return numberOfCardsCountedTowardsLimit;
    }
}
