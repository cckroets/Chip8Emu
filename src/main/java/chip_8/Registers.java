package chip_8;


import java.io.IOException;
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
  public int v[];

  /* Special 16-bit register I, holds memory addresses */
  public int i;

  /* 16-bit program counter */
  public int pc = CPU.START_OF_PROGRAM;

  /* Array of 16 16-bit values which store return addresses. (16 levels of subroutines) */
  public int stack[];

  /* 8-bit Stack Pointer, points to the top most level of the stack */
  public int sp = 0;

  /* Delay Timer. Subtract 1 from the register at 60Hz when non-zero */
  public int dt = 0;

  /* Sound Timer. Same as DT, but buzzes when non-zero */
  public int st = 0;

  /* A Timer which keeps track of when to update ST and DT */
  private Timer timer;

  /* Audio clip that controls the sound */
  private Clip soundChip;

  /* Timers work at 60Hz. Convert to ms. */
  private static int TIMER_PERIOD = 1000/60;

  /* Contains the sound activated by the Sound Timer */
  private static final URL SOUND_FILE = Utils.getResourceURL("sound.wav");

  /* Initialize the registers */
  public Registers() {
    v = new int[16];
    stack = new int[16];
    timer = new Timer();
    setupClip();

    timer.scheduleAtFixedRate(new TimerTask()
    {
      @Override
      public void run()
      {
        if (dt > 0) dt--;

        dt = (dt > 0) ? dt - 1 : dt;

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


  /* Store [v[0],v[x]] at I in memory */
  public void store(int x, byte memory[]) {
    for (int b = 0; b <= x; b++) {
      memory[i + b] = (byte)v[b];
    }
  }

  /* Load memory at I into [v[0],v[x]] */
  public void load(int x, byte memory[]) {
    for (int b = 0; b <= x; b++) {
      v[b] = memory[i + b];
    }
  }
}
