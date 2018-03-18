package ru.ifmo.services.game.checkers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import ru.ifmo.services.game.GameException;
import ru.ifmo.services.game.GameUpdate;
import ru.ifmo.telegram.bot.entity.Player;
import ru.ifmo.telegram.bot.services.game.Game;
import ru.ifmo.telegram.bot.services.main.Games;
import ru.ifmo.telegram.bot.services.telegramApi.TgException;
import ru.ifmo.telegram.bot.services.telegramApi.classes.Keyboard;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CheckersGame<S extends CheckersStep> implements Game<S> {
    private boolean isFirstTurn;
    private int currPlayer;
    private int fromX, fromY;
    private CheckersBoard board;
    private Player player1, player2, winner;

    CheckersGame(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.currPlayer = 1;
        this.board = new CheckersBoard();
        isFirstTurn = true;
    }

    CheckersGame(String jsonString, Player player1, Player player2) throws GameException {
        JsonParser parser = new JsonParser();
        JsonObject gameJson = parser.parse(jsonString).getAsJsonObject();
        board = new CheckersBoard(gameJson.get("board").getAsJsonObject());
        if (gameJson.get("player1").getAsLong() == player1.getId() && gameJson.get("player2").getAsLong() == player2.getId()) {
            this.player1 = player1;
            this.player2 = player2;
        } else {
            if (gameJson.get("player2").getAsLong() == player1.getId() && gameJson.get("player1").getAsLong() == player2.getId()) {
                this.player2 = player1;
                this.player1 = player2;
            } else {
                throw new GameException("Wrong players for deserialization");
            }
        }
        currPlayer = gameJson.get("currPlayer").getAsLong() == player1.getId() ? 1 : -1;
        isFirstTurn = gameJson.get("isFirstTurn").getAsBoolean();
        fromX = gameJson.get("fromX").getAsInt();
        fromY = gameJson.get("fromY").getAsInt();
    }

    private boolean checkWinner() {
        return board.blackCount == 0 || board.whiteCount == 0;
    }

    @Override
    @NotNull
    public Pair<String, Boolean> step(@NotNull S step) {
        return step1(step);
    }

    private Pair<String, Boolean> step1(@NotNull S step) {
        if (currPlayer == 0) {
            return new Pair<>("Game is won by " + winner.getName(), true);
        }
        if (isFirstTurn) {
            isFirstTurn = false;
            fromX = step.x;
            fromY = step.y;
            return new Pair<>("Continue your turn!", false);
        }
        isFirstTurn = true;
        Player player = (currPlayer == 1) ? player1 : player2;
        if (player.equals(step.player)) {
            if (board.makeTurn(fromX - 1, fromY - 1, step.x - 1, step.y - 1, currPlayer)) {
                if (checkWinner()) {
                    winner = player;
                    currPlayer = 0;
                    return new Pair<>(winner.getName() + " won", true);
                } else {
                    currPlayer *= -1;
                    return new Pair<>("Turn was made", true);
                }
            } else {
                return new Pair<>("Wrong turn", false);
            }
        } else {
            return new Pair<>("Wrong player tried to make turn", false);
        }
    }

    @NotNull
    private String getMessage(@NotNull Player player) {
        if (player.equals(player1) || player.equals(player2)) {
            StringBuilder sb = new StringBuilder();
            if (currPlayer != 0) {
                sb.append("Current player: ");
                sb.append(currPlayer == 1
                        ? Objects.requireNonNull(player1.getName()).replace('_', ' ') + " (white)"
                        : Objects.requireNonNull(player2.getName()).replace('_', ' ') + " (black)");
            } else {
                if (winner != null) {
                    sb.append("Winner: ");
                    sb.append(winner.getName());
                } else {
                    sb.append("Draw");
                }
            }
            sb.append('\n');
//            if (player.equals(player1)) {
//                sb.append(board.toString());
//            } else {
//                sb.append(board.toString());
//            }
            return sb.toString();
        } else {
            return "Wrong player";
        }
    }

    @NotNull
    @Override
    public String toJson() {
        JsonObject object = new JsonObject();
        object.add("board", board.toJson());
        object.addProperty("player1", player1.getId());
        object.addProperty("player2", player2.getId());
        object.addProperty("currPlayer", currPlayer);
        object.addProperty("isFirstTurn", isFirstTurn);
        object.addProperty("fromX", fromX);
        object.addProperty("fromY", fromY);
        return object.toString();
    }

    @Override
    public void surrender(@NotNull Player player) {
        if (player.equals(player1)) {
            winner = player2;
            currPlayer = 0;
        } else if (player.equals(player2)) {
            winner = player1;
            currPlayer = 0;
        }
    }

    @NotNull
    @Override
    public List<Player> getPlayers() {
        return Arrays.asList(player1, player2);
    }

    @NotNull
    @Override
    public Games getGameId() {
        return Games.CHECKERS;
    }

    @Override
    public boolean isFinished() {
        return currPlayer == 0;
    }

    @NotNull
    private Keyboard getKeyboard(@NotNull Player player) {
        if (currPlayer != 0 && currPlayer == 1 && player.equals(player1))
            return board.getKeyboard(player.equals(player1));
        if (currPlayer != 0 && currPlayer != 1 && !player.equals(player1))
            return board.getKeyboard(player.equals(player1));
        return new Keyboard();
    }

    @NotNull
    @Override
    public GameUpdate getGameUpdate(@NotNull Player player) throws TgException {
        return new GameUpdate(getMessage(player), getKeyboard(player), drawPicture(player));
    }

    @Override
    public boolean isCurrent(@NotNull Player player) {
        return (player1.equals(player) && currPlayer > 0) || (player2.equals(player) && currPlayer < 0);
    }

    @NotNull
    public byte[] drawPicture(@NotNull Player player) throws TgException {
        //String picture = player == player1 ? board.toString() : board.toReverseString();
        String picture = board.toString();
        Image whiteImage;
        Image blackImage;
        Image whiteDImage;
        Image blackDImage;
        Image fieldImage;
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            whiteImage = ImageIO.read(new File(classLoader.getResource("checkers/images/white_cheers.png").getFile()));
            blackImage = ImageIO.read(new File(classLoader.getResource("checkers/images/black_cheers.png").getFile()));
            whiteDImage = ImageIO.read(new File(classLoader.getResource("checkers/images/whiteD_cheers.png").getFile()));
            blackDImage = ImageIO.read(new File(classLoader.getResource("checkers/images/blackD_cheers.png").getFile()));
            fieldImage = ImageIO.read(new File(classLoader.getResource("checkers/images/cheers_pole.png").getFile()));
        } catch (Exception e) {
            throw new TgException("Need game resourses", e);
        }
        BufferedImage image = new BufferedImage(260, 260, BufferedImage.TYPE_INT_ARGB);
        String b[] = picture.split("\\n");
        char a[] = new char[64];
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < 8; j++) {
                a[i * 8 + j] = b[i].charAt(j);
            }
        }
        Graphics g = image.getGraphics();
        g.drawImage(fieldImage, 0, 0, null);
        for (int i = 0; i < a.length; i++) {
            if ('b' == a[i]) {
                g.drawImage(blackImage, (i % 8) * 30 + 10, (i / 8) * 30 + 10, null);
            }
            if (a[i] == 'w') {
                g.drawImage(whiteImage, (i % 8) * 30 + 10, (i / 8) * 30 + 10, null);
            }
            if ('B' == a[i]) {
                g.drawImage(blackDImage, (i % 8) * 30 + 10, (i / 8) * 30 + 10, null);
            }
            if (a[i] == 'W') {
                g.drawImage(whiteDImage, (i % 8) * 30 + 10, (i / 8) * 30 + 10, null);
            }

        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            baos.flush();
            byte[] f = baos.toByteArray();
            baos.close();
            return f;
        } catch (IOException e) {
            throw new TgException("rebuffering error", e);
        }
    }
}
