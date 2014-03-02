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
    window.setResizable(false);
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    window.getContentPane().add(panel);//, BorderLayout.CENTER);

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
    window.setLocation(200, 200);
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setVisible(true);



    romList.addListSelectionListener(new ListSelectionListener()
    {
      @Override
      public void valueChanged(ListSelectionEvent e)
      {
        if (cpu != null)
          cpu.stop();
        /* Start the CPU */
        screen.clear();
        screen.grabFocus();
        cpu = new CPU(screen,romList.getSelectedValue().toString());
        new Thread(cpu).start();
      }
    });

  }

  public static void main(String[] a)
  {
    new Emulator();
  }

  public class Rom {
    private File file;

    public Rom(File file)
    {
      this.file = file;
    }

    public String toString() {
      return file.getName();
    }
  }


}
