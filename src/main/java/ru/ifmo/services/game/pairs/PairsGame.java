package ru.ifmo.services.game.pairs;

import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
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
import java.util.Collections;
import java.util.List;

public class PairsGame<S extends PairsStep> implements Game<S> {
    private boolean isPlayerSurrender;
    private boolean isFirstCard;
    private int firstCardX;
    private int firstCardY;
    private Player player;
    private PairsBoard board;

    public PairsGame(Player player) {
        this.isPlayerSurrender = false;
        this.player = player;
        this.board = new PairsBoard();
        this.isFirstCard = true;
    }

    @NotNull
    @Override
    public Pair<String, Boolean> step(@NotNull S step) {
        if (step.x == 0) {
            board.notSelectAll();
            return new Pair<>("Choose new pair", true);
        }
        if (!player.equals(step.player)) {
            return new Pair<>("Wrong player tried to make turn", false);
        }
        if (isPlayerSurrender) {
            return new Pair<>("You're surrender", false);
        }
        if (isFirstCard) {
            if (board.select(step.x - 1, step.y - 1)) {
                this.firstCardX = step.x;
                this.firstCardY = step.y;
                isFirstCard = false;
                return new Pair<>("Select pair", false);
            } else {
                return new Pair<>("Wrong select!!!", false);
            }
        }
        isFirstCard = true;
        if (board.makeTurn(firstCardX - 1, firstCardY - 1, step.x - 1, step.y - 1)) {
            if (isFinished()) {
                return new Pair<>(player.getName() + " won", false);
            } else {
                return new Pair<>("Turn was made", true);
            }
        } else {
            return new Pair<>("Wrong turn", false);
        }
    }

