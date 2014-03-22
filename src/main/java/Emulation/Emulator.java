package Emulation;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


/**
 * A general Emulator for a given CPU. This is what operates the CPU,
 * and has functionality for pausing, resetting, changing ROMs, and loading/saving states of the CPU to disk.
 * @author ckroetsc
 */
public class Emulator implements Runnable, Hardware
{
  private CPU cpu;
  private volatile State state = State.START;
  private Rom currentRom;

  public Emulator(CPU cpu)
  {
    this.cpu = cpu;
  }

  /**
   * Request to reset the CPU and all of its components.
   */
  public void reset()
  {
    changeState(State.RESET);
  }

  /* Reset the CPU with a new Rom */

  /**
   * Resets the CPU with a new ROM. Must be called to start the emulation
   * @param newRom the new ROM to be loaded
   */
  public void reset(Rom newRom)
  {
    currentRom = newRom;
    if (getState() == State.START)
      new Thread(this).start();
    else
      reset();
  }

  /**
   *  Request to pause the emulation
   */
  public synchronized void pause()
  {
    if (getState() == State.PAUSE)
      resume();
    else
      changeState(State.PAUSE);
  }

  /**
   *  Resume activity of the emulation
   */
  public synchronized void resume()
  {
    changeState(State.PLAY);
  }

  /**
   *  Request to exit the emulation
   */
  public void quit()
  {
    changeState(State.QUIT);
  }

  /**
   *  Request to load from a previous save immediately
   */
  public void load()
  {
    changeState(State.LOAD);
  }

  /**
   * Request to create a save immediately
   */
  public void save()
  {
    changeState(State.SAVE);
  }

  private synchronized void changeState(State newState)
  {
    state = newState;
  }

  /**
   * Return the current state that the Emulator is in
   * @return Current state
   * @see Emulation.Emulator.State
   */
  private synchronized State getState()
  {
    return state;
  }

  /**
   *  Run the CPU. Handles all state changes
   */
  @Override public void run()
  {
    boolean quit = false;

    while (! quit) {
      switch (getState()) {
        /* The CPU has just started. Load the rom and run */
        case START:
          cpu.loadRom(currentRom);
          resume();
          break;
        /* Continue CPU Execution */
        case PLAY:
          cpu.executeCycle();
          break;
        /* Stop the CPU. Emulation ended */
        case QUIT:
          quit = true;
          break;
        /* Reset the emulator with the current rom */
        case RESET:
          resetComponents();
          cpu.loadRom(currentRom);
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
          Thread.yield();
          break;
      }
    }
    cpu.cleanup();
  }



  /** Reset all hardware components of the CPU
   * e.g. Memory, Registers, Display, ...
   */
  private void resetComponents()
  {
    for (Hardware hw : cpu.getHardwareComponents()) {
      hw.reset();
    }
    resume();
  }

  /* Save the state of the CPU */
  @Override public void saveState(DataOutput out)
  {
    try {
      for (Hardware hw : cpu.getHardwareComponents()) {
        hw.saveState(out);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    resume();
  }

  /* Load the CPU from previous state */
  @Override public void loadState(DataInput in)
  {
    try {
      for (Hardware hw : cpu.getHardwareComponents()) {
        hw.loadState(in);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    resume();
  }

  /**
   *  Different states the Emulator can be put in.
   */
  private enum State
  { START, PLAY, PAUSE, RESET, LOAD, SAVE, QUIT }

}

