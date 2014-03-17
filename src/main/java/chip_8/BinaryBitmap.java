package chip_8;


import Emulation.Screen.Bitmap;
import java.util.Arrays;


/**
 * @author ckroetsc
 */
public class BinaryBitmap implements Bitmap<Boolean>
{
  boolean[][] _bitmap;

  private BinaryBitmap(int l, int h)
  {
    _bitmap = new boolean[l][h];
  }

  public static BinaryBitmap newBitmap1D(int length, int height)
  {
    return new BinaryBitmap(length,height);
  }

  public int getHeight()
  {
    return _bitmap[0].length;
  }

  public int getLength()
  {
    return _bitmap.length;
  }

  @Override
  public void setThreshold(double d) { }

  /**
   * Return the Black/White Pixel at (x,y). Black=false, White=true
   * @param x position of the coordinate
   * @param y position of the coordinate
   * @return The polarity of the pixel (On/Off)
   */
  public Boolean get(int x, int y)
  {
    return _bitmap[x][y];
  }


  @Override
  public boolean similar(Boolean p1, Boolean p2)
  {
    return p1 == p2;
  }

  @Override
  public Bitmap<Boolean> genEmptyAndScaled(int scaleFactor)
  {
    return new BinaryBitmap(getLength() * scaleFactor,
                                   getHeight() * scaleFactor);
  }

  public void set(int x, int y)
  {
    set(x,y,true);
  }

  @Override
  public void set(int x, int y, Boolean val)
  {
    _bitmap[x][y] = val;
  }

  public void xor(int x, int y, boolean val)
  {
    _bitmap[x][y] ^= val;
  }

  @Override
  public void clear()
  {
    for (boolean[] row: _bitmap) {
      Arrays.fill(row, false);
    }
  }
}
