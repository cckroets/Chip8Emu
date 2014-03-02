package chip_8;


import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/**
 * @author ckroetsc
 */
public class Emulator
{
  /* The CHIP-8 CPU that executes instructions */
  private CPU cpu = null;

  /* Displays the screen of the Chip-8 */
  private Display screen;

  private File romFolder = Utils.getResourceFile("roms");

  /* Initialize the emulator */
  public Emulator()
  {
    /* Create new JFrame */
    JFrame window = new JFrame("CHIP-8");
    JPanel panel = new JPanel();
    window.getContentPane().add(panel);
    panel.setLayout(new BorderLayout());

    final JList romList = new JList(romFolder.list());
    romList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    romList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    romList.setVisibleRowCount(-1);

    /* Add display to the frame */
    screen = new Display();
    panel.add(screen, BorderLayout.NORTH);
    int width = screen.getPreferredSize().width;
    int height = screen.getPreferredSize().height;
    romList.setFixedCellWidth(width/4-1);
    panel.setPreferredSize(new Dimension(width,height*3/2));

    JScrollPane listScroller = new JScrollPane(romList);
    panel.add(listScroller,BorderLayout.CENTER);

    /* JFrame Properties */
    window.pack();
    window.setResizable(false);
    window.setLocation(200, 200);
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setVisible(true);



    romList.addListSelectionListener(new ListSelectionListener()
    {
      @Override
      public void valueChanged(ListSelectionEvent e)
      {
        if (! e.getValueIsAdjusting()) {
          String rom = romList.getSelectedValue().toString();
          System.out.println("CHANGED");

          if (cpu == null) {
            /* Start the CPU */
            screen.clear();
            screen.grabFocus();
            cpu = new CPU(screen,rom);
            new Thread(cpu).start();
          } else {
            cpu.reset(rom);
          }
        }
      }
    });

  }

  public static void main(String[] a)
  {
    new Emulator();
  }
}
