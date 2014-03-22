package Emulation.Screen;


/**
 * @author ckroetsc
 */
public class Scale2x<Pixel> extends ScaleNx<Pixel>
{
  final int SCALE_FACTOR = 2;

  @Override
  public int getScaleFactor()
  {
    return SCALE_FACTOR;
  }

  @Override
  public String getDisplayName()
  {
    return "Scale 2x";
  }

  @Override
  protected void upscalePixel(Bitmap<Pixel> bmap, int x, int y,
                              Bitmap<Pixel> scaled, int scaled_x, int scaled_y)
  {
    Pixel a = getNorth(bmap,x,y);
    Pixel b = getEast(bmap, x, y);
    Pixel d = getSouth(bmap, x, y);
    Pixel c = getWest(bmap, x, y);
    Pixel p = bmap.get(x,y);
    Pixel p1 = p, p2 = p, p3 = p, p4 = p;
    if (changeValueLogic(c, a, d, b, bmap)) p1 = a;
    if (changeValueLogic(a, b, c, d, bmap)) p2 = b;
    if (changeValueLogic(b, d, a, c, bmap)) p4 = d;
    if (changeValueLogic(d, c, b, a, bmap)) p3 = c;
    safeSet(scaled_x,scaled_y,scaled,p1);
    safeSet(scaled_x+1,scaled_y,scaled,p2);
    safeSet(scaled_x,scaled_y+1,scaled,p3);
    safeSet(scaled_x+1,scaled_y+1,scaled,p4);
  }


}
