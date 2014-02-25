package chip_8;


import java.awt.*;
import javax.swing.*;


/**
 * @author ckroetsc
 */
public class Emulator
{
  /* The CHIP-8 CPU that executes instructions */
  private CPU cpu;

  /* Displays the screen of the Chip-8 */
  private Display screen;

  /* Initialize the emulator */
  public Emulator()
  {
    /* Create new JFrame */
    JFrame window = new JFrame("CHIP-8");

    /* Add display to the frame */
    screen = new Display();
    window.getContentPane().add(screen);
    window.pack();

    /* JFrame Properties */
    window.setLocation(200, 200);
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setVisible(true);

    /* Start the CPU */
    cpu = new CPU(screen);
    new Thread(cpu).start();
  }

  public static void main(String[] a)
  {
    new Emulator();
  }


}
