package Emulation.Screen;


import java.util.Arrays;
import java.util.List;


/**
 * @author ckroetsc
 */
public class ImageScalingFactory
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

      @Override
      public String getDisplayName()
      {
        StringBuilder buffer = new StringBuilder();
        for (int i=0; i < algs.length-1; i++) {
          ImageScalingAlgorithm alg = algs[i];
          buffer.append(alg.getDisplayName());
          buffer.append(" -> ");
        }
        buffer.append(algs[algs.length-1].getDisplayName());
        return buffer.toString();
      }
    };
  }

  public static List<ImageScalingAlgorithm> getAlgorithms()
  {
    ImageScalingAlgorithm scale2x = new Scale2x();
    ImageScalingAlgorithm scale3x = new Scale3x();
    ImageScalingAlgorithm scale4x = newCompositeAlgorithm(scale2x,scale2x);

    return Arrays.asList(noUpscale(), scale2x, scale3x, scale4x,
                         newCompositeAlgorithm(scale3x, scale2x),
                         newCompositeAlgorithm(scale2x, scale3x),
                         newCompositeAlgorithm(scale3x, scale4x),
                         newCompositeAlgorithm(scale4x, scale3x));

  }

  public static ImageScalingAlgorithm noUpscale()
  {
    return new ImageScalingAlgorithm()
    {
      @Override
      public Bitmap upscaleBitmap(Bitmap original)
      {
        return original;
      }

      @Override
      public int getScaleFactor()
      {
        return 1;
      }

      @Override
      public String getDisplayName()
      {
        return "Default";
      }
    };
  }
}
