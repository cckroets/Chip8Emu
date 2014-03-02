package chip_8;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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
  public short pc;

  /* Array of 16 16-bit values which store return addresses. (16 levels of subroutines) */
  public short stack[];

  /* 8-bit Stack Pointer, points to the top most level of the stack */
  public byte sp;

  /* Delay Timer. Subtract 1 from the register at 60Hz when non-zero */
  public byte dt;

  /* Sound Timer. Same as DT, but buzzes when non-zero */
  public byte st;

  /* Timers work at 60Hz. Convert to ms. */
  private static int TIMER_PERIOD = 1000/60;

  /* Contains the sound activated by the Sound Timer */
  private static final File SOUND_FILE = Utils.getResourceFile("buzz.wav");

  /* Audio clip that controls the sound */
  private static Clip soundChip = setupClip();

  private static final int NUM_REGS = 16;
  private static final int STACK_SIZE = 16;


  /* Initialize the registers */
  public Registers() {
    v = new byte[NUM_REGS];
    stack = new short[STACK_SIZE];
    this.reset();
    Timer timer = new Timer();

    /* Decrement DT and ST when not zero. */
    timer.scheduleAtFixedRate(new TimerTask()
    {
      @Override
      public void run()
      {
        if (dt > 0) dt--;
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
  private static Clip setupClip()
  {
    Clip soundChip = null;
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
    return soundChip;
  }

  public void shutdown()
  {
    soundChip.close();
  }

  public void reset()
  {
    pc = CPU.START_OF_PROGRAM;
    sp = 0;
    dt = 0;
    st = 0;
    i = 0;
    Arrays.fill(v, 0, NUM_REGS, CPU.zero);
    Arrays.fill(stack,0,STACK_SIZE, CPU.zero);
  }


}
