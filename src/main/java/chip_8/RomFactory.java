package chip_8;

import Emulation.Rom;
import java.io.File;
import java.io.FileReader;

import java.io.IOException;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


/**
 * @author ckroetsc
 */
public class RomFactory
{
  private static JSONObject romConf = readRomConfig();

  private static JSONObject readRomConfig()
  {
    File romConfig = Utils.getResourceFile("romConfig.json");
    JSONObject conf = null;
    try {
      conf = (JSONObject) JSONValue.parse(new FileReader(romConfig));
    } catch (IOException e) {
      System.err.println("Error parsing config");
    }
    return conf;
  }

  public static Rom createRom(String filename) {
    String filenameNoExt = filename.split(".rom")[0];
    JSONObject romConfig = (JSONObject)romConf.get(filenameNoExt);
    if (romConfig == null) {
      System.out.println("Bad");
      return new Rom(filename);
    } else {
      String displayName = romConfig.get("name").toString();
      Map keyMap = (Map)romConfig.get("keyMap");
      System.out.printf("RomName=%s, map=%s",displayName, keyMap.toString());
      return new Rom(filename,displayName,keyMap);
    }


  }
}
