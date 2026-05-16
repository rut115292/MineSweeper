package ru.samsung.minesweeper;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class TextButton {
    private String text;
    private float x, y;
    private BitmapFont font;
    private Rectangle bounds;
    private Runnable action;
    private GlyphLayout layout;

    public TextButton(String text, float x, float y, BitmapFont font, Runnable action) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.font = font;
        this.action = action;
        this.layout = new GlyphLayout();
        updateBounds();
    }

    private void updateBounds() {
        layout.setText(font, text);
        bounds = new Rectangle(x, y - layout.height, layout.width, layout.height);
    }

    public boolean contains(float touchX, float touchY) {
        return bounds.contains(touchX, touchY);
    }

    public void execute() {
        if (action != null) {
            action.run();
        }
    }

    public void render(SpriteBatch batch) {
        font.draw(batch, text, x, y);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        updateBounds();
    }
}
