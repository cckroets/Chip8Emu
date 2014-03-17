package Emulation.Screen;


/**
 * @author ckroetsc
 */
public abstract class ImageScalingFactory
{

  public static ImageScalingAlgorithm newCompositeAlgorithm(final ImageScalingAlgorithm... algs)
  {
    int product = 1;
    for (ImageScalingAlgorithm alg : algs) {
      product *= alg.getScaleFactor();
    }
    final int scaleFactor = product;

    return new ImageScalingAlgorithm() {

      @Override
      public Bitmap upscaleBitmap(Bitmap original)
      {
        Bitmap upscaled = original;
        for (ImageScalingAlgorithm alg : algs) {
          upscaled = alg.upscaleBitmap(upscaled);
        }
        return upscaled;
      }

      @Override
      public int getScaleFactor()
      {
        return scaleFactor;
      }
    };
  }

}
