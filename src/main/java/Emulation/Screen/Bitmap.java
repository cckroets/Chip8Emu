package Emulation.Screen;



/**
 * @author ckroetsc
 */
public interface Bitmap<Pixel>
{
  public Pixel get(int x, int y);

  public void set(int x, int y, Pixel p);

  public boolean similar(Pixel p1, Pixel p2);

  public Bitmap<Pixel> genEmptyAndScaled(int scaleFactor);

  public void clear();

  public int getHeight();

  public int getLength();

  public void setThreshold(double d);
}

