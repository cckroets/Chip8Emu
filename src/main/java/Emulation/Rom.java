package Emulation;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;


/**
 * @author ckroetsc
 */
public class Rom
{
  private static String romExtension  = ".rom";
  private static String saveExtension  = ".chip8";
  private static String romFolderName  = "roms";
  private static String saveFolderName = "saves";

  private String filename;
  private String displayName;
  private Map<Integer,KeyInfo> keyMap;

  public Rom(String filename)
  {
    this.filename = filename;
  }

  public Rom(String filename, String displayName, Map keyMap)
  {
    this.filename = filename;
    this.displayName = displayName;
    this.keyMap = keyMap;
  }

  public static void setRomFolderName(String newFolder)
  {
    romFolderName = newFolder;
  }

  public static void setSaveFolderName(String newFolder)
  {
    saveFolderName = newFolder;
  }

  private String getSaveFileName()
  {
    return saveFolderName + "/" + filename.replace(romExtension,saveExtension);
  }

  private File getResourceFile(String filename) {
    URL url = getClass().getClassLoader().getResource(filename);
    File file = null;
    try {
      file = new File(url.toURI());
    } catch (URISyntaxException e) {
      System.err.println("Could not read resource: " + filename);
    } catch (NullPointerException e) {
      System.err.println("Could not read resource: " + filename);
    }
    return file;
  }

  public File getRomFile() {
    return getResourceFile(romFolderName + "/" + filename);
  }

  public DataInputStream getLoadStream()
  {
    FileInputStream in = null;
    try {
      in = new FileInputStream(getSaveFileName());
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return new DataInputStream(in);
  }

  public DataOutputStream getSaveStream()
  {
    FileOutputStream out = null;
    try {
      out = new FileOutputStream(getSaveFileName());
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return new DataOutputStream(out);
  }

  class KeyInfo
  {
    public int keycode;
    public String doc;
  }
}
