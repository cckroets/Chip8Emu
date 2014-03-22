package Emulation.Screen;


import Emulation.Hardware;
import java.awt.Point;

/**
 * @author ckroetsc
 */
public interface Bitmap<Pixel> extends Hardware
{
  public Pixel get(int x, int y);

  public Pixel get(Point p);

  public void set(int x, int y, Pixel p);

  public boolean similar(Pixel p1, Pixel p2);

  public Bitmap<Pixel> genEmptyAndScaled(int scaleFactor);

  public void clear();

  public int getHeight();

  public int getWidth();

  public void setThreshold(double d);
}

