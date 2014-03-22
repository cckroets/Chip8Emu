package Emulation.Screen;


/**
 * @author ckroetsc
 */
public abstract class ScaleNx<Pixel> implements ImageScalingAlgorithm<Pixel>
{

  @Override
  public Bitmap<Pixel> upscaleBitmap(Bitmap<Pixel> original)
  {
    Bitmap<Pixel> scaled = original.genEmptyAndScaled(getScaleFactor());
    int scale = getScaleFactor();

    for (int x = 0, scaled_x = 0; x < original.getWidth(); x++, scaled_x+=scale) {
      for (int y = 0, scaled_y = 0; y < original.getHeight(); y++, scaled_y+=scale) {
        upscalePixel(original,x,y,scaled,scaled_x,scaled_y);
      }
    }
    return scaled;
  }

  @Override
  public abstract int getScaleFactor();

  @Override
  public String getDisplayName()
  {
    return String.format("Scale %dx", getScaleFactor());
  }

  protected abstract void upscalePixel(Bitmap<Pixel> bmap, int x, int y,
                                       Bitmap<Pixel> scaled, int sx, int sy);

  protected boolean changeValueLogic(Pixel a, Pixel b, Pixel c, Pixel d, Bitmap<Pixel> bitmap)
  {
    return (bitmap.similar(a,b)
        && !bitmap.similar(a,c)
        && !bitmap.similar(b,d));
  }

  protected Pixel getNorth(Bitmap<Pixel> bitmap, int x, int y)
  {
    return (y == 0) ? null : bitmap.get(x,y-1);
  }

  protected Pixel getSouth(Bitmap<Pixel> bitmap, int x, int y)
  {
    return (y == bitmap.getHeight()-1) ? null : bitmap.get(x,y+1);
  }

  protected Pixel getEast(Bitmap<Pixel> bitmap, int x, int y)
  {
    return (x == bitmap.getWidth()-1) ? null : bitmap.get(x+1,y);
  }

  protected Pixel getWest(Bitmap<Pixel> bitmap, int x, int y)
  {
    return (x == 0) ? null : bitmap.get(x-1,y);
  }

  protected void safeSet(int x, int y, Bitmap<Pixel> bmap, Pixel value)
  {
    if (x >= 0 && x < bmap.getWidth() && y >= 0 && y < bmap.getHeight() && value != null) {
      bmap.set(x,y,value);
    }
  }


}
