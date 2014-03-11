package Emulation;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author ckroetsc
 * A general CPU that implements:
 * - Pause and Unpause
 * - Saving and Loading states
 * - Reset
 */
public abstract class CPU implements Runnable, Hardware
{
  private List<Hardware> hardwareComponents = new ArrayList<Hardware>();
  private Rom currentRom;
  private Mode mode = Mode.PLAY;

  protected CPU(Rom rom)
  {
    currentRom = rom;
  }

  /* A reset of the Chip8Processor has been requested */
  @Override public void reset()
  {
    mode = Mode.RESET;
  }

  /* Reset the CPU with a new Rom */
  public void reset(Rom newRom)
  {
    currentRom = newRom;
    reset();
  }

  /* A pause has been requested for the Chip8Processor */
  public void pause()
  {
    mode = Mode.PAUSE;
  }

  /* Resume activity of the Chip8Processor */
  public void resume()
  {
    mode = Mode.PLAY;
  }

  /* Exit emulation */
  public void quit()
  {
    mode = Mode.QUIT;
  }

  public void load()
  {
    mode = Mode.LOAD;
  }

  public void save()
  {
    mode = Mode.SAVE;
  }

  /* Run the Chip8Processor. */
  @Override public void run()
  {
    boolean quit = false;

    while (! quit) {
      switch (mode) {
        /* Continue CPU Execution */
        case PLAY:
          executeCycle();
          break;
        /* Stop the CPU. Emulation ended */
        case QUIT:
          quit = true;
          break;
        /* Reset the emulator with the current rom */
        case RESET:
          resetComponents();
          loadRom(currentRom);
          break;
        /* Load state from file */
        case LOAD:
          loadState(currentRom.getLoadStream());
          break;
        /* Save state to file */
        case SAVE:
          saveState(currentRom.getSaveStream());
          break;
        /* Emulation is paused, do nothing */
        case PAUSE:
          break;

      }
    }
    cleanup();
  }

  private void cleanup()
  {

  }

  /* Hook up a piece of Hardware to the Chip8Processor */
  protected void addHardware(Hardware... components)
  {
    hardwareComponents.addAll(Arrays.asList(components));
  }

  /* Reset all hardware components of the Chip8Processor
   * Memory, Registers, Display, etc...
   */
  private void resetComponents()
  {
    for (Hardware hw : hardwareComponents) {
      hw.reset();
    }
    resume();
  }

  /* Save the state of the CPU */
  @Override public void saveState(DataOutputStream out)
  {
    try {
      for (Hardware hw : hardwareComponents) {
        hw.saveState(out);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    resume();
  }

  /* Load the CPU from previous state */
  @Override public void loadState(DataInputStream in)
  {
    try {
      for (Hardware hw : hardwareComponents) {
        hw.loadState(in);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    resume();
  }


  /* Load the currentRom for use */
  protected abstract void loadRom(Rom rom);

  /* Complete a Chip8Processor cycle. This may include:
   * 1) Read Instruction
   * 2) Increment Program Counter
   * 3) Execute Instruction
   * 4) Synchronize timing with emulated hardware
   */
  protected abstract void executeCycle();


  /* Different Modes the CPU can be in */
  private enum Mode { PLAY, PAUSE, RESET, LOAD, SAVE, QUIT }
}
