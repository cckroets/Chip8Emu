package Emulation;


import java.util.Collection;


/**
 * An interface used by an Emulator to emulate a CPU. A concrete implementation of a CPU
 * can be used to create an instance of an Emulator, which will emulate the processor.
 *  * @author Curtis Kroetsch
 */
public interface CPU
{
  /**
   * Load a rom into the CPU.
   * This task may be delegated to a Memory unit.
   * @param rom The new ROM to read from
   */
  public void loadRom(Rom rom);

  /**
   * Complete a single CPU cycle. This may include:
   * <li> Reading an Instruction</li>
   * <li> Incrementing the Program Counter</li>
   * <li> Executing the Instruction</li>
   * <li> Synchronizing timing with emulated hardware</li>
   */
  public void executeCycle();

  /**
   * Returns a complete collection of every hardware component
   * associated with the CPU.
   * @return A collection of associated Hardware
   */
  public Collection<Hardware> getHardwareComponents();

  /**
   * Do any cleanup necessary for the CPU that is needed before
   * exiting the emulation.
   */
  public void cleanup();
}