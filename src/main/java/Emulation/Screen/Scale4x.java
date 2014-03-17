package Emulation.Screen;


/**
 * @author ckroetsc
 */
public class Scale4x<Pixel> implements ImageScalingAlgorithm<Pixel>
{
  ImageScalingAlgorithm filter1 = new Scale2x();
  ImageScalingAlgorithm filter2 = new Scale2x();

  @Override
  public Bitmap<Pixel> upscaleBitmap(Bitmap<Pixel> original)
  {
    return filter2.upscaleBitmap(filter1.upscaleBitmap(original));
  }

  @Override
  public int getScaleFactor()
  {
    return 4;
  }
}
