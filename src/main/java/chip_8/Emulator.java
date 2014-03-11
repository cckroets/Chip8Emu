package chip_8;


import Emulation.Rom;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.*;


/**
 * @author ckroetsc
 */
public class Emulator
{
  /* The CHIP-8 Chip8Processor that executes instructions */
  private Chip8Processor cpu = null;

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
    JPanel buttons = makeButtonPanel(romList,screen);

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

  private JPanel makeButtonPanel(final JList romList, final Display display)
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

    saveButton.addActionListener(new AbstractAction()
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        cpu.save();
        screen.grabFocus();
      }
    });

    loadButton.addActionListener(new AbstractAction()
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        cpu.load();
        screen.grabFocus();
      }
    });

    loadRomButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        if (romList.isSelectionEmpty()) return;

        String romName = romList.getSelectedValue().toString();
        Rom rom = new Rom(romName);
        display.grabFocus();

        if (cpu == null) {
          /* Start the Chip8Processor */
          cpu = new Chip8Processor(screen,rom);
          new Thread(cpu).start();
        } else {
          cpu.reset(rom);
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

  public void save(String fname)
      throws IOException
  {
    FileOutputStream out = new FileOutputStream(fname);
    cpu.saveState(new DataOutputStream(out));
    out.close();
  }

  public void load(String fname)
      throws IOException
  {
    FileInputStream in = new FileInputStream(fname);
    cpu.loadState(new DataInputStream(in));
    in.close();
  }
}
