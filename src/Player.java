import java.util.ArrayList;
import java.util.List;

public class Player {
    private char playerId;
    private List<Figure> figures;

    public Player(char playerId) {
        this.playerId = playerId;
        this.figures = new ArrayList<>();
    }

    public char getPlayerId() {
        return playerId;
    }

    public void addFigure(Figure figure) {
        figures.add(figure);
    }

    public void removeFigure(Figure figure) {
        figures.remove(figure);
    }

    public List<Figure> getFigures() {
        return figures;
    }
}
