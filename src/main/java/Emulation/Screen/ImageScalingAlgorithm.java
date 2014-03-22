package Emulation.Screen;


/**
 * @author ckroetsc
 */
public interface ImageScalingAlgorithm<Pixel>
{
  public Bitmap<Pixel> upscaleBitmap(Bitmap<Pixel> original);

  public int getScaleFactor();

  public String getDisplayName();
}
