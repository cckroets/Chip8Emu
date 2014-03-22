package chip_8;


import Emulation.Emulator;
import Emulation.Rom;
import Emulation.Screen.ImageScalingAlgorithm;
import Emulation.Screen.ImageScalingFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Collection;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


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
    window.setJMenuBar(makeMenuBar(romList));
    window.getContentPane().add(panel);
    panel.setLayout(new BorderLayout());
    JScrollPane romChooser = makeRomChooser(romList, _display.getPreferredSize());

    /* Add panels to the frame */
    panel.add(_display, BorderLayout.CENTER);
    panel.add(romChooser,BorderLayout.SOUTH);

    _display.addKeyListener(new KeyAdapter()
    {
      @Override
      public void keyPressed(KeyEvent keyEvent)
      {
        if (keyEvent.getKeyCode() == KeyEvent.VK_SPACE)
          _emu.pause();
      }
    });

    /* JFrame Properties */
    window.pack();
    window.setResizable(true);
    window.setLocation(200, 200);
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setVisible(true);
  }

  private JScrollPane makeRomChooser(final JList romList, Dimension parent)
  {
    romList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    romList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    romList.setVisibleRowCount(-1);
    romList.setFixedCellWidth(parent.width / 8 - 2);
    Color fg = new Color(0x34383D);
    Color bg = Color.WHITE;
    romList.setBackground(fg);
    romList.setForeground(bg);
    romList.setSelectionBackground(bg);
    romList.setSelectionForeground(fg);
    JScrollPane pane = new JScrollPane(romList);
    pane.setPreferredSize(new Dimension(parent.width, parent.height / 4));
    romList.addListSelectionListener(new ListSelectionListener()
    {
      @Override
      public void valueChanged(ListSelectionEvent listSelectionEvent)
      {
        loadRom(romList);
      }
    });
    return pane;
  }

  private void loadRom(final JList romList)
  {
    if (romList.isSelectionEmpty() || romList.getValueIsAdjusting()) return;
    String romName = romList.getSelectedValue().toString();
    Rom rom = RomFactory.createRom(romName);
    _display.grabFocus();
    _emu.reset(rom);
  }

  private JMenuItem makeFileMenu(final JList romList)
  {
    JMenu fileMenu = new JMenu("File");
    addMenuItem(fileMenu, new JMenuItem("Open Rom"), KeyEvent.VK_O, new AbstractAction()
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        loadRom(romList);
      }
    });
    fileMenu.addSeparator();
    addMenuItem(fileMenu, new JMenuItem("Load"), KeyEvent.VK_L, new AbstractAction()
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        _emu.load();
        _display.grabFocus();
      }
    });
    addMenuItem(fileMenu, new JMenuItem("Save"), KeyEvent.VK_S, new AbstractAction()
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        _emu.save();
        _display.grabFocus();
      }
    });
    fileMenu.addSeparator();
    addMenuItem(fileMenu, new JMenuItem("Reset"), KeyEvent.VK_R, new AbstractAction()
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        _emu.reset();
        _display.grabFocus();
      }
    });
    return fileMenu;
  }

  private JMenuItem makeVideoMenu()
  {
    JMenu videoMenu = new JMenu("Video");
    final JColorChooser colorPicker = new JColorChooser();
    colorPicker.setColor(Display.FOREGROUND_COLOR);
    addMenuItem(videoMenu, new JMenuItem("Change Foreground Color"), KeyEvent.VK_UNDEFINED, new AbstractAction()
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        Color chosen = JColorChooser.showDialog(colorPicker, null, null);
        Display.setForegroundColor(chosen);
      }
    });
    addMenuItem(videoMenu, new JMenuItem("Change Background Color"), KeyEvent.VK_UNDEFINED, new AbstractAction()
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        Color chosen = JColorChooser.showDialog(colorPicker, null, null);
        Display.setBackgroundColor(chosen);
      }
    });


    videoMenu.addSeparator();
    Collection<ImageScalingAlgorithm> algorithms = ImageScalingFactory.getAlgorithms();
    ButtonGroup algGroup = new ButtonGroup();
    int algIndex = KeyEvent.VK_1;
    for (final ImageScalingAlgorithm alg : algorithms) {
      JRadioButtonMenuItem algOption = new JRadioButtonMenuItem(alg.getDisplayName());
      addMenuItem(videoMenu, algOption, algIndex,new AbstractAction()
      {
        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
          _display.setUpscaleAlgorithm(alg);
        }
      });
      algOption.setSelected(KeyEvent.VK_1 == algIndex);
      algGroup.add(algOption);
      algIndex++;
    }
    return videoMenu;
  }

  private JMenuBar makeMenuBar(final JList romList)
  {
    JMenuBar menuBar = new JMenuBar();
    menuBar.add(makeFileMenu(romList));
    menuBar.add(makeVideoMenu());
    return menuBar;
  }

  private static void addMenuItem(JMenu menu, JMenuItem jMenuItem, int key, ActionListener actionListener)
  {
    if (key != KeyEvent.VK_UNDEFINED) {
      jMenuItem.setAccelerator(KeyStroke.getKeyStroke(key,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }
    jMenuItem.addActionListener(actionListener);
    menu.add(jMenuItem);
  }

}
