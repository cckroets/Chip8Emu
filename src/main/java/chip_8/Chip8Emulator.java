package chip_8;


import Emulation.Emulator;


/**
 * @author ckroetsc
 */
public class Chip8Emulator
{
  /* The CHIP-8 Chip8Processor that executes instructions */
  private Chip8Processor cpu;
  private Chip8GUI gui;
  private Emulator emu;

  /* Initialize the emulator */
  public Chip8Emulator()
  {
    Display display = new Display();
    this.cpu = new Chip8Processor(display);
    this.emu = new Emulator(cpu);
    this.gui = new Chip8GUI(emu,display);
  }


  public static void main(String[] a)
  {
    new Chip8Emulator();
  }
}
