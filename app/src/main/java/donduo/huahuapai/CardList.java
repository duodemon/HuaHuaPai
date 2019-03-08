package donduo.huahuapai;

import android.content.Intent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CardList {
    private ArrayList<Card> cards;
    private HashMap<Integer, Integer> cardCount; //for easier access of the card list
    public enum CardTypes {
        SINGLE, PAIR, TRIPLE, QUAD, SINGLE_FISH, DOUBLE_FISH, TRIPLE_FISH, QUAD_FISH, SINGLE_PENDULUM,
        DOUBLE_PENDULUM, TRIPLE_PENDULUM, QUAD_PENDULUM, PAIR_OPERA, TRIPLE_OPERA, QUAD_OPERA, PAIR_BULL,
        TRIPLE_BULL, QUAD_BULL, NONE, OPERAS, BULLS, FISHES, PENDULUMS, MULTIPLES

    }

    public CardList() {
        cards = new ArrayList<>();
        cardCount = new HashMap<>();
    }

    public boolean contains(Card card) {
        return cards.contains(card);
    }

    public void removeAllCards() {
        cards.clear();
        cardCount.clear();
    }

    public void removeCards(Card card) {
        cards.remove(card);
        cardCount.put(card.getTypeId(), cardCount.get(card.getTypeId()) - 1);
        if (cardCount.get(card.getTypeId()) == 0) {
            cardCount.remove(card.getTypeId());
        }
    }

    public void removeCards(CardList cardsToRemove) {
        for (Card card: cardsToRemove.getCards()
             ) {
            cards.remove(card);
            cardCount.put(card.getTypeId(), cardCount.get(card.getTypeId()) - 1);
            if (cardCount.get(card.getTypeId()) == 0) {
                cardCount.remove(card.getTypeId());
            }
        }
    }

    public void addCards(Card card) {
        cards.add(card);
        if (cardCount.containsKey(card.getTypeId())) {
            cardCount.put(card.getTypeId(), cardCount.get(card.getTypeId()) + 1);
        }
        else {
            cardCount.put(card.getTypeId(), 1);
        }
    }

    public void addCards(ArrayList<Card> cardsToAdd){
        cards.addAll(cardsToAdd);
        for (Card card: cards
        ) {
            if (cardCount.containsKey(card.getTypeId())) {
                cardCount.put(card.getTypeId(), cardCount.get(card.getTypeId()) + 1);
            }
            else {
                cardCount.put(card.getTypeId(), 1);
            }
        }
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public HashMap<Integer, Integer> getCardCount() {
        return cardCount;
    }

    /**
     * gets the card type explained in the rules of the game
     * @return the card type of this card list
     */
    public CardTypes getCardType () {
        if (cards.size() == 0) {
            return CardTypes.NONE;
        }
        else if (cards.size() == 1) {
            return CardTypes.SINGLE;
        }
        if (cards.size() == 2) {
            if (cardCount.keySet().size() == 1) {
                return CardTypes.PAIR;
            }
            else if (cardCount.containsKey(Card.OPERETTA) && cardCount.containsKey(Card.OPERA)) {
                return CardTypes.PAIR_OPERA;
            }
            else if (cardCount.containsKey(Card.LITTLE_BULL) && cardCount.containsKey(Card.BIG_BULL)) {
                return CardTypes.PAIR_BULL;
            }
        }
        else if (cards.size() == 3) {
            if (cardCount.keySet().size() == 1) {
                return CardTypes.TRIPLE;
            }
            else if (cardCount.containsKey(Card.OPERETTA) && cardCount.containsKey(Card.OPERA)
                    && cardCount.keySet().size() == 2) {
                return CardTypes.TRIPLE_OPERA;
            }
            else if (cardCount.containsKey(Card.LITTLE_BULL) && cardCount.containsKey(Card.BIG_BULL)
                    && cardCount.keySet().size() == 2) {
                return CardTypes.TRIPLE_BULL;
            }
            else if (cardCount.containsKey(Card.RED_EYES) && cardCount.containsKey(Card.BLACK_EYES)
                    && cardCount.containsKey(Card.OBLIQUES)) {
                return CardTypes.SINGLE_FISH;
            }
            else if (cardCount.containsKey(Card.RED_EIGHTS) && cardCount.containsKey(Card.BLACK_TEN)
                    && cardCount.containsKey(Card.GOD)) {
                return CardTypes.SINGLE_PENDULUM;
            }
        }
        else if (cards.size() == 4) {
            if (cardCount.keySet().size() == 1) {
                return CardTypes.QUAD;
            }
            else if (cardCount.containsKey(Card.OPERETTA) && cardCount.containsKey(Card.OPERA)
                    && cardCount.keySet().size() == 2) {
                return CardTypes.QUAD_OPERA;
            }
            else if (cardCount.containsKey(Card.LITTLE_BULL) && cardCount.containsKey(Card.BIG_BULL)
                    && cardCount.keySet().size() == 2) {
                return CardTypes.QUAD_BULL;
            }
        }
        return CardTypes.NONE;
        //TODO: The rest of the (obscure) card types
    }

    /**
     * gets the card(s) as indicated by the params
     * @param type the type of cards
     * @param count the number of said type
     * @param id the identification of the card - not needed for fish, pendulum, bull, or opera
     * @return an ArrayList of the cards
     */
    public ArrayList<Card> getSpecificCards(CardTypes type, int count, int id) {
        ArrayList<Card> tempCards = new ArrayList<>();
        switch (type) {
            case MULTIPLES:
                for (Card card: cards
                ) {
                    if (card.getTypeId() == id) {
                        tempCards.add(card);
                        if (tempCards.size() == count) {
                            break;
                        }
                    }
                }
                break;
            case OPERAS:
                if (count == 2) {
                    boolean operaFound = false;
                    boolean operettaFound = false;
                    for (Card card : cards) {
                        if (card.getTypeId() == Card.OPERA && !operaFound) {
                            tempCards.add(card);
                            operaFound = true;
                        } else if (card.getTypeId() == Card.OPERETTA && !operettaFound) {
                            tempCards.add(card);
                            operettaFound = true;
                        }
                        if (operaFound && operettaFound) {
                            break;
                        }
                    }
                }
                else {
                    for (Card card : cards
                    ) {
                        if (card.getTypeId() == Card.OPERETTA || card.getTypeId() == Card.OPERA) {
                            tempCards.add(card);
                            if (tempCards.size() == count) {
                                break;
                            }
                        }
                    }
                }
                break;
            case BULLS:
                if (count == 2) {
                    boolean bigBullFound = false;
                    boolean littleBullFound = false;
                    for (Card card : cards) {
                        if (card.getTypeId() == Card.BIG_BULL && !bigBullFound) {
                            tempCards.add(card);
                            bigBullFound = true;
                        } else if (card.getTypeId() == Card.LITTLE_BULL && !littleBullFound) {
                            tempCards.add(card);
                            littleBullFound = true;
                        }
                        if (bigBullFound && littleBullFound) {
                            break;
                        }
                    }
                }
                else {
                    for (Card card: cards) {
                        if (card.getTypeId() == Card.BIG_BULL || card.getTypeId() == Card.LITTLE_BULL) {
                            tempCards.add(card);
                            if (tempCards.size() == count) {
                                break;
                            }
                        }
                    }
                }
                break;
            case PENDULUMS:
                int redEightCount = 0;
                int blackTenCount = 0;
                int godCount = 0;
                for (Card card: cards) {
                    if (card.getTypeId() == Card.RED_EYES && redEightCount != count) {
                        tempCards.add(card);
                        redEightCount += 1;
                    } else if (card.getTypeId() == Card.BLACK_EYES && blackTenCount != count) {
                        tempCards.add(card);
                        blackTenCount += 1;
                    } else if (card.getTypeId() == Card.OBLIQUES && godCount != count) {
                        tempCards.add(card);
                        godCount += 1;
                    }
                    if (redEightCount == count && blackTenCount == count && godCount == count) {
                        break;
                    }
                }
                break;
            case FISHES:
                int redEyesCount = 0;
                int blackEyesCount = 0;
                int obliquesCount = 0;
                for (Card card: cards) {
                    if (card.getTypeId() == Card.RED_EYES && redEyesCount != count) {
                        tempCards.add(card);
                        redEyesCount += 1;
                    } else if (card.getTypeId() == Card.BLACK_EYES && blackEyesCount != count) {
                        tempCards.add(card);
                        blackEyesCount += 1;
                    } else if (card.getTypeId() == Card.OBLIQUES && obliquesCount != count) {
                        tempCards.add(card);
                        obliquesCount += 1;
                    }
                    if (redEyesCount == count && blackEyesCount == count && obliquesCount == count) {
                        break;
                    }
                }
                break;
        }
        return tempCards;
    }

    /**
     * gets the largest cards of a particular type in this card list
     * @param type type of cards
     * @return an ArrayList of the largest possible cards; null if the card type does not exist in the list
     */
    public ArrayList<Card> getLargestCards(CardTypes type) {
        ArrayList<Card> largestCards = new ArrayList<>();
        switch (type) {
            case SINGLE:
                Card currentLargestCard = cards.get(0);
                for (Card card: cards
                     ) {
                    if (card.getValue() > currentLargestCard.getValue()) {
                        currentLargestCard = card;
                    }
                }
                largestCards.add(currentLargestCard);
                break;
            case PAIR:
                if (containsCardType(CardTypes.PAIR_BULL)) {
                    largestCards = getSpecificCards(CardTypes.BULLS, 2, -1);
                }
                else if (!containsCardType(CardTypes.PAIR)) {
                    return null;
                }
                else {
                    List<Integer> idsSorted = new ArrayList<>(cardCount.keySet());
                    Collections.sort(idsSorted);
                    int highestId = -1;
                    for (int i = idsSorted.size() - 1; i >= 0; i --) {
                        if(cardCount.get(idsSorted.get(i)) == 2) {
                            highestId = idsSorted.get(i);
                            break;
                        }
                    }
                    largestCards = getSpecificCards(CardTypes.MULTIPLES, 2, highestId);
                }
                break;
            case TRIPLE:
                if (containsCardType(CardTypes.TRIPLE_BULL)) {
                   largestCards = getSpecificCards(CardTypes.BULLS, 3, -1);
                }
                else if (!containsCardType(CardTypes.TRIPLE)) {
                    return null;
                }
                else {
                    List<Integer> idsSorted = new ArrayList<>(cardCount.keySet());
                    Collections.sort(idsSorted);
                    int highestId = -1;
                    for (int i = idsSorted.size() - 1; i > 0; i --) {
                        if(cardCount.get(idsSorted.get(i)) == 3) {
                            highestId = idsSorted.get(i);
                            break;
                        }
                    }
                    largestCards = getSpecificCards(CardTypes.MULTIPLES, 3, highestId);
                }
                break;
            case QUAD:
                if (containsCardType(CardTypes.QUAD)) {
                    getSpecificCards(CardTypes.MULTIPLES, 4, -1);
                }
                else if (!containsCardType(CardTypes.QUAD)) {
                    return null;
                }
                else {
                    List<Integer> idsSorted = new ArrayList<>(cardCount.keySet());
                    Collections.sort(idsSorted);
                    int highestId = -1;
                    for (int i = idsSorted.size() - 1; i > 0; i --) {
                        if(cardCount.get(idsSorted.get(i)) == 3) {
                            highestId = idsSorted.get(i);
                            break;
                        }
                    }
                    getSpecificCards(CardTypes.MULTIPLES, 4, highestId);
                }
                break;
            case PAIR_BULL:
                if (!containsCardType(CardTypes.PAIR_BULL)) {
                    return null;
                }
                else {
                    largestCards = getSpecificCards(CardTypes.BULLS, 2, -1);
                }
                break;
            case TRIPLE_BULL:
                if (!containsCardType(CardTypes.TRIPLE_BULL)) {
                    return null;
                }
                else {
                    largestCards = getSpecificCards(CardTypes.BULLS, 3, -1);
                }
                break;
            case QUAD_BULL:
                if (!containsCardType(CardTypes.QUAD_BULL)) {
                    return null;
                }
                else {
                    largestCards = getSpecificCards(CardTypes.BULLS, 4, -1);
                }
                break;
            case PAIR_OPERA:
                if (!containsCardType(CardTypes.PAIR_OPERA)) {
                    return null;
                }
                else {
                    largestCards = getSpecificCards(CardTypes.OPERAS, 2, -1);
                }
                break;
            case TRIPLE_OPERA:
                if (!containsCardType(CardTypes.TRIPLE_OPERA)) {
                    return null;
                }
                else {
                    largestCards = getSpecificCards(CardTypes.OPERAS, 3, -1);
                }
                break;
            case QUAD_OPERA:
                if (!containsCardType(CardTypes.QUAD_OPERA)) {
                    return null;
                }
                else {
                    largestCards = getSpecificCards(CardTypes.OPERAS, 4, -1);
                }
                break;
            case SINGLE_FISH:
                if(containsCardType(CardTypes.SINGLE_PENDULUM)) {
                    return getLargestCards(CardTypes.SINGLE_PENDULUM);
                }
                else if (!containsCardType(CardTypes.SINGLE_FISH)) {
                    return null;
                }
                else {
                    largestCards = getSpecificCards(CardTypes.FISHES, 1, -1);
                }
                break;
            case DOUBLE_FISH:
                if(containsCardType(CardTypes.DOUBLE_PENDULUM)) {
                    return getLargestCards(CardTypes.DOUBLE_PENDULUM);
                }
                else if (!containsCardType(CardTypes.DOUBLE_FISH)) {
                    return null;
                }
                else {
                    largestCards = getSpecificCards(CardTypes.FISHES, 2, -1);
                }
                break;
            case TRIPLE_FISH:
                if(containsCardType(CardTypes.TRIPLE_PENDULUM)) {
                    return getLargestCards(CardTypes.TRIPLE_PENDULUM);
                }
                else if (!containsCardType(CardTypes.TRIPLE_FISH)) {
                    return null;
                }
                else {
                    largestCards = getSpecificCards(CardTypes.FISHES, 3, -1);
                }
                break;
            case SINGLE_PENDULUM:
                if (!containsCardType(CardTypes.SINGLE_PENDULUM)) {
                    return null;
                }
                else {
                    largestCards = getSpecificCards(CardTypes.PENDULUMS, 1, -1);
                }
                break;
            case DOUBLE_PENDULUM:
                if (!containsCardType(CardTypes.DOUBLE_PENDULUM)) {
                    return null;
                }
                else {
                    largestCards = getSpecificCards(CardTypes.PENDULUMS, 2, -1);
                }
                break;
            case TRIPLE_PENDULUM:
                if (!containsCardType(CardTypes.TRIPLE_PENDULUM)) {
                    return null;
                }
                else {
                    largestCards = getSpecificCards(CardTypes.PENDULUMS, 3, -1);
                }
                break;
        }
        return largestCards;
    }

    /**
     * compares if this hand is bigger than the other depending on the type
     * @param type type of cards to compare
     * @param cardsToCompare the cards that this cards is being compared to
     * @return true if this hand is bigger; false otherwise
     */
    public boolean compareGreater(CardTypes type, ArrayList<Card> cardsToCompare) {
        switch (type) {
            case SINGLE:
                return cards.get(0).getValue() > cardsToCompare.get(0).getValue();
            case PAIR:
                if (getCardType() == CardTypes.PAIR_BULL && cardsToCompare.get(0).getTypeId() != Card.GOD) {
                    //Bull pair and god can't eat each other, but bull pairs eat everything else
                    return true;
                }
                else {
                    return cards.get(0).getValue() > cardsToCompare.get(0).getValue();
                }
            case TRIPLE:
                if (getCardType() == CardTypes.TRIPLE_BULL && cardsToCompare.get(0).getTypeId() != Card.GOD) {
                    //Bull triple and god can't eat each other, but bull pairs eat everything else
                    return true;
                }
                else {
                    return cards.get(0).getValue() > cardsToCompare.get(0).getValue();
                }
            case QUAD:
                if (getCardType() == CardTypes.TRIPLE_BULL && cardsToCompare.get(0).getTypeId() != Card.GOD) {
                    //Bull triple and god can't eat each other, but bull pairs eat everything else
                    return true;
                }
                else {
                    return cards.get(0).getValue() > cardsToCompare.get(0).getValue();
                }
            case PAIR_BULL:
                return false;
            case TRIPLE_BULL:
                return false;
            case QUAD_BULL:
                return false;
            case PAIR_OPERA:
                return false;
            case TRIPLE_OPERA:
                return false;
            case QUAD_OPERA:
                return false;
            case SINGLE_FISH:
                return getCardType() == CardTypes.SINGLE_PENDULUM;
            case DOUBLE_FISH:
                return getCardType() == CardTypes.DOUBLE_PENDULUM;
            case TRIPLE_FISH:
                return getCardType() == CardTypes.TRIPLE_PENDULUM;
            case SINGLE_PENDULUM:
                return false;
            case DOUBLE_PENDULUM:
                return false;
            case TRIPLE_PENDULUM:
                return false;
        }
        return false;
    }

    /**
     * checks if the card list contains a given card type
     * @param type type of cards
     * @return true if this card list contains that type; false otherwise
     */
    public boolean containsCardType(CardTypes type){
        switch (type) {
            case SINGLE:
                return !cards.isEmpty();
            case PAIR:
                return cardCount.containsValue(2);
            case TRIPLE:
                return cardCount.containsValue(3);
            case QUAD:
                return cardCount.containsValue(4);
            case PAIR_BULL:
                return cardCount.containsKey(Card.BIG_BULL) && cardCount.containsKey(Card.LITTLE_BULL);
            case TRIPLE_BULL:
                if (cardCount.containsKey(Card.BIG_BULL) && cardCount.containsKey(Card.LITTLE_BULL)) {
                    return cardCount.get(Card.BIG_BULL) == 2 || cardCount.get(Card.LITTLE_BULL) == 2;
                }
                else {
                    return false;
                }
            case QUAD_BULL:
                if (cardCount.containsKey(Card.BIG_BULL) && cardCount.containsKey(Card.LITTLE_BULL)) {
                    return cardCount.get(Card.BIG_BULL) == 2 && cardCount.get(Card.LITTLE_BULL) == 2;
                }
                else {
                    return false;
                }
            case PAIR_OPERA:
                return cardCount.containsKey(Card.OPERETTA) && cardCount.containsKey(Card.OPERA);
            case TRIPLE_OPERA:
                if (cardCount.containsKey(Card.OPERETTA) && cardCount.containsKey(Card.OPERA)) {
                    return cardCount.get(Card.OPERETTA) == 2 || cardCount.get(Card.OPERA) == 2;
                }
                else {
                    return false;
                }
            case QUAD_OPERA:
                if (cardCount.containsKey(Card.OPERETTA) && cardCount.containsKey(Card.OPERA)) {
                    return cardCount.get(Card.OPERETTA) == 2 && cardCount.get(Card.OPERA) == 2;
                }
                else {
                    return false;
                }
            case SINGLE_FISH:
                return cardCount.containsKey(Card.RED_EYES) && cardCount.containsKey(Card.BLACK_EYES) && cardCount.containsKey(Card.OBLIQUES);
            case DOUBLE_FISH:
                return cardCount.containsKey(Card.RED_EYES) && cardCount.containsKey(Card.BLACK_EYES) && cardCount.containsKey(Card.OBLIQUES)
                        && cardCount.get(Card.RED_EYES) >= 2 && cardCount.get(Card.BLACK_EYES) >= 2 && cardCount.get(Card.OBLIQUES) >= 2;
            case TRIPLE_FISH:
                return cardCount.containsKey(Card.RED_EYES) && cardCount.containsKey(Card.BLACK_EYES) && cardCount.containsKey(Card.OBLIQUES)
                        && cardCount.get(Card.RED_EYES) >= 3 && cardCount.get(Card.BLACK_EYES) >= 3 && cardCount.get(Card.OBLIQUES) >= 3;
            case SINGLE_PENDULUM:
                return cardCount.containsKey(Card.RED_EIGHTS) && cardCount.containsKey(Card.BLACK_TEN) && cardCount.containsKey(Card.GOD);
            case DOUBLE_PENDULUM:
                return cardCount.containsKey(Card.RED_EIGHTS) && cardCount.containsKey(Card.BLACK_TEN) && cardCount.containsKey(Card.GOD)
                        && cardCount.get(Card.RED_EIGHTS) >= 2 && cardCount.get(Card.BLACK_TEN) >= 2 && cardCount.get(Card.GOD) >= 2;
            case TRIPLE_PENDULUM:
                return cardCount.containsKey(Card.RED_EIGHTS) && cardCount.containsKey(Card.BLACK_TEN) && cardCount.containsKey(Card.GOD)
                        && cardCount.get(Card.RED_EIGHTS) >= 3 && cardCount.get(Card.BLACK_TEN) >= 3 && cardCount.get(Card.GOD) >= 3;
        }
        return false;
    }

}
