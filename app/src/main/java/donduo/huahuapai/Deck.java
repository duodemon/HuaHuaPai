package donduo.huahuapai;
import java.util.Random;
import java.util.ArrayList;

public class Deck {
    private Random random = new Random();
    private ArrayList<Card> deck;

    Deck () {
        deck = new ArrayList<>();
        for (int i=0; i < 2; i++) {
            deck.add(new Card("operetta", 3, Card.Color.MIXED, Card.OPERETTA, i));
            deck.add(new Card("opera", 6, Card.Color.MIXED, Card.OPERA, i));
            deck.add(new Card("little bull", 8, Card.Color.BLACK, Card.LITTLE_BULL, i));
            deck.add(new Card("big bull", 9, Card.Color.BLACK, Card.BIG_BULL, i));
        }
        for (int i=0; i < 4; i++) {
            deck.add(new Card("red eyes", 2, Card.Color.RED, Card.RED_EYES, i));
            deck.add(new Card("black eyes", 4, Card.Color.BLACK, Card.BLACK_EYES, i));
            deck.add(new Card("obliques", 6, Card.Color.BLACK, Card.OBLIQUES, i));
            deck.add(new Card("sixes", 6, Card.Color.MIXED, Card.SIXES, i));
            deck.add(new Card("sevens", 7, Card.Color.MIXED, Card.SEVENS, i));
            deck.add(new Card("red eights", 8, Card.Color.RED, Card.RED_EIGHTS, i));
            deck.add(new Card("red ten", 10, Card.Color.MIXED, Card.RED_TEN, i));
            deck.add(new Card("black ten", 10, Card.Color.BLACK, Card.BLACK_TEN, i));
            deck.add(new Card("tiger", 11, Card.Color.BLACK, Card.TIGER, i));
            deck.add(new Card("god", 12, Card.Color.MIXED, Card.GOD, i));
        }
    }

    public void shuffle () {
        int size = deck.size();
        ArrayList<Card> shuffledDeck = new ArrayList<>();
        for (int i=0; i < size; i++) {
            int index = random.nextInt( size - i);
            shuffledDeck.add(deck.remove(index));
        }
        deck = shuffledDeck;
    }

    public void cut () {
        int size = deck.size();
        int index = random.nextInt(size);
        ArrayList<Card> subDeck1 = new ArrayList<>(deck.subList(0, index));
        ArrayList<Card> subDeck2 = new ArrayList<>(deck.subList(index, size));
        subDeck2.addAll(subDeck1);
        deck = subDeck2;
    }

    public Card draw () {
        return deck.remove(0);
    }

    public int getSize () {
        return deck.size();
    }

    private void printDeck() {
        for (Card card: deck
             ) {
            System.out.println(card.toString());
        }
        System.out.println("---");
    }
}
