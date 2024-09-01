public class Figure {
    private char playerId;
    private int figureId;
    private int x, y;
    private Player player;

    public Figure(char playerId, int figureId, Player player, int startX, int startY) {
        this.playerId = playerId;
        this.figureId = figureId;
        this.player = player;
        this.x = startX;
        this.y = startY;
    }

    public char getPlayerId() {
        return playerId;
    }

    public int getFigureId() {
        return figureId;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Player getPlayer() {
        return player;
    }

    public void move(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }
}
