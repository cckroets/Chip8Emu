package chip_8;


import Emulation.Hardware;
import Emulation.Screen.Bitmap;
import Emulation.Screen.ImageScalingAlgorithm;
import Emulation.Screen.Scale2x;
import Emulation.Screen.Scale3x;
import Emulation.Screen.Scale4x;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import javax.swing.*;


/**
 * @author ckroetsc
 */
public class Display extends JPanel implements Hardware
{
  public static final int TILES_ACROSS = 64;
  public static final int TILES_DOWN = 32;
  public static int TILE_SIZE = 12;
  public static int SCREEN_WIDTH = TILE_SIZE * TILES_ACROSS;
  public static int SCREEN_HEIGHT = TILE_SIZE * TILES_DOWN;

  public static Color FOREGROUND_COLOR = new Color(0x19251B);
  public static Color BACKGROUND_COLOR = new Color(0x496D4F);

  private BinaryBitmap pixelModel = BinaryBitmap.newBitmap1D(TILES_ACROSS, TILES_DOWN);
  private ImageScalingAlgorithm upscaleAlg = new Scale4x<Boolean>();


  @Override
  public void paint(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;
    BinaryBitmap upscaled = (BinaryBitmap) upscaleAlg.upscaleBitmap(pixelModel);
    int scaleFactor = upscaleAlg.getScaleFactor();
    int scaledTileSize = TILE_SIZE / scaleFactor;

    for (int x = 0; x < TILES_ACROSS*scaleFactor; x++) {
      for (int y = 0; y < TILES_DOWN*scaleFactor; y++) {
        Color c = (upscaled.get(x, y)) ? FOREGROUND_COLOR : BACKGROUND_COLOR;
        g2.setColor(c);
        g2.fillRect(x * scaledTileSize, y * scaledTileSize, scaledTileSize, scaledTileSize);
      }
    }
  }

  /* Draw a sprite av (vx,vy) on the screen, which starts at i in memory */
  public boolean draw(int vx, int vy, int i, Memory mem, int height)
  {
    vx &= 0xFF;
    Chip8Processor.log.dumpI();
    Chip8Processor.log.dumpSprite(height);
    Chip8Processor.log.dumpAllReg();
    if ( vy < 0) vy += TILES_DOWN;
    int y = vy & 0xFF;
    boolean collision = false;
    for (int p = 0; p < height; p++) {
      byte bite = mem.at((short)(i+p));
      for (int b = 0; b < 8; b++) {
        boolean on = ((bite & (0x80 >> b))) != 0;
        int x = (vx + b) % TILES_ACROSS;
        if (x < 0) x += TILES_ACROSS;
        boolean erased = on && pixelModel.get(x, y);
        collision |= erased;
        pixelModel.xor(x,y,on);
      }
      y = (y + 1) % TILES_DOWN;
    }

    repaint();
    return collision;
  }

  /* Clear the screen */
  public void clear() {
    pixelModel.clear();
    repaint();
  }

  public Display() {
    this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
    this.setFocusable(true);
    this.addKeyListener(new KeyAdapter()
    {
      @Override
      public void keyPressed(KeyEvent keyEvent)
      {
        if (keyEvent.getKeyChar() == ' ') {
          if (upscaleAlg.getScaleFactor() == 1)
            upscaleAlg = new Scale4x();
          else
            upscaleAlg = new ImageScalingAlgorithm()
            {
              @Override
              public Bitmap upscaleBitmap(Bitmap original)
              {
                return original;
              }

              @Override
              public int getScaleFactor()
              {
                return 1;
              }
            };
          keyEvent.getComponent().repaint();
        }
      }
    });
  }

  @Override
  public void saveState(DataOutput out)
      throws IOException
  {
    for (int x = 0; x < TILES_ACROSS; x++) {
      for (int y = 0; y < TILES_DOWN; y++) {
        out.writeBoolean(pixelModel.get(x, y));
      }
    }
  }

  @Override
  public void loadState(DataInput in)
      throws IOException
  {
    clear();
    for (int x = 0; x < TILES_ACROSS; x++) {
      for (int y = 0; y < TILES_DOWN; y++) {
        pixelModel.set(x, y, in.readBoolean());
      }
    }
    repaint();
  }

  @Override
  public void reset()
  {
    clear();
  }
}
