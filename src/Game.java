import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private char[][] board;
    private List<Player> players;
    private int currentPlayerIndex;
    private Player winner;
    private Random random;
    private boolean isOver;

    public Game(String mapFile, int numPlayers) {
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.random = new Random();
        this.isOver = false;

        try {
            initializeBoardFromFile(mapFile);
        } catch (FileNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }
        initializePlayers(numPlayers);
    }

    private void initializeBoardFromFile(String mapFile) throws FileNotFoundException {
        List<String> lines = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(mapFile))) {
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
        }

        int rows = lines.size();
        int cols = lines.get(0).length();
        this.board = new char[rows][cols];

        for (int i = 0; i < rows; i++) {
            String line = lines.get(i);
            for (int j = 0; j < line.length(); j++) {
                board[i][j] = line.charAt(j);
            }
        }
    }

    private void initializePlayers(int numPlayers) {
        int figureId = 1;
        for (char playerId = 'a'; playerId < 'a' + numPlayers; playerId++) {
            Player player = new Player(playerId);
            players.add(player);
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    if (board[i][j] == playerId) {
                        Figure figure = new Figure(playerId, figureId++, player, i, j);
                        player.addFigure(figure);
                    }
                }
            }
        }
    }

    public void displayBoard() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void setRandomSeed(long seed) {
        this.random.setSeed(seed);
    }

    public int rollDice() {
        return random.nextInt(6) + 1;
    }

    public Optional<Figure> getPlayerFigure(char playerId, int figureId) {
        return players.stream()
                .filter(p -> p.getPlayerId() == playerId)
                .flatMap(p -> p.getFigures().stream())
                .filter(f -> f.getFigureId() == figureId)
                .findFirst();
    }

    public void moveFigure(Figure figure, int dx, int dy) {
        if (isOver) {
            System.out.println("The game is over. No more moves can be made.");
            return;
        }

        int newX = figure.getX() + dx;
        int newY = figure.getY() + dy;

        if (isValidMove(newX, newY)) {
            board[figure.getX()][figure.getY()] = 'P'; // Clear old position
            figure.move(dx, dy);
            if (board[newX][newY] == 'T') {
                announceWinner(figure.getPlayer());
            } else {
                board[newX][newY] = figure.getPlayerId(); // Place figure at new position
                switchPlayerTurn();
            }
        } else {
            System.out.println("Move not possible!");
        }
    }

    public void moveObstacle(int x, int y, int dx, int dy) {
        if (board[x][y] == 'O') {
            int newX = x + dx;
            int newY = y + dy;
            if (isValidMove(newX, newY) && board[newX][newY] == 'P') {
                board[x][y] = 'P'; // Clear the old position
                board[newX][newY] = 'O'; // Place obstacle at new position
            } else {
                System.out.println("Obstacle move not possible!");
            }
        }
    }

    void switchPlayerTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        System.out.println("Player " + players.get(currentPlayerIndex).getPlayerId() + "'s turn!");
    }

    private boolean isValidMove(int x, int y) {
        // Check if the move is within the board and the destination is not an obstacle
        return x >= 0 && x < board.length && y >= 0 && y < board[0].length && board[x][y] != 'O';
    }

    private void announceWinner(Player player) {
        System.out.println("Player " + player.getPlayerId() + " wins!");
        isOver = true;
        winner = player;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public Player getWinner() {
        return winner;
    }

    public boolean isGameOver() {
        return isOver;
    }

    public List<Player> getPlayers() {
        return players;
    }
}

