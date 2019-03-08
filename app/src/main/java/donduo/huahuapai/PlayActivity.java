package donduo.huahuapai;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class PlayActivity extends AppCompatActivity {
    private Deck deck;
    private Player[] Players;
    private CardList selectedCards;
    private CardList currentlyPlayedCards;
    private int numberOfPlayingFieldCardsRemaining;
    private CardList allPlayedCards;
    public static final int PLAYING_FIELD_LIMIT = 16;
    public static final int NUMBER_OF_CARDS_TO_WIN = 6;
    public static final int NUMBER_OF_PLAYERS = 3;

    private enum GameStates {PLAY, EAT_PLAYED, LIFT, EAT_LIFTED, END_PLAY}

    private GameStates gameState;
    private int currentPlayerIndex;
    private int eldestPlayerIndex; //eldest - first player to play in the round.
    private int lifterIndex;

    private void startGame() {
        deck = new Deck();
        Players = new Player[3];
        selectedCards = new CardList();
        currentlyPlayedCards = new CardList();
        numberOfPlayingFieldCardsRemaining = PLAYING_FIELD_LIMIT;
        allPlayedCards = new CardList();
        currentPlayerIndex = 0;
        eldestPlayerIndex = 0;
        lifterIndex = -1;
        Players[0] = new Player("Player 1", false, 0);
        Players[1] = new Player("Player 2", true, 1);
        Players[2] = new Player("Player 3", true, 2);
        startRound();
    }

    /**
     * starts each round
     */
    private void startRound() {
        shuffleDeck();
        dealCards();
        setGameState(GameStates.PLAY);
        initiateUI();
        startTurn();
    }

    /**
     * initiates all the Android UI for the player
     */
    private void initiateUI() {
        final Button playButton = findViewById(R.id.play);
        final ArrayList<Button> buttons = new ArrayList<>();
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClicked(v, buttons);
            }
        });
        final Button endButton = findViewById(R.id.end_turn);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTurnClicked(v, buttons);
            }
        });
        final Button liftButton = findViewById(R.id.lift);
        liftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liftClicked(v);
            }
        });
        ConstraintLayout layout = findViewById(R.id.play_activity_layout);
        enableDisableButtons("011");

        int[] buttonIDs = new int[Players[currentPlayerIndex].getHand().getCards().size()];
        int j = 0;
        for (final Card card : Players[currentPlayerIndex].getHand().getCards()) {
            ConstraintSet set = new ConstraintSet();
            Button button = new Button(this);
            button.setText(card.getName());
            button.setTag(Color.GRAY);
            button.setId(View.generateViewId());
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    cardSelected(v, card, buttons);
                }
            });
            buttons.add(button);
            buttonIDs[j] = button.getId();
            layout.addView(button);
            set.clone(layout);
            if (j == 0) {
                set.connect(buttonIDs[j], ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                set.connect(buttonIDs[j], ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
            } else if (j == Players[currentPlayerIndex].getHand().getCards().size() / 2) {
                set.connect(buttonIDs[j], ConstraintSet.LEFT, buttonIDs[j - Players[currentPlayerIndex].getHand().getCards().size() / 2], ConstraintSet.RIGHT);
                set.connect(buttonIDs[j], ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            } else if (j > Players[currentPlayerIndex].getHand().getCards().size() / 2) {
                set.connect(buttonIDs[j], ConstraintSet.LEFT, buttonIDs[j - Players[currentPlayerIndex].getHand().getCards().size() / 2], ConstraintSet.RIGHT);
                set.connect(buttonIDs[j], ConstraintSet.TOP, buttonIDs[j - 1], ConstraintSet.BOTTOM);
            } else {
                set.connect(buttonIDs[j], ConstraintSet.TOP, buttonIDs[j - 1], ConstraintSet.BOTTOM);
                set.connect(buttonIDs[j], ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
            }
            set.applyTo(layout);
            j++;
        }

    }

    /**
     * the start of each player's turn. Will execute AI logic for computer players; wait for a button press for real players
     */
    private void startTurn() {
        if (Players[currentPlayerIndex].isComputer()) {
            enableDisableButtons("000");
            playComputerMove();
        }
    }

    /**
     * shuffles the deck
     */
    private void shuffleDeck() {
        int i;
        for (i = 0; i < 5; i++) {
            deck.shuffle();
        }
        deck.cut();
    }

    /**
     * deals cards to every player one card at a time
     */
    private void dealCards() {
        while (deck.getSize() > 0) {
            Players[currentPlayerIndex].addCard(deck.draw());
            nextPlayer();
        }
        for (Card card: Players[1].getHand().getCards()
             ) {
            System.out.println(card.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        startGame();

    }

    /**
     * When a human player clicks the play button
     * @param view the play button
     * @param buttons all player cards
     */
    public void playClicked(View view, ArrayList<Button> buttons) {
        clearDisplay();
        for (Button button : buttons
        ) {
            if ((Integer) button.getTag() == Color.RED) {
                button.setVisibility(View.GONE);
            }
        }
        playCard();

    }

    /**
     * When a human player clicks the end turn button
     * @param view the end turn button
     * @param buttons all player cards
     */
    public void endTurnClicked(View view, ArrayList<Button> buttons) {
        clearDisplay();
        for (Button button : buttons
        ) {
            button.getBackground().clearColorFilter();
        }
        endTurn();
    }

    /**
     * When a human player clicks the lift button
     * @param view the lift button
     */
    public void liftClicked(View view) {
        clearDisplay();
        lift();
    }

    /**
     * This method executes what happens when cards are played depending on the state.
     */
    private void playCard() {
        displayPlayedCards();
        Players[currentPlayerIndex].getHand().removeCards(selectedCards);
        switch (gameState) {
            case PLAY:
                currentlyPlayedCards.addCards(selectedCards.getCards());
                selectedCards.removeAllCards();
                setGameState(GameStates.EAT_PLAYED);
                nextPlayer();
                startTurn();
                break;
            case EAT_PLAYED:
                currentlyPlayedCards.removeAllCards();
                if (currentPlayerIndex == (eldestPlayerIndex + NUMBER_OF_PLAYERS - 1) % NUMBER_OF_PLAYERS ) {
                    Players[currentPlayerIndex].increaseNumberOfCardsCountedTowardsLimit(selectedCards.getCards().size());
                    numberOfPlayingFieldCardsRemaining -= selectedCards.getCards().size();
                    eldestPlayerIndex = currentPlayerIndex;
                    setGameState(GameStates.PLAY);
                }
                else {
                    currentlyPlayedCards.addCards(selectedCards.getCards());
                    nextPlayer();
                }
                allPlayedCards.addCards(selectedCards.getCards());
                selectedCards.removeAllCards();
                startTurn();
                break;
            case LIFT:
                currentlyPlayedCards.addCards(selectedCards.getCards());
                selectedCards.removeAllCards();
                setGameState(GameStates.EAT_LIFTED);
                nextPlayer();
                enableDisableButtons("011");
                startTurn();
                break;
            case EAT_LIFTED:
                currentlyPlayedCards.removeAllCards();
                if (currentPlayerIndex == (eldestPlayerIndex + NUMBER_OF_PLAYERS - 1) % NUMBER_OF_PLAYERS ) {
                    Players[currentPlayerIndex].increaseNumberOfCardsCountedTowardsLimit(selectedCards.getCards().size());
                    numberOfPlayingFieldCardsRemaining -= selectedCards.getCards().size();
                    eldestPlayerIndex = currentPlayerIndex;
                    setGameState(GameStates.PLAY);
                }
                else {
                    currentlyPlayedCards.addCards(selectedCards.getCards());
                    nextPlayer();
                    enableDisableButtons("011");
                }
                allPlayedCards.addCards(selectedCards.getCards());
                selectedCards.removeAllCards();
                startTurn();
                break;
        }
    }

    /**
     * This method executes what happens when a turn is ended without playing any cards depending
     * on the state.
     */
    private void endTurn() {
        displayPassed();
        switch (gameState) {
            case PLAY:
                selectedCards.removeAllCards();
                setGameState(GameStates.END_PLAY);
                nextPlayer();
                enableDisableButtons("101");
                startTurn();
                break;
            case EAT_PLAYED:
                if (currentPlayerIndex == (eldestPlayerIndex + NUMBER_OF_PLAYERS - 1) % NUMBER_OF_PLAYERS) {
                    setGameState(GameStates.PLAY);
                    Players[currentlyPlayedCards.getCards().get(0).getBelongsToPlayerId()].increaseNumberOfCardsCountedTowardsLimit(currentlyPlayedCards.getCards().size());
                    numberOfPlayingFieldCardsRemaining -= currentlyPlayedCards.getCards().size();
                    eldestPlayerIndex = currentlyPlayedCards.getCards().get(0).getBelongsToPlayerId();
                    currentPlayerIndex = eldestPlayerIndex;
                    currentlyPlayedCards.removeAllCards();
                }
                else {
                    nextPlayer();
                }
                selectedCards.removeAllCards();
                enableDisableButtons("011");
                startTurn();
                break;
            case LIFT:
                //Cannot end turn when in this state. Card(s) must be played.
                System.out.println("CANNOT HAPPEN. BUG.");
                break;
            case EAT_LIFTED:
                if (currentPlayerIndex == lifterIndex && eldestPlayerIndex == (lifterIndex + NUMBER_OF_PLAYERS - 1) % NUMBER_OF_PLAYERS) {
                    // lifter is the 2nd player after elder; 3rd player can still eat
                    nextPlayer();
                }
                else if (currentPlayerIndex == lifterIndex && lifterIndex == (eldestPlayerIndex + NUMBER_OF_PLAYERS - 1) % NUMBER_OF_PLAYERS) {
                    // lifter is the 3rd player. Try again. Doesn't matter if 2nd player ate or not,
                    // because 2nd player already folded for the lifter to be the 3rd player
                    eldestPlayerIndex = currentlyPlayedCards.getCards().get(0).getBelongsToPlayerId();
                    setGameState(GameStates.LIFT);
                    enableDisableButtons("101");
                    currentlyPlayedCards.removeAllCards();
                }
                else if (currentPlayerIndex != lifterIndex && lifterIndex == (eldestPlayerIndex + NUMBER_OF_PLAYERS - 1) % NUMBER_OF_PLAYERS ) {
                    // lifter is 3rd player but 2nd player just ended turn
                    nextPlayer();
                    setGameState(GameStates.EAT_LIFTED);
                    enableDisableButtons("011");
                }
                else if (currentPlayerIndex != lifterIndex && eldestPlayerIndex == (lifterIndex + NUMBER_OF_PLAYERS - 1) % NUMBER_OF_PLAYERS ) {
                    // lifter is 2nd player but third player just ended turn
                    if (currentlyPlayedCards.getCards().get(0).getBelongsToPlayerId() == eldestPlayerIndex) {
                        setGameState(GameStates.END_PLAY);
                        enableDisableButtons("101");
                    }
                    else if (currentlyPlayedCards.getCards().get(0).getBelongsToPlayerId() == lifterIndex){
                        setGameState(GameStates.PLAY);
                        enableDisableButtons("011");
                        eldestPlayerIndex = lifterIndex;
                    }
                    nextPlayer();
                    nextPlayer();
                    currentlyPlayedCards.removeAllCards();
                }
                selectedCards.removeAllCards();
                startTurn();
                break;
            case END_PLAY:
                if (currentPlayerIndex == (eldestPlayerIndex + NUMBER_OF_PLAYERS - 1) % NUMBER_OF_PLAYERS) {
                    endRound();
                }
                else {
                    nextPlayer();
                    enableDisableButtons("101");
                    startTurn();
                }
                break;
        }
    }

    /**
     * Enables and disables buttons.
     * @param buttonsVector 1 - end turn button enabled
     *                      2 - play button enabled
     *                      4 - lift button enabled
     */
    private void enableDisableButtons(String buttonsVector) {
        if (Players[currentPlayerIndex].isComputer()) {
            return;
        }
        char[] buttonsVectorCharArray = buttonsVector.toCharArray();
        Button endTurnButton = findViewById(R.id.end_turn);
        Button playButton = findViewById(R.id.play);
        Button liftButton = findViewById(R.id.lift);
        if (buttonsVectorCharArray[2] == '1') {
            endTurnButton.setEnabled(true);
        }
        else if (buttonsVectorCharArray[2] == '0'){
            endTurnButton.setEnabled(false);
        }
        if (buttonsVectorCharArray[1] == '1') {
            playButton.setEnabled(true);
        }
        else if (buttonsVectorCharArray[1] == '0'){
            playButton.setEnabled(false);
        }
        if (buttonsVectorCharArray[0] == '1') {
            liftButton.setEnabled(true);
        }
        else if (buttonsVectorCharArray[0] == '0'){
            liftButton.setEnabled(false);
        }
    }

    /**
     * This method executes what happens during a lift.
     */
    private void lift() {
        lifterIndex = currentPlayerIndex;
        setGameState(GameStates.LIFT);
        displayLifted();
        currentPlayerIndex = eldestPlayerIndex;
        enableDisableButtons("010");
        startTurn();
    }

    /**
     * ends the round.
     */
    private void endRound() {
        String winningString = "The winners are: ";
        for (int i=0; i<2; i++) {
            if (Players[i].getNumberOfCardsCountedTowardsLimit() >= NUMBER_OF_CARDS_TO_WIN) {
                winningString += Players[i].getName() + ", ";
            }
        }
        TextView textView = findViewById(R.id.textView);
        textView.setText(winningString);
    }

    /**
     * plays a move for the computer depending on the state
     */
    private void playComputerMove() {
        System.out.println(gameState);
        CardList hand = Players[currentPlayerIndex].getHand();
        switch(gameState) {
            case PLAY:
                if (Players[currentPlayerIndex].getNumberOfCardsCountedTowardsLimit() >= NUMBER_OF_CARDS_TO_WIN) {
                    endTurn();
                }
                else if (Players[currentPlayerIndex].getNumberOfCardsCountedTowardsLimit() + numberOfPlayingFieldCardsRemaining >= NUMBER_OF_CARDS_TO_WIN) {
                    int numberOfCardsNeededToPlay = NUMBER_OF_CARDS_TO_WIN - Players[currentPlayerIndex].getNumberOfCardsCountedTowardsLimit();
                    int godCount = hand.getCardCount().containsKey(Card.GOD) ? hand.getCardCount().get(Card.GOD): 0;
                    int pendulumCount = hand.containsCardType(CardList.CardTypes.DOUBLE_PENDULUM) ? 2: (hand.containsCardType(CardList.CardTypes.SINGLE_PENDULUM) ? 1 : 0);
                    int bullCount = hand.containsCardType(CardList.CardTypes.QUAD_BULL) ? 4 :
                            (hand.containsCardType(CardList.CardTypes.TRIPLE_BULL) ? 3 :
                                    (hand.containsCardType(CardList.CardTypes.PAIR_BULL) ? 2: 0));
                    int operaCount = hand.containsCardType(CardList.CardTypes.QUAD_OPERA) ? 4 :
                            (hand.containsCardType(CardList.CardTypes.TRIPLE_OPERA) ? 3 :
                                    (hand.containsCardType(CardList.CardTypes.PAIR_OPERA) ? 2: 0));
                    if ((godCount - pendulumCount) + pendulumCount * 3 + bullCount + operaCount >= numberOfCardsNeededToPlay) {
                        if (operaCount > 0) {
                            selectedCards.addCards(hand.getSpecificCards(CardList.CardTypes.OPERAS, operaCount, -1));
                        }
                        else if (bullCount > 0) {
                            selectedCards.addCards(hand.getSpecificCards(CardList.CardTypes.BULLS, bullCount, -1));
                        }
                        else if (pendulumCount > 0) {
                            selectedCards.addCards(hand.getSpecificCards(CardList.CardTypes.PENDULUMS, pendulumCount, -1));
                        }
                        else if (godCount - pendulumCount > 0) {
                            selectedCards.addCards(hand.getSpecificCards(CardList.CardTypes.MULTIPLES, godCount - pendulumCount, Card.GOD));
                        }
                        playCard();
                    }
                    else {
                        endTurn();
                    }
                }
                else {
                    endTurn();
                }
                break;
            case LIFT:
                selectedCards.addCards(Players[currentPlayerIndex].getHand().getCards().get(0));
                playCard();
                break;
            case EAT_LIFTED:
                if (Players[currentPlayerIndex].getHand().getLargestCards(currentlyPlayedCards.getCardType()) == null) {
                    endTurn();
                }
                else {
                    selectedCards.addCards(Players[currentPlayerIndex].getHand().getLargestCards(currentlyPlayedCards.getCardType()));
                    if (selectedCards.compareGreater(currentlyPlayedCards.getCardType(), currentlyPlayedCards.getCards())) {
                        playCard();
                    } else {
                        endTurn();
                    }
                }
                break;
            case EAT_PLAYED:
                if (Players[currentPlayerIndex].getHand().getLargestCards(currentlyPlayedCards.getCardType()) == null) {
                    endTurn();
                }
                else {
                    selectedCards.addCards(Players[currentPlayerIndex].getHand().getLargestCards(currentlyPlayedCards.getCardType()));
                    if (selectedCards.compareGreater(currentlyPlayedCards.getCardType(), currentlyPlayedCards.getCards())) {
                        playCard();
                    } else {
                        endTurn();
                    }
                }
                break;
            case END_PLAY:
                endTurn();
                break;
        }
    }

    /**
     * checks if the cards selected by the player is a legal move
     * @return true if move is legal
     */
    private boolean checkLegalMove() {
        //TODO: Verification of legal moves that the player selects. Assume the player always picks correct cards for now.
        return true;
    }

    /**
     * when a player selects a card
     * @param view the view of the card
     * @param card the card selected
     * @param buttons ArrayList of buttons to update
     */
    public void cardSelected(View view, Card card, ArrayList<Button> buttons) {
        if (selectedCards.contains(card)) { //card selected; deselect
            selectedCards.removeCards(card);
            view.getBackground().clearColorFilter();
        } else if (checkCardTypes(card)) {
            selectedCards.addCards(card);
            view.setTag(Color.RED);
            view.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        } else {
            selectedCards.removeAllCards();
            selectedCards.addCards(card);
            for (Button button : buttons
            ) {
                button.getBackground().clearColorFilter();
                button.setTag(Color.GRAY);
            }
            view.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
            view.setTag(Color.RED);
        }
    }

    /**
     * checks if the newly selected card and the already selected cards are part of a card group
     * @param currentCard the newly selected card
     * @return true if all selected cards belong in a group; false otherwise
     */
    private boolean checkCardTypes(Card currentCard) {
        CardList tempCardList = new CardList();
        tempCardList.addCards(selectedCards.getCards());
        tempCardList.addCards(currentCard);
        if( tempCardList.getCardType() != CardList.CardTypes.NONE) {
            return true;
        }
        else { //check if the player wants to play fish or pendulum
            return tempCardList.getCardCount().keySet().size() == 2 && (
                    tempCardList.getCardCount().containsKey(Card.RED_EYES) && tempCardList.getCardCount().containsKey(Card.BLACK_EYES) ||
                            tempCardList.getCardCount().containsKey(Card.OBLIQUES) && tempCardList.getCardCount().containsKey(Card.BLACK_EYES) ||
                            tempCardList.getCardCount().containsKey(Card.RED_EYES) && tempCardList.getCardCount().containsKey(Card.OBLIQUES) ||
                            tempCardList.getCardCount().containsKey(Card.RED_EIGHTS) && tempCardList.getCardCount().containsKey(Card.BLACK_TEN) ||
                            tempCardList.getCardCount().containsKey(Card.RED_EIGHTS) && tempCardList.getCardCount().containsKey(Card.GOD) ||
                            tempCardList.getCardCount().containsKey(Card.BLACK_TEN) && tempCardList.getCardCount().containsKey(Card.GOD)) ||
                    tempCardList.getCardCount().keySet().size() == 3 && (tempCardList.getCardCount().containsKey(Card.RED_EYES) &&
                            tempCardList.getCardCount().containsKey(Card.BLACK_EYES) && tempCardList.getCardCount().containsKey(Card.OBLIQUES) ||
                            tempCardList.getCardCount().containsKey(Card.RED_EIGHTS) &&
                                    tempCardList.getCardCount().containsKey(Card.BLACK_TEN) && tempCardList.getCardCount().containsKey(Card.GOD))
            ;
        }

    }

    private void setGameState(GameStates gameState) {
        this.gameState = gameState;
    }

    /**
     * increases the player index to go to the next player
     */
    private void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % NUMBER_OF_PLAYERS;
    }

    private void displayPlayedCards() {
        TextView textView = findViewById(R.id.textView);
        String cardString = "";
        for (Card card: selectedCards.getCards()
        ) {
            cardString += card.getName() + ", ";
        }
        textView.setText(textView.getText() + Players[currentPlayerIndex].getName() + " has played " + cardString);
    }

    private void clearDisplay() {
        TextView textView = findViewById(R.id.textView);
        textView.setText("");
    }

    private void displayPassed() {
        TextView textView = findViewById(R.id.textView);
        textView.setText(textView.getText() + Players[currentPlayerIndex].getName() + " has passed, ");
    }

    private void displayLifted() {
        TextView textView = findViewById(R.id.textView);
        textView.setText(textView.getText() + Players[currentPlayerIndex].getName() + " has lifted, ");
    }

}
