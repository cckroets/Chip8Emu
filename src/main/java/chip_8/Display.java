package chip_8;


import Emulation.Screen.ImageScalingAlgorithm;
import Emulation.Screen.ImageScalingFactory;
import java.util.List;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;


/**
 * @author ckroetsc
 */
public class Display extends JPanel implements Observer
{
  private int tileSize = 12;
  private int tilesAcross;
  private int tilesDown;

  public static Color FOREGROUND_COLOR = new Color(0x19251B);
  public static Color BACKGROUND_COLOR = new Color(0x496D4F);

  private int algIndex = 0;
  private List<ImageScalingAlgorithm> algorithms = ImageScalingFactory.getAlgorithms();
  private volatile ImageScalingAlgorithm upscaleAlg = ImageScalingFactory.noUpscale();
  private BinaryBitmap _bitmapModel = null;
  private BinaryBitmap _bitmapUpscaled = null;


  @Override
  public synchronized void paint(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;
    int scaleFactor = upscaleAlg.getScaleFactor();
    int scaledTileSize = tileSize / scaleFactor;

    /* Clear display */
    g2.setColor(BACKGROUND_COLOR);
    g2.fillRect(0,0, tileSize * tilesAcross, tileSize * tilesDown);
    g2.setColor(FOREGROUND_COLOR);

    int pixelOnLength = 0;

    for (int y=0; y < _bitmapUpscaled.getHeight(); y++) {
      for (int x=0; x < _bitmapUpscaled.getWidth(); x++) {
        if (_bitmapUpscaled.get(x,y)) {
          pixelOnLength++;
        } else {
          drawLine(g2, x - pixelOnLength, y, pixelOnLength, scaledTileSize);
          pixelOnLength = 0;
        }
      }
      drawLine(g2, _bitmapUpscaled.getWidth() - pixelOnLength, y, pixelOnLength, scaledTileSize);
      pixelOnLength = 0;
    }
  }

  private void drawLine(Graphics2D g2, int x, int y, int width, int scale)
  {
    if (width > 0) {
      g2.fillRect(x * scale, y * scale, scale * width, scale);
    }
  }

  public Display(Chip8Processor cpu)
  {
    this._bitmapModel = cpu.getBitmap();
    this._bitmapUpscaled = _bitmapModel.genEmptyAndScaled(upscaleAlg.getScaleFactor());
    this.tilesAcross = Chip8Processor.PIXEL_WIDTH;
    this.tilesDown = Chip8Processor.PIXEL_HEIGHT;
    cpu.setDisplay(this);

    this.setPreferredSize(new Dimension(tilesAcross * tileSize, tilesDown * tileSize));
    this.setFocusable(true);
    this.addKeyListener(new KeyAdapter()
    {
      @Override
      public void keyPressed(KeyEvent keyEvent)
      {
        if (keyEvent.getKeyChar() == 'n')
        {
          algIndex = (algIndex + 1) % algorithms.size();
          setUpscaleAlgorithm(algorithms.get(algIndex));
        } else if (keyEvent.getKeyChar() == 'm')
        {
          algIndex--;
          algIndex = (algIndex == -1) ? algorithms.size()-1 : algIndex;
          setUpscaleAlgorithm(algorithms.get(algIndex));
        }
      }
    });
  }

  @Override
  public void update(Observable observable, Object o)
  {
    new Thread(new Runnable()
    {
      @Override
      public void run()
      {
        _bitmapUpscaled = (BinaryBitmap) upscaleAlg.upscaleBitmap(_bitmapModel);
        repaint();
      }
    }).start();
  }

  public synchronized void setUpscaleAlgorithm(ImageScalingAlgorithm newAlg)
  {
    this.upscaleAlg = newAlg;
    this._bitmapUpscaled = (BinaryBitmap) newAlg.upscaleBitmap(_bitmapModel);
    repaint();
  }

  public static void setForegroundColor(Color c)
  {
    FOREGROUND_COLOR = c;
  }

  public static void setBackgroundColor(Color c)
  {
    BACKGROUND_COLOR = c;
  }
}
