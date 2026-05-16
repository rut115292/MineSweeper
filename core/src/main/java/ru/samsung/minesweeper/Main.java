package ru.samsung.minesweeper;

import com.badlogic.gdx.Game;
import ru.samsung.minesweeper.screens.MenuScreen;

public class Main extends Game {

    @Override
    public void create() {
        setScreen(new MenuScreen(this));
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
