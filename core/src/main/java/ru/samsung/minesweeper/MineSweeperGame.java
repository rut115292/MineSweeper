package ru.samsung.minesweeper;

import com.badlogic.gdx.Game;
import ru.samsung.minesweeper.screens.MenuScreen;

public class MineSweeperGame extends Game {
    public static final float VIRTUAL_WIDTH = 800;  // Уменьшил для лучшей видимости
    public static final float VIRTUAL_HEIGHT = 1200; // Увеличил для вертикальной ориентации

    @Override
    public void create() {
        setScreen(new MenuScreen(this));
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
