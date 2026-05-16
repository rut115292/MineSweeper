package ru.samsung.minesweeper.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ru.samsung.minesweeper.MineSweeperGame;
import ru.samsung.minesweeper.TextButton;

public class SettingsScreen implements Screen {
    private Game game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private BitmapFont font;
    private TextButton backButton;

    public SettingsScreen(Game game) {
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

        backButton = new TextButton("Назад", MineSweeperGame.VIRTUAL_WIDTH/2f - 40, 100, font,
            () -> game.setScreen(new MenuScreen(game)));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        handleInput();

        batch.begin();
        font.draw(batch, "Настройки", MineSweeperGame.VIRTUAL_WIDTH/2f - 80, MineSweeperGame.VIRTUAL_HEIGHT - 100);
        backButton.render(batch);
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.input.getY();
            com.badlogic.gdx.math.Vector3 worldPos = viewport.unproject(new com.badlogic.gdx.math.Vector3(touchX, touchY, 0));

            if (backButton.contains(worldPos.x, worldPos.y)) {
                backButton.execute();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        backButton.setPosition(MineSweeperGame.VIRTUAL_WIDTH/2f - 40, 100);
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
