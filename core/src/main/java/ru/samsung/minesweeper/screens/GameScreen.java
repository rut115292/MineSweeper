package ru.samsung.minesweeper.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ru.samsung.minesweeper.GameBoard;
import ru.samsung.minesweeper.MineSweeperGame;
import ru.samsung.minesweeper.TextButton;
import ru.samsung.minesweeper.Timer;
import ru.samsung.minesweeper.Cell;

public class GameScreen implements Screen {
    private Game game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private GameBoard board;
    private Timer timer;
    private Texture closedTexture;
    private Texture openedTexture;
    private Texture mineTexture;
    private Texture flagTexture;
    private BitmapFont font;
    private TextButton backButton;

    private float cellSize;
    private float boardWidth;
    private float boardHeight;
    private float boardX;
    private float boardY;

    private boolean firstMove;
    private boolean gameEnded;

    // Для обработки касаний
    private boolean touching;
    private float touchStartX, touchStartY;
    private long touchStartTime;
    private boolean longPressHandled;
    private static final float LONG_PRESS_DURATION = 500; // миллисекунды

    // Параметры интерфейса
    private static final float TOP_PANEL_HEIGHT = 100;
    private static final float BACK_BUTTON_SIZE = 80;

    public GameScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(MineSweeperGame.VIRTUAL_WIDTH, MineSweeperGame.VIRTUAL_HEIGHT, camera);
        camera.position.set(MineSweeperGame.VIRTUAL_WIDTH / 2, MineSweeperGame.VIRTUAL_HEIGHT / 2, 0);

        batch = new SpriteBatch();
        board = new GameBoard();
        timer = new Timer();
        firstMove = true;
        gameEnded = false;

        touching = false;
        longPressHandled = false;

        // Загрузка текстур
        closedTexture = new Texture("field_closed.png");
        openedTexture = new Texture("field_opened.png");
        mineTexture = new Texture("mina_blown.png");
        flagTexture = new Texture("red_flag.png");

        // Загрузка шрифта
        font = new BitmapFont(Gdx.files.internal("crystal50yellow.fnt"),
            Gdx.files.internal("crystal50yellow.png"), false);

        // Рассчитываем размеры игрового поля
        calculateBoardSize();

