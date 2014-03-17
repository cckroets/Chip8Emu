package chip_8;


import Emulation.Emulator;
import Emulation.Rom;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;


/**
 * @author ckroetsc
 */
public class Chip8GUI
{
  final Display _display;
  final Emulator _emu;

  /* Initialize the emulator */
  public Chip8GUI(Emulator e, Display d)
  {
    this._emu = e;
    this._display = d;
    File romFolder = Utils.getResourceFile("roms");

    /* Create new JFrame */
    JFrame window = new JFrame("CHIP-8");
    JPanel panel = new JPanel();
    final JList romList = new JList(romFolder.list());

    window.getContentPane().add(panel);
    panel.setLayout(new BorderLayout());

    JScrollPane romChooser = makeRomChooser(romList,_display.getPreferredSize());
    romChooser.setPreferredSize(new Dimension(160,_display.getPreferredSize().height));
    JPanel buttons = makeButtonPanel(romList);

    /* Add panels to the frame */
    panel.add(_display, BorderLayout.EAST);
    panel.add(romChooser,BorderLayout.WEST);
    panel.add(buttons,BorderLayout.SOUTH);
    int width = _display.getPreferredSize().width;
    int height = _display.getPreferredSize().height;
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
    JButton loadRomButton = new JButton("Load Rom");
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
        _emu.save();
        _display.grabFocus();
      }
    });

    loadButton.addActionListener(new AbstractAction()
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        _emu.load();
        _display.grabFocus();
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
        _display.grabFocus();
        _emu.reset(rom);
      }
    });

    resetButton.addActionListener(new AbstractAction()
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        _emu.reset();
        _display.grabFocus();
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
}
