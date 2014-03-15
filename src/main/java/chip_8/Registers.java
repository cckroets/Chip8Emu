package chip_8;


import Emulation.Hardware;
import java.io.DataInput;
import java.io.DataOutput;
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
public class Registers implements Hardware
{

  /* 16 8-bit general purpose registers. regV[0xF] is a flag */
  private byte v[];

  /* Special 16-bit register I, holds memory addresses */
  public int i;

  /* 16-bit program counter */
  public int pc;

  /* Array of 16 16-bit values which store return addresses. (16 levels of subroutines) */
  public short stack[];

  /* 8-bit Stack Pointer, points to the top most level of the stack */
  public int sp;

  /* Delay Timer. Subtract 1 from the register at 60Hz when non-zero */
  public int dt;

  /* Sound Timer. Same as DT, but buzzes when non-zero */
  public int st;

  /* Timers work at 60Hz. Convert to ms. */
  private static int TIMER_PERIOD = 1000/60;

  /* Contains the sound activated by the Sound Timer */
  private static final File SOUND_FILE = Utils.getResourceFile("buzz.wav");

  /* Audio clip that controls the sound */
  private static Clip soundChip = setupClip();

  private static final int NUM_REGS = 16;
  private static final int STACK_SIZE = 16;

  public int v(int i)
  {
    return 0xFF & this.v[i];
  }

  public void setVx(int i, int value)
  {
    this.v[i] = (byte)value;
  }

  public void setVF(boolean value)
  {
    this.v[0xF] = value ? Chip8Processor.one : Chip8Processor.zero;
  }

  public void setVx(int i, byte value)
  {
    this.v[i] = value;
  }

  public byte[] getV()
  {
    return this.v;
  }

  public void call(int nnn) {
    stack[sp] = (short)pc;
    sp++;
    pc = nnn;
  }

  /* Return from a sub-routine */
  public void ret() {
    sp--;
    pc = stack[sp];
  }

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

  @Override
  public void saveState(DataOutput out)
      throws IOException
  {
    out.write(v);
    out.writeInt(i);
    out.writeInt(pc);
    out.writeInt(sp);
    out.writeInt(dt);
    out.writeInt(st);
    for (int i = 0; i < STACK_SIZE; i++)
      out.writeShort(stack[i]);
  }

  @Override
  public void loadState(DataInput in)
      throws IOException
  {
    in.readFully(v);
    i  = in.readInt();
    pc = in.readInt();
    sp = in.readInt();
    dt = in.readInt();
    st = in.readInt();
    for (int i = 0; i < STACK_SIZE; i++)
      stack[i] = in.readShort();
  }

  @Override
  public void reset()
  {
    pc = Chip8Processor.START_OF_PROGRAM;
    sp = 0;
    dt = 0;
    st = 0;
    i = 0;
    Arrays.fill(v, 0, NUM_REGS, Chip8Processor.zero);
    Arrays.fill(stack,0,STACK_SIZE, Chip8Processor.zero);
  }


}
