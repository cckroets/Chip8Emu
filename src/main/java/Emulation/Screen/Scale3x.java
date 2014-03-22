package Emulation.Screen;


/**
 * @author ckroetsc
 */
public class Scale3x<Pixel> extends ScaleNx<Pixel>
{
  final int SCALE_FACTOR = 3;

  @Override
  public int getScaleFactor()
  {
    return SCALE_FACTOR;
  }

  @Override
  protected void upscalePixel(Bitmap<Pixel> bmap, int x, int y,
                              Bitmap<Pixel> scaled, int sx, int sy)
  {
    Pixel e = bmap.get(x,y);
    Pixel b = getNorth(bmap, x, y);
    Pixel f = getEast(bmap, x, y);
    Pixel h = getSouth(bmap, x, y);
    Pixel d = getWest(bmap, x, y);
    Pixel c = getNorthEast(bmap, x, y);
    Pixel i = getSouthEast(bmap, x, y);
    Pixel g = getSouthWest(bmap, x, y);
    Pixel a = getNorthWest(bmap, x, y);

    Pixel p1=e, p2=e, p3=e, p4=e, p5=e, p6=e, p7=e, p8=e, p9=e;

    if (changeValueLogic(d,b,h,f,bmap)) p1=d;
    if (changeValueLogic(d,b,h,f,e,c,bmap) ||
        (changeValueLogic(b,f,d,h,e,a,bmap))) p2=b;
    if (changeValueLogic(b,f,d,h,bmap)) p3=f;
    if (changeValueLogic(h,d,f,b,e,a,bmap) ||
        (changeValueLogic(d,b,h,f,e,g,bmap))) p4=d;
    if (changeValueLogic(b,f,d,h,e,i,bmap) ||
        changeValueLogic(f,h,b,d,e,c,bmap)) p6=f;
    if (changeValueLogic(h,d,f,b,bmap)) p7=d;
    if (changeValueLogic(f,h,b,d,e,g,bmap) ||
        changeValueLogic(h,d,f,b,e,i,bmap)) p8=h;
    if (changeValueLogic(f,h,b,d,bmap)) p9=f;

    safeSet(sx,sy,scaled,p1);
    safeSet(sx+1,sy,scaled,p2);
    safeSet(sx+2,sy,scaled,p3);
    safeSet(sx,sy+1,scaled,p4);
    safeSet(sx+1,sy+1,scaled,p5);
    safeSet(sx+2,sy+1,scaled,p6);
    safeSet(sx,sy+2,scaled,p7);
    safeSet(sx+1,sy+2,scaled,p8);
    safeSet(sx+2,sy+2,scaled,p9);
  }

  private boolean changeValueLogic(Pixel a, Pixel b, Pixel c, Pixel d, Pixel e, Pixel f, Bitmap<Pixel> bitmap)
  {
    return changeValueLogic(a,b,c,d,bitmap)
        && ! bitmap.similar(e,f);
  }
  private Pixel getNorthEast(Bitmap<Pixel> bitmap, int x, int y)
  {
    return ((y == 0) || (x == bitmap.getWidth()-1)) ?
        null : bitmap.get(x+1,y-1);
  }

  private Pixel getSouthEast(Bitmap<Pixel> bitmap, int x, int y)
  {
    return ((y == bitmap.getHeight()-1) || (x == bitmap.getWidth()-1)) ?
        null : bitmap.get(x+1,y+1);
  }

  private Pixel getNorthWest(Bitmap<Pixel> bitmap, int x, int y)
  {
    return ((y == 0) || (x == 0)) ?
        null : bitmap.get(x-1,y-1);
  }

  private Pixel getSouthWest(Bitmap<Pixel> bitmap, int x, int y)
  {
    return ((y == bitmap.getHeight()-1) || (x == 0))
        ? null : bitmap.get(x-1,y+1);
  }


}
