import java.util.Optional;

public class Session {
    private final String sessionId;
    private final Game game;
    private final Optional<Long> seed;
    private final String mapFile;

    public Session(String sessionId, String mapFile, int numPlayers, Optional<Long> seed) {
        this.sessionId = sessionId;
        this.mapFile = mapFile;
        this.seed = seed;

        // Initialize the game with the given map file and player number
        this.game = new Game(mapFile, numPlayers);

        // If a seed is provided, set it for random operations
        seed.ifPresent(this.game::setRandomSeed);
    }

    public String getSessionId() {
        return sessionId;
    }

    public Game getGame() {
        return game;
    }

    public String getMapFile() {
        return mapFile;
    }

    public Optional<Long> getSeed() {
        return seed;
    }
}
