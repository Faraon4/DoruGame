import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;

public class GameApplication {
    private static HashMap<String, Session> sessions = new HashMap<>();
    private static Session activeSession = null;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to the Grid Adventure Game! Type 'help' for a list of commands.");
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            if (!handleCommand(input)) {
                break;
            }
        }
    }

    private static boolean handleCommand(String command) {
        String[] parts = command.split(" ");
        switch (parts[0].toLowerCase()) {
            case "help":
                printHelp();
                break;
            case "quit":
                return false;
            case "start":
                if (parts.length > 2 && parts[1].equals("session")) {
                    startSession(parts[2]);
                } else {
                    System.out.println("Invalid command. Use 'start session <sessionId>'.");
                }
                break;
            case "show":
                if (parts.length > 1 && parts[1].equals("session")) {
                    showSessions();
                } else {
                    showBoard();
                }
                break;
            case "switch":
                if (parts.length > 2 && parts[1].equals("session")) {
                    switchSession(parts[2]);
                } else {
                    System.out.println("Invalid command. Use 'switch session <sessionId>'.");
                }
                break;
            case "delete":
                if (parts.length > 2 && parts[1].equals("session")) {
                    deleteSession(parts[2]);
                } else {
                    System.out.println("Invalid command. Use 'delete session <sessionId>'.");
                }
                break;
            case "roll":
                if (activeSession != null && parts.length > 1 && parts[1].equals("dice")) {
                    int result = activeSession.getGame().rollDice();
                    System.out.println("Rolled a " + result);
                } else {
                    System.out.println("Invalid command. Use 'roll dice'.");
                }
                break;
            case "move":
                processMoveCommand(parts);
                break;
            case "moveobstacle":
                processMoveObstacle(parts);
                break;
            case "skip":
                if (activeSession != null && parts.length > 1 && parts[1].equals("turn")) {
                    activeSession.getGame().switchPlayerTurn();
                } else {
                    System.out.println("Invalid command. Use 'skip turn'.");
                }
                break;
            case "rematch":
                rematch();
                break;
            default:
                System.out.println("Unknown command. Type 'help' to see the list of available commands.");
                break;
        }
        return true;
    }

    private static void printHelp() {
        System.out.println("Available commands:");
        System.out.println("- help: Display this help message.");
        System.out.println("- quit: Exit the game.");
        System.out.println("- start session <sessionId>: Start a new game session.");
        System.out.println("- show session: List all active sessions.");
        System.out.println("- switch session <sessionId>: Switch to a different game session.");
        System.out.println("- delete session <sessionId>: Delete a session.");
        System.out.println("- show: Display the current game board.");
        System.out.println("- roll dice: Roll the dice.");
        System.out.println("- move <playerId> <figureId> <dx> <dy>: Move a player's figure.");
        System.out.println("- moveobstacle <x> <y> <dx> <dy>: Move an obstacle.");
        System.out.println("- skip turn: Skip the current player's turn.");
        System.out.println("- rematch: Restart the game with the same players and board.");
    }

    private static void startSession(String sessionId) {
        if (sessions.containsKey(sessionId)) {
            System.out.println("Session ID already exists.");
            return;
        }
        int numPlayers = 2; // Example player count, this can be adjusted or taken from user input
        Optional<Long> seed = Optional.empty(); // You can prompt for a seed if required
        Session session = new Session(sessionId, "src/map.txt", numPlayers, seed); // Example map file path
        sessions.put(sessionId, session);
        activeSession = session;
        System.out.println("Started new session with ID: " + sessionId);
    }

    private static void showSessions() {
        if (sessions.isEmpty()) {
            System.out.println("No active sessions.");
            return;
        }
        System.out.println("Active sessions:");
        sessions.forEach((id, session) -> System.out.println("Session ID: " + id));
    }

    private static void switchSession(String sessionId) {
        Session session = sessions.get(sessionId);
        if (session == null) {
            System.out.println("Session ID not found.");
        } else {
            activeSession = session;
            System.out.println("Switched to session: " + sessionId);
        }
    }

    private static void deleteSession(String sessionId) {
        if (sessions.remove(sessionId) != null) {
            System.out.println("Deleted session: " + sessionId);
            if (activeSession != null && activeSession.getSessionId().equals(sessionId)) {
                activeSession = null;
                System.out.println("No active session now.");
            }
        } else {
            System.out.println("Session ID not found.");
        }
    }

    private static void showBoard() {
        if (activeSession != null) {
            System.out.println("Game Board:");
            activeSession.getGame().displayBoard();
        } else {
            System.out.println("No active session to display.");
        }
    }

    private static void processMoveCommand(String[] parts) {
        if (activeSession != null && parts.length == 5) {
            try {
                char playerId = parts[1].charAt(0);
                int figureId = Integer.parseInt(parts[2]);
                int dx = Integer.parseInt(parts[3]);
                int dy = Integer.parseInt(parts[4]);
                Optional<Figure> figure = activeSession.getGame().getPlayerFigure(playerId, figureId);
                figure.ifPresentOrElse(
                        f -> activeSession.getGame().moveFigure(f, dx, dy),
                        () -> System.out.println("Figure not found.")
                );
            } catch (NumberFormatException e) {
                System.out.println("Invalid command. Use 'move <playerId> <figureId> <dx> <dy>'.");
            }
        } else {
            System.out.println("No active session or invalid 'move' command.");
        }
    }

    private static void processMoveObstacle(String[] parts) {
        if (activeSession != null && parts.length == 5) {
            try {
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                int dx = Integer.parseInt(parts[3]);
                int dy = Integer.parseInt(parts[4]);
                activeSession.getGame().moveObstacle(x, y, dx, dy);
            } catch (NumberFormatException e) {
                System.out.println("Invalid command. Use 'moveobstacle <x> <y> <dx> <dy>'.");
            }
        } else {
            System.out.println("No active session or invalid 'moveobstacle' command.");
        }
    }

    private static void rematch() {
        if (activeSession != null) {
            String sessionId = activeSession.getSessionId();
            String mapFile = activeSession.getMapFile();
            int numPlayers = activeSession.getGame().getPlayers().size();
            Optional<Long> seed = activeSession.getSeed();
            Session newSession = new Session(sessionId, mapFile, numPlayers, seed);
            sessions.put(sessionId, newSession);
            activeSession = newSession;
            System.out.println("Rematch setup for session with ID: " + sessionId);
        } else {
            System.out.println("No active session to rematch.");
        }
    }
}

