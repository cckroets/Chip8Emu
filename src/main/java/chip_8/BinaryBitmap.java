package chip_8;


import Emulation.Screen.Bitmap;
import java.awt.*;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Observable;


/**
 * @author ckroetsc
 */
public class BinaryBitmap extends Observable implements Bitmap<Boolean>
{
  BitSet _bitmap;
  int _width;
  int _height;


  private BinaryBitmap(int l, int h)
  {
    _bitmap = new BitSet(l * h);
    _width = l;
    _height = h;
  }

  public static BinaryBitmap newBitmap1D(int length, int height)
  {
    return new BinaryBitmap(length,height);
  }

  public int getHeight()
  {
    return _height;
  }

  public int getWidth()
  {
    return _width;
  }

  @Override
  public void setThreshold(double d) { }

  /**
   * Return the Black/White Pixel at (x,y). Black=false, White=true
   * @param x position of the coordinate
   * @param y position of the coordinate
   * @return The polarity of the pixel (On/Off)
   */
  @Override
  public Boolean get(int x, int y)
  {
    return _bitmap.get(y * _width + x);
  }

  @Override
  public Boolean get(Point p)
  {
    return get(p.x,p.y);
  }

  @Override
  public boolean similar(Boolean p1, Boolean p2)
  {
    return (p1 != null) && (p1 == p2);
  }

  @Override
  public BinaryBitmap genEmptyAndScaled(int scaleFactor)
  {
    return new BinaryBitmap(getWidth() * scaleFactor,
                                   getHeight() * scaleFactor);
  }

  public void set(int x, int y)
  {
    set(x,y,true);
  }

  @Override
  public void set(int x, int y, Boolean val)
  {
    setChanged();
    _bitmap.set(y * _width + x, val);
  }

  public void xor(int x, int y, boolean val)
  {
    set(x,y, val ^ get(x,y));
  }

  @Override
  public void clear()
  {
    _bitmap.clear();
    setChanged();
    notifyObservers();
  }


  @Override
  public void saveState(DataOutput out)
      throws IOException
  {
    for (int x = 0; x < getWidth(); x++) {
      for (int y = 0; y < getHeight(); y++) {
        out.writeBoolean(get(x, y));
      }
    }
  }

  @Override
  public void loadState(DataInput in)
      throws IOException
  {
    clear();
    for (int x = 0; x < getWidth(); x++) {
      for (int y = 0; y < getHeight(); y++) {
        set(x, y, in.readBoolean());
      }
    }
    setChanged();
    notifyObservers();
  }

  @Override
  public void reset()
  {
    clear();
  }
}
