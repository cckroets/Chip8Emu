package chip_8;


import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * @author ckroetsc
 */
public class Registers
{

  /* 16 8-bit general purpose registers. regV[0xF] is a flag */
  public byte v[];

  /* Special 16-bit register I, holds memory addresses */
  public short i;

  /* 16-bit program counter */
  public short pc = CPU.START_OF_PROGRAM;

  /* Array of 16 16-bit values which store return addresses. (16 levels of subroutines) */
  public short stack[];

  /* 8-bit Stack Pointer, points to the top most level of the stack */
  public byte sp = 0;

  /* Delay Timer. Subtract 1 from the register at 60Hz when non-zero */
  public byte dt = 0;

  /* Sound Timer. Same as DT, but buzzes when non-zero */
  public byte st = 0;

  /* A Timer which keeps track of when to update ST and DT */
  private Timer timer;

  /* Audio clip that controls the sound */
  private Clip soundChip;

  /* Timers work at 60Hz. Convert to ms. */
  private static int TIMER_PERIOD = 1000/60;

  /* Contains the sound activated by the Sound Timer */
  private static final File SOUND_FILE = Utils.getResourceFile("buzz.wav");

  /* Initialize the registers */
  public Registers() {
    v = new byte[16];
    stack = new short[16];
    timer = new Timer();
    setupClip();

    timer.scheduleAtFixedRate(new TimerTask()
    {
      @Override
      public void run()
      {
        if (dt > 0) dt--;

        dt = (dt > 0x00) ? (byte)(dt - 1) : dt;

        if (st > 0) {
          soundChip.start();
          st--;
        } else if (soundChip.isRunning()) {
          soundChip.stop();
          soundChip.setFramePosition(100);
        }
      }
    }, TIMER_PERIOD, TIMER_PERIOD);
  }

  /* Initialize the clip */
  public void setupClip()
  {
    try {
      AudioInputStream audioIn = AudioSystem.getAudioInputStream(SOUND_FILE);
      soundChip = AudioSystem.getClip();
      soundChip.open(audioIn);
    } catch (UnsupportedAudioFileException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (LineUnavailableException e) {
      e.printStackTrace();
    }
  }

  public void shutdown()
  {
    soundChip.close();
  }



}
