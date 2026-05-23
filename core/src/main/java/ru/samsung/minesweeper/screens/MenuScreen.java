package ru.samsung.minesweeper.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ru.samsung.minesweeper.MineSweeperGame;
import ru.samsung.minesweeper.TextButton;

public class MenuScreen implements Screen {
    private Game game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private BitmapFont font;
    private TextButton[] buttons;

    public MenuScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(MineSweeperGame.VIRTUAL_WIDTH, MineSweeperGame.VIRTUAL_HEIGHT, camera);
        camera.position.set(MineSweeperGame.VIRTUAL_WIDTH / 2, MineSweeperGame.VIRTUAL_HEIGHT / 2, 0);

        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("crystal50yellow.fnt"),
            Gdx.files.internal("crystal50yellow.png"), false);

        float centerX = MineSweeperGame.VIRTUAL_WIDTH / 2;
        float startY = MineSweeperGame.VIRTUAL_HEIGHT - 150;

        buttons = new TextButton[] {
            new TextButton("Играть", centerX - 60, startY, font,
                () -> game.setScreen(new GameScreen(game))),
            new TextButton("Настройки", centerX - 80, startY - 80, font,
                () -> game.setScreen(new SettingsScreen(game))),
            new TextButton("Таблица рекордов", centerX - 140, startY - 160, font,
                () -> game.setScreen(new RecordsScreen(game))),
            new TextButton("Об игре", centerX - 70, startY - 240, font,
                () -> game.setScreen(new AboutScreen(game))),
            new TextButton("Выход", centerX - 50, startY - 320, font,
                () -> Gdx.app.exit())
        };
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        handleInput();

        batch.begin();
        for (TextButton button : buttons) {
            button.render(batch);
        }
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.input.getY();
            touchX = viewport.unproject(new com.badlogic.gdx.math.Vector3(touchX, touchY, 0)).x;
            touchY = viewport.unproject(new com.badlogic.gdx.math.Vector3(touchX, touchY, 0)).y;

            for (TextButton button : buttons) {
                if (button.contains(touchX, touchY)) {
                    button.execute();
                    break;
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
