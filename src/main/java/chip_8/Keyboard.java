package chip_8;


import Emulation.Hardware;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;


/**
 * @author ckroetsc
 */
public class Keyboard extends KeyAdapter implements Hardware
{
  public static final int NUM_KEYS = 16;

  private boolean keys[];

  private static final int[] keyMap = new int[]
                              { 0x1, 0x2, 0x3, 0xC,
                                0x4, 0x5, 0x6, 0xD,
                                0x7, 0x8, 0x9, 0xE,
                                0xA, 0x0, 0xB, 0xF };

  public Keyboard() {
    keys = new boolean[NUM_KEYS];
  }

  public int waitForPress() {
    for (int i = 0; i < NUM_KEYS; i++)
      if (keys[i]) return i;
    return -1;
  }

  public boolean isDown(int key) {
    return keys[key];
  }

  private void handleKeyEvent(KeyEvent event, boolean pressRelease) {
    char key = Character.toLowerCase(event.getKeyChar());

    if ((key >= '0') && (key <= '9')) {
      keys[key - '0'] = pressRelease;
    } else if ((key >= 'a') && (key <= 'f')) {
      keys[key - 'a' + 0xA] = pressRelease;
    }
  }

  @Override
  public void keyPressed(KeyEvent event)
  {
    // Key was pressed
    handleKeyEvent(event, true);
  }

  @Override
  public void keyReleased(KeyEvent event)
  {
    // Key was released
    handleKeyEvent(event,false);
  }

  public void release(int key)
  {
    keys[key] = false;
  }

  @Override
  public void saveState(DataOutput out)
      throws IOException
  {

  }

  @Override
  public void loadState(DataInput in)
      throws IOException
  {

  }

  @Override
  public void reset()
  {
    Arrays.fill(keys, 0, NUM_KEYS, false);
  }
}
