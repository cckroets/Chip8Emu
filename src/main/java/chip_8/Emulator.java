package chip_8;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
  private JButton loadRomButton;

  /* Initialize the emulator */
  public Emulator()
  {
    /* Create new JFrame */
    JFrame window = new JFrame("CHIP-8");
    JPanel panel = new JPanel();
    screen = new Display();
    final JList romList = new JList(romFolder.list());

    window.getContentPane().add(panel);
    panel.setLayout(new BorderLayout());

    JScrollPane romChooser = makeRomChooser(romList,screen.getPreferredSize());
    romChooser.setPreferredSize(new Dimension(160,screen.getPreferredSize().height));
    JPanel buttons = makeButtonPanel(romList);

    /* Add panels to the frame */
    panel.add(screen, BorderLayout.EAST);
    panel.add(romChooser,BorderLayout.WEST);
    panel.add(buttons,BorderLayout.SOUTH);
    int width = screen.getPreferredSize().width;
    int height = screen.getPreferredSize().height;
    panel.setPreferredSize(new Dimension(width+160,height+buttons.getPreferredSize().height));

    /* JFrame Properties */
    window.pack();
    window.setResizable(false);
    window.setLocation(200, 200);
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setVisible(true);
  }

  private JPanel makeButtonPanel(final JList romList)
  {
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));
    loadRomButton = new JButton("Load Rom");
    JButton resetButton = new JButton("Reset");
    JButton saveButton = new JButton("Save");
    JButton loadButton = new JButton("Load");

    buttonPanel.add(loadRomButton);
    buttonPanel.add(Box.createHorizontalGlue());
    buttonPanel.add(resetButton);
    buttonPanel.add(saveButton);
    buttonPanel.add(loadButton);

    loadRomButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        Object rom = romList.getSelectedValue();
        if (rom == null) return;
        if (cpu == null) {
          /* Start the CPU */
          cpu = new CPU(screen,rom.toString());
          new Thread(cpu).start();
        } else {
          cpu.reset(rom.toString());
          cpu.run();
        }
      }
    });

    return buttonPanel;
  }

  private JScrollPane makeRomChooser(final JList romList, Dimension parent)
  {
    romList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    romList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    romList.setVisibleRowCount(-1);
    romList.setFixedCellWidth(parent.width/4);
    return new JScrollPane(romList);
  }

  public static void main(String[] a)
  {
    new Emulator();
  }
}
