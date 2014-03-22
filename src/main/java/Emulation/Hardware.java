package Emulation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


/**
 * @author ckroetsc
 * A piece of Hardware. Coule be Memory, Registers, Bitmap, etc...
 */
public interface Hardware
{
  public void saveState(DataOutput out)
      throws IOException;

  public void loadState(DataInput in)
      throws IOException;

  public void reset();
}