        // Создаем кнопку "Назад"
        backButton = new TextButton("Назад", 20, MineSweeperGame.VIRTUAL_HEIGHT - 20, font,
            () -> {
                timer.stop();
                game.setScreen(new MenuScreen(game));
            });
    }

    private void calculateBoardSize() {
        // Игровое поле должно быть на всю ширину экрана
        boardWidth = MineSweeperGame.VIRTUAL_WIDTH;

        // Вычисляем размер ячейки
        cellSize = boardWidth / GameBoard.SIZE;

        // Вычисляем высоту поля
        boardHeight = cellSize * GameBoard.SIZE;

        // Позиция по X (всегда 0, т.к. поле на всю ширину)
        boardX = 0;

        // Позиция по Y (после верхней панели)
        boardY = TOP_PANEL_HEIGHT;

        // Если поле не помещается по высоте, уменьшаем размер ячейки
        if (boardY + boardHeight > MineSweeperGame.VIRTUAL_HEIGHT) {
            float availableHeight = MineSweeperGame.VIRTUAL_HEIGHT - TOP_PANEL_HEIGHT;
            cellSize = availableHeight / GameBoard.SIZE;
            boardWidth = cellSize * GameBoard.SIZE;
            boardHeight = availableHeight;
            boardX = (MineSweeperGame.VIRTUAL_WIDTH - boardWidth) / 2;
            boardY = TOP_PANEL_HEIGHT;
        }

        System.out.println("Board size: " + boardWidth + "x" + boardHeight);
        System.out.println("Cell size: " + cellSize);
        System.out.println("Board position: " + boardX + ", " + boardY);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Обновление таймера
        if (!gameEnded && !firstMove) {
            timer.update();
        }

        // Обработка ввода
        handleInput(delta);

        batch.begin();

        // Отрисовка верхней панели
        drawTopPanel();

        // Отрисовка игрового поля
        drawGameBoard();

        // Отрисовка сообщения о победе/поражении
        if (gameEnded) {
            drawGameEndMessage();
        }

        batch.end();
    }

    private void drawTopPanel() {
        // Количество флагов (слева)
        String flagsText = "Флаги: " + board.getRemainingFlags();
        font.draw(batch, flagsText, 30, MineSweeperGame.VIRTUAL_HEIGHT - 40);

        // Счётчик времени (справа)
        String timeText = timer.getTimeString();
        float timeWidth = font.getRegion().getTexture() != null ? 150 : 100;
        font.draw(batch, timeText, MineSweeperGame.VIRTUAL_WIDTH - timeWidth - 30,
            MineSweeperGame.VIRTUAL_HEIGHT - 40);

        // Кнопка "Назад" (если игра окончена)
        if (gameEnded) {
            backButton.render(batch);
        }
    }

    private void drawGameBoard() {
        for (int row = 0; row < GameBoard.SIZE; row++) {
            for (int col = 0; col < GameBoard.SIZE; col++) {
                float x = boardX + col * cellSize;
                float y = boardY + (GameBoard.SIZE - 1 - row) * cellSize;

                if (board.isRevealed(col, row)) {
                    if (board.getCell(col, row) == Cell.MINE) {
                        batch.draw(mineTexture, x, y, cellSize, cellSize);
                    } else {
                        batch.draw(openedTexture, x, y, cellSize, cellSize);
                        int value = board.getCell(col, row).getValue();
                        if (value > 0) {
                            String text = String.valueOf(value);
                            float textScale = cellSize / 100f;
                            font.getData().setScale(Math.min(textScale, 1.0f));
                            float textWidth = font.getRegion().getTexture() != null ? 30 : 20;
                            font.draw(batch, text,
                                x + cellSize/2 - textWidth/2,
                                y + cellSize/2 + 20);
                            font.getData().setScale(1.0f);
                        }
                    }
                } else {
                    batch.draw(closedTexture, x, y, cellSize, cellSize);
                    if (board.isFlagged(col, row)) {
                        batch.draw(flagTexture, x, y, cellSize, cellSize);
                    }
                }
            }
        }
    }

    private void drawGameEndMessage() {
        String message = board.isGameWin() ? "ПОБЕДА!" : "ПРОИГРЫШ!";
        float messageX = MineSweeperGame.VIRTUAL_WIDTH / 2 - 100;
        float messageY = MineSweeperGame.VIRTUAL_HEIGHT / 2;

        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(closedTexture, messageX - 20, messageY - 40, 240, 100);
        batch.setColor(1, 1, 1, 1);

        font.getData().setScale(1.2f);
        font.draw(batch, message, messageX, messageY);
        font.getData().setScale(1.0f);
    }

    private void handleInput(float delta) {
        if (Gdx.input.justTouched()) {
            touching = true;
            touchStartX = Gdx.input.getX();
            touchStartY = Gdx.input.getY();
            touchStartTime = System.currentTimeMillis();
            longPressHandled = false;
        }

        if (touching && !longPressHandled && Gdx.input.isTouched()) {
            long elapsedTime = System.currentTimeMillis() - touchStartTime;
            if (elapsedTime >= LONG_PRESS_DURATION) {
                handleTouch(true);
                longPressHandled = true;
            }
        }

        if (!Gdx.input.isTouched() && touching) {
            if (!longPressHandled) {
                handleTouch(false);
            }
            touching = false;
        }
    }

    private void handleTouch(boolean isLongPress) {
        float screenX = touchStartX;
        float screenY = touchStartY;
        com.badlogic.gdx.math.Vector3 worldPos = viewport.unproject(new com.badlogic.gdx.math.Vector3(screenX, screenY, 0));

        if (gameEnded && !isLongPress) {
            if (backButton.contains(worldPos.x, worldPos.y)) {
                backButton.execute();
            }
            return;
        }

        if (worldPos.y > MineSweeperGame.VIRTUAL_HEIGHT - TOP_PANEL_HEIGHT) {
            return;
        }

        int col = (int)((worldPos.x - boardX) / cellSize);
        int row = (int)((worldPos.y - boardY) / cellSize);
        row = GameBoard.SIZE - 1 - row;

        if (col >= 0 && col < GameBoard.SIZE && row >= 0 && row < GameBoard.SIZE) {

            if (firstMove && !isLongPress) {
                if (!board.isRevealed(col, row) && !board.isFlagged(col, row)) {
                    board.placeMines(col, row);
                    timer.start();
                    firstMove = false;
                }
            }

            if (!firstMove && !gameEnded) {
                if (isLongPress) {
                    // Длительное нажатие: ставим/убираем флаг
                    if (!board.isRevealed(col, row)) {
                        board.toggleFlag(col, row);
                    }
                } else {
                    // Короткое нажатие
                    if (board.isRevealed(col, row)) {
                        // На открытой клетке: проверяем chord reveal
                        int adjacentFlags = countAdjacentFlags(col, row);
                        int number = board.getCell(col, row).getValue();

                        if (adjacentFlags == number && number > 0) {
                            // Раскрываем соседние клетки
                            boolean exploded = revealAdjacentCells(col, row);
                            if (exploded) {
                                gameEnded = true;
                                timer.stop();
                                board.revealAllMines();
                            }
                        }
                    } else {
                        // На закрытой клетке: открываем
                        if (!board.isFlagged(col, row)) {
                            board.revealCell(col, row);
                            if (board.isGameOver()) {
                                timer.stop();
                            }
                        }
                    }

                    // Проверка победы
                    if (board.isGameWin()) {
                        gameEnded = true;
                        timer.stop();
                    } else if (board.isGameOver()) {
                        gameEnded = true;
                        board.revealAllMines();
                    }
                }
            }
        }
    }

    private int countAdjacentFlags(int x, int y) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newX = x + i;
                int newY = y + j;
                if (newX >= 0 && newX < GameBoard.SIZE && newY >= 0 && newY < GameBoard.SIZE) {
                    if (board.isFlagged(newX, newY)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private boolean revealAdjacentCells(int x, int y) {
        boolean exploded = false;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newX = x + i;
                int newY = y + j;
                if (newX >= 0 && newX < GameBoard.SIZE && newY >= 0 && newY < GameBoard.SIZE) {
                    // Пропускаем клетки с флагами
                    if (!board.isFlagged(newX, newY) && !board.isRevealed(newX, newY)) {
                        if (board.getCell(newX, newY) == Cell.MINE) {
                            exploded = true;
                        }
                        board.revealCell(newX, newY);
                    }
                }
            }
        }

        return exploded;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        calculateBoardSize();
        backButton.setPosition(400, MineSweeperGame.VIRTUAL_HEIGHT - 20);
    }

    @Override
    public void pause() {
        if (timer != null) {
            timer.stop();
        }
    }

    @Override
    public void resume() {
        if (!gameEnded && !firstMove && timer != null) {
            timer.start();
        }
    }

    @Override
    public void hide() {
        if (timer != null) {
            timer.stop();
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        closedTexture.dispose();
        openedTexture.dispose();
        mineTexture.dispose();
        flagTexture.dispose();
        font.dispose();
    }
}
