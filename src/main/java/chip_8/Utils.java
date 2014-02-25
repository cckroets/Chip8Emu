package chip_8;


import java.net.URL;


/**
 * @author ckroetsc
 */
public class Utils
{
  public static int get_nib(int num, int pos) {
    return (num >> (4 * pos)) & 0xF;
  }

  public static int getX(int num) {
    return get_nib(num,2);
  }

  public static int getY(int num) {
    return get_nib(num,1);
  }

  public static int getKK(int num) {
    return num & 0x00FF;
  }

  public static int getNNN(int num) {
    return num & 0x0FFF;
  }

  public static boolean testBit(byte b, int pos) {
    return ((b >> pos) & 1) == 1;
  }

  public static int concatBytes(byte a, byte b) {
    return ((a & 0xFF) << 8) | (b & 0xFF);
  }

  public static URL getResourceURL(String file) {
    return Utils.class.getClassLoader().getResource(file);
  }
}