    @NotNull
    @Override
    public byte[] drawPicture(@NotNull Player player) throws TgException {
        String picture = board.toString();
        Image aImage;
        Image bImage;
        Image vImage;
        Image gImage;
        Image dImage;
        Image eImage;
        Image yoImage;
        Image jImage;
        Image zImage;
        Image iImage;
        Image iiImage;
        Image kImage;
        Image lImage;
        Image mImage;
        Image nImage;
        Image oImage;
        Image pImage;
        Image rImage;
        Image aBImage;
        Image bBImage;
        Image vBImage;
        Image gBImage;
        Image dBImage;
        Image eBImage;
        Image yoBImage;
        Image jBImage;
        Image zBImage;
        Image iBImage;
        Image iiBImage;
        Image kBImage;
        Image lBImage;
        Image mBImage;
        Image nBImage;
        Image oBImage;
        Image pBImage;
        Image rBImage;
        Image closeImage;
        Image fieldImage;
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            aImage = ImageIO.read(new File(classLoader.getResource("pairs/images/а.png").getFile()));
            bImage = ImageIO.read(new File(classLoader.getResource("pairs/images/б.png").getFile()));
            vImage = ImageIO.read(new File(classLoader.getResource("pairs/images/в.png").getFile()));
            gImage = ImageIO.read(new File(classLoader.getResource("pairs/images/г.png").getFile()));
            dImage = ImageIO.read(new File(classLoader.getResource("pairs/images/д.png").getFile()));
            eImage = ImageIO.read(new File(classLoader.getResource("pairs/images/е.png").getFile()));
            yoImage = ImageIO.read(new File(classLoader.getResource("pairs/images/ё.png").getFile()));
            jImage = ImageIO.read(new File(classLoader.getResource("pairs/images/ж.png").getFile()));
            zImage = ImageIO.read(new File(classLoader.getResource("pairs/images/з.png").getFile()));
            iImage = ImageIO.read(new File(classLoader.getResource("pairs/images/и.png").getFile()));
            iiImage = ImageIO.read(new File(classLoader.getResource("pairs/images/й.png").getFile()));
            kImage = ImageIO.read(new File(classLoader.getResource("pairs/images/к.png").getFile()));
            lImage = ImageIO.read(new File(classLoader.getResource("pairs/images/л.png").getFile()));
            mImage = ImageIO.read(new File(classLoader.getResource("pairs/images/м.png").getFile()));
            nImage = ImageIO.read(new File(classLoader.getResource("pairs/images/н.png").getFile()));
            oImage = ImageIO.read(new File(classLoader.getResource("pairs/images/о.png").getFile()));
            pImage = ImageIO.read(new File(classLoader.getResource("pairs/images/п.png").getFile()));
            rImage = ImageIO.read(new File(classLoader.getResource("pairs/images/р.png").getFile()));
            aBImage = ImageIO.read(new File(classLoader.getResource("pairs/images/а_big.png").getFile()));
            bBImage = ImageIO.read(new File(classLoader.getResource("pairs/images/б_big.png").getFile()));
            vBImage = ImageIO.read(new File(classLoader.getResource("pairs/images/в_big.png").getFile()));
            gBImage = ImageIO.read(new File(classLoader.getResource("pairs/images/г_big.png").getFile()));
            dBImage = ImageIO.read(new File(classLoader.getResource("pairs/images/д_big.png").getFile()));
            eBImage = ImageIO.read(new File(classLoader.getResource("pairs/images/е_big.png").getFile()));
            yoBImage = ImageIO.read(new File(classLoader.getResource("pairs/images/ё_big.png").getFile()));
            jBImage = ImageIO.read(new File(classLoader.getResource("pairs/images/ж_big.png").getFile()));
            zBImage = ImageIO.read(new File(classLoader.getResource("pairs/images/з_big.png").getFile()));
            iBImage = ImageIO.read(new File(classLoader.getResource("pairs/images/и_big.png").getFile()));
            iiBImage = ImageIO.read(new File(classLoader.getResource("pairs/images/й_big.png").getFile()));
            kBImage = ImageIO.read(new File(classLoader.getResource("pairs/images/к_big.png").getFile()));
            lBImage = ImageIO.read(new File(classLoader.getResource("pairs/images/л_big.png").getFile()));
            mBImage = ImageIO.read(new File(classLoader.getResource("pairs/images/м_big.png").getFile()));
            nBImage = ImageIO.read(new File(classLoader.getResource("pairs/images/н_big.png").getFile()));
            oBImage = ImageIO.read(new File(classLoader.getResource("pairs/images/о_big.png").getFile()));
            pBImage = ImageIO.read(new File(classLoader.getResource("pairs/images/п_big.png").getFile()));
            rBImage = ImageIO.read(new File(classLoader.getResource("pairs/images/р_big.png").getFile()));
            closeImage = ImageIO.read(new File(classLoader.getResource("pairs/images/close_card.png").getFile()));
            fieldImage = ImageIO.read(new File(classLoader.getResource("pairs/images/pair_pole.png").getFile()));
        } catch (Exception e) {
            throw new TgException("Need game resourses", e);
        }
        BufferedImage image = new BufferedImage(320, 320, BufferedImage.TYPE_INT_ARGB);
        String b[] = picture.split("\\n");
        char a[] = new char[36];
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < 6; j++) {
                a[i * 6 + j] = b[i].charAt(j);
            }
        }
        Graphics g = image.getGraphics();
        g.drawImage(fieldImage, 0, 0, null);
        for (int i = 0; i < a.length; i++) {
            if ('а' == a[i]) {
                g.drawImage(aImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('б' == a[i]) {
                g.drawImage(bImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('в' == a[i]) {
                g.drawImage(vImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('г' == a[i]) {
                g.drawImage(gImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('д' == a[i]) {
                g.drawImage(dImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('е' == a[i]) {
                g.drawImage(eImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('ё' == a[i]) {
                g.drawImage(yoImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('ж' == a[i]) {
                g.drawImage(jImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('з' == a[i]) {
                g.drawImage(zImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('и' == a[i]) {
                g.drawImage(iImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('й' == a[i]) {
                g.drawImage(iiImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('к' == a[i]) {
                g.drawImage(kImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('л' == a[i]) {
                g.drawImage(lImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('м' == a[i]) {
                g.drawImage(mImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('н' == a[i]) {
                g.drawImage(nImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('о' == a[i]) {
                g.drawImage(oImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('п' == a[i]) {
                g.drawImage(pImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('р' == a[i]) {
                g.drawImage(rImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('А' == a[i]) {
                g.drawImage(aBImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('Б' == a[i]) {
                g.drawImage(bBImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('В' == a[i]) {
                g.drawImage(vBImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('Г' == a[i]) {
                g.drawImage(gBImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('Д' == a[i]) {
                g.drawImage(dBImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('Е' == a[i]) {
                g.drawImage(eBImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('Ё' == a[i]) {
                g.drawImage(yoBImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('Ж' == a[i]) {
                g.drawImage(jBImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('З' == a[i]) {
                g.drawImage(zBImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('И' == a[i]) {
                g.drawImage(iBImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('Й' == a[i]) {
                g.drawImage(iiBImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('К' == a[i]) {
                g.drawImage(kBImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('Л' == a[i]) {
                g.drawImage(lBImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('М' == a[i]) {
                g.drawImage(mBImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('Н' == a[i]) {
                g.drawImage(nBImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('О' == a[i]) {
                g.drawImage(oBImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('П' == a[i]) {
                g.drawImage(pBImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('Р' == a[i]) {
                g.drawImage(rBImage, (i % 6) * 30 , (i / 6) * 30, null);
            }
            if ('*' == a[i]) {
                g.drawImage(closeImage, (i % 6) * 30 , (i / 6) * 30, null);
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


    @NotNull
    public String getMessage(@NotNull Player player) {
        if (player.equals(this.player)) {
            StringBuilder sb = new StringBuilder();
            if (!board.isFull()) {
                sb.append("Current player: ");
                sb.append(player.getName());
            } else {
                sb.append("Winner: ");
                sb.append(player.getName());
            }
            return sb.toString();
        } else {
            return "Wrong player";
        }
    }

    @NotNull
    public Keyboard getKeyboard(@NotNull Player player) {
        return board.getKeyboard();
    }

    @NotNull
    @Override
    public GameUpdate getGameUpdate(@NotNull Player player) {
        return new GameUpdate(getMessage(player), getKeyboard(player), drawPicture(player));
    }

    @Override
    public void surrender(@NotNull Player player) {
        this.isPlayerSurrender = true;
    }

    @NotNull
    @Override
    public String toJson() {
        return "";
    }

    @NotNull
    @Override
    public List<Player> getPlayes() {
        return Collections.singletonList(player);
    }

    @NotNull
    @Override
    public Games getGameId() {
        return Games.PAIRS;
    }

    @Override
    public boolean isFinished() {
        return isPlayerSurrender || board.isFull();
    }

    @Override
    public boolean isCurrent(@NotNull Player player) {
        return this.player.equals(player);
    }
}
