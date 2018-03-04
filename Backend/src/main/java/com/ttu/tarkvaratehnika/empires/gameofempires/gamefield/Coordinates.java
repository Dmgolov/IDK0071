package com.ttu.tarkvaratehnika.empires.gameofempires.gamefield;

public class Coordinates {

    private int x, y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int hashCode() {
        int result = 31;
        result = 31 * result + x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof Coordinates
                && this.x == ((Coordinates) obj).getX()
                && this.y == ((Coordinates) obj).getY()
                && this.hashCode() == obj.hashCode();
    }

    @Override
    public String toString() {
        return String.format("[%d, %d]", x, y);
    }
}
