package Emulation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * @author ckroetsc
 * A piece of Hardware. Coule be Chip8Processor, Memory, Registers, Display, etc...
 */
public interface Hardware
{
  public void saveState(DataOutputStream out)
      throws IOException;

  public void loadState(DataInputStream in)
      throws IOException;

  public void reset();
}

