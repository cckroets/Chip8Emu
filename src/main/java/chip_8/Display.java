package chip_8;


import java.awt.*;
import javax.swing.*;
import java.util.Arrays;


/**
 * @author ckroetsc
 */
public class Display extends JPanel
{
  public static final int TILES_ACROSS = 64;
  public static final int TILES_DOWN = 32;
  public static int TILE_SIZE = 4;
  public static int SCREEN_WIDTH = TILE_SIZE * TILES_ACROSS;
  public static int SCREEN_HEIGHT = TILE_SIZE * TILES_DOWN;

  public static Color FOREGROUND_COLOR = new Color(0x19251B);
  public static Color BACKGROUND_COLOR = new Color(0x496D4F);

  private boolean[][] pixelModel;

  @Override
  public void paint(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;

    for (int x = 0; x < TILES_ACROSS; x++) {
      for (int y = 0; y < TILES_DOWN; y++) {
        Color c = (pixelModel[x][y]) ? FOREGROUND_COLOR : BACKGROUND_COLOR;
        g2.setColor(c);
        g2.fillRect(x*TILE_SIZE,y*TILE_SIZE,TILE_SIZE,TILE_SIZE);
      }
    }
  }

  /* Draw a sprite av (vx,vy) on the screen, which starts at i in memory */
  public boolean draw(int vx, int vy, int i, byte[] memory, int height) {

    vx = vx % TILES_ACROSS;
    int y = vy % TILES_DOWN;
    boolean collision = false;

    for (int p = 0; p < height; p++) {
      int bite = memory[i+p] & 0xff;
      for (int b = 0; b < 8; b++) {
        boolean on = ((bite >> 7-b) & 1) == 1;
        int x = (vx + b) % TILES_ACROSS;
        boolean erased = on && pixelModel[x][y];
        collision = collision || erased;
        pixelModel[x][y] ^= on;
      }
      y = (y + 1) % TILES_DOWN;
    }

    repaint();
    return collision;
  }

  /* Clear the screen */
  public void clear() {
    for (int i = 0; i < TILES_ACROSS; i++) {
      Arrays.fill(pixelModel,false);
    }
  }

  public Display() {
    pixelModel = new boolean[TILES_ACROSS][TILES_DOWN];
    setLayout(new GridLayout(TILES_ACROSS,TILES_DOWN));
    this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
    this.setFocusable(true);
  }


}
