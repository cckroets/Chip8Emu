package chip_8;


import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;


/**
 * @author ckroetsc
 */
public class Utils
{
  public static byte get_nib(int num, int pos) {
    return (byte)((num >> (4 * pos)) & 0xF);
  }

  public static byte getX(int num) {
    return get_nib(num,2);
  }

  public static byte getY(int num) {
    return get_nib(num,1);
  }

  public static byte getKK(int num) {
    return (byte)num;
  }

  public static short getNNN(int num) {
    return (short)(num & 0x0FFF);
  }

  public static boolean testBit(byte b, int pos) {
    return ((b >> pos) & 1) == 1;
  }

  public static short concatBytes(byte a, byte b) {
    return (short)(((a & 0xFF) << 8) | (b & 0xFF));
  }

  public static File getResourceFile(String filename) {
    URL url = Utils.class.getClassLoader().getResource(filename);
    File file = null;
    try {
      file = new File(url.toURI());
    } catch (URISyntaxException e) {
      System.err.println("Could not read resource");
    }
    return file;
  }

  public static File getRom(String filename) {
    return getResourceFile("roms/" + filename);
  }
}
