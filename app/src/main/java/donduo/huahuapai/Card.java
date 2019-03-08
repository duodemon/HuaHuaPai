package donduo.huahuapai;

import java.util.Objects;

public class Card {
    public enum Color {RED,BLACK,MIXED}
    public static final int RED_EYES = 0;
    public static final int OPERETTA = 1;
    public static final int BLACK_EYES = 2;
    public static final int OBLIQUES = 3;
    public static final int OPERA = 4;
    public static final int SIXES = 5;
    public static final int SEVENS = 6;
    public static final int RED_EIGHTS = 7;
    public static final int LITTLE_BULL = 8;
    public static final int BIG_BULL = 9;
    public static final int BLACK_TEN = 10;
    public static final int RED_TEN = 11;
    public static final int TIGER = 12;
    public static final int GOD = 13;

    private String name;
    private int value;
    private Color color;
    private int typeId; // the type of card
    private int uniqueId; // which copy of the card
    private int belongsToPlayerId; //keep track of who played the card

    Card (String name, int value, Color color, int typeId, int uniqueId) {
        this.name = name;
        this.value = value;
        this.color = color;
        this.typeId = typeId;
        this.uniqueId = uniqueId;
    }

    public String getColor () {
        return color.toString();
    }

    @Override
    public String toString() {
        return "Card{" +
                "name='" + name + '\'' +
                ", value=" + value +
                ", color=" + color +
                ", typeId=" + typeId +
                ", uniqueId=" + uniqueId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return value == card.value &&
                typeId == card.typeId &&
                uniqueId == card.uniqueId &&
                Objects.equals(name, card.name) &&
                color == card.color;
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, value, color, typeId, uniqueId);
    }

    public int getUniqueId() {

        return uniqueId;
    }

    public int getValue () {
        return value;
    }

    public int getTypeId () {
        return typeId;
    }

    public String getName () {
        return name;
    }

    public void setBelongsToPlayerId (int playerId) {
        belongsToPlayerId = playerId;
    }

    public int getBelongsToPlayerId () {
        return belongsToPlayerId;
    }
}
