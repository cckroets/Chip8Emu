package chip_8;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Random;
import com.google.common.io.Files;

/**
 * @author ckroetsc
 */
public class CPU implements Runnable
{ 
  public static final int SPRITE_LENGTH = 5;
  public static final int START_OF_PROGRAM = 0x200;
  public static final int RAM_SIZE = 4096;

  private Display display;
  private Registers reg;
  private Keyboard keyboard;
  private Random randGenerator;
  private byte memory[];
  private boolean exit;

  public CPU(Display d)
  {
    display = d;
    init();
  }

  private void init()
  {
    keyboard = new Keyboard();
    randGenerator = new Random();
    reg = new Registers();
    exit = false;
    memory = new byte[RAM_SIZE];

    File rom = new File("src/main/resources/roms/pong2.rom");
    File digits = null;
    try {
      digits = new File(Utils.getResourceURL("Digits.bin").toURI());
    } catch (URISyntaxException e) {
      System.err.println("Could not read resource");
    }

    loadBytesToMemory(rom,START_OF_PROGRAM);
    loadBytesToMemory(digits,0);

    display.addKeyListener(keyboard);
  }


  /* Load bytes from a file into memory at the given location */
  private void loadBytesToMemory(File file, int location)
  {
    byte[] bytes;

    try {
      int len = (int)file.length();
      bytes = Files.toByteArray(file);
      System.arraycopy(bytes,0,memory,location,len);
    /* Handle File Not Found */
    } catch (FileNotFoundException e) {
      System.err.print("File not found!");
    /* Handle IO Exception */
    } catch (IOException e) {
      System.err.print("Cannot read File!");
    }
    System.out.println();
  }

  @Override
  public void run()
  {
    while (! exit) {
      /* Read instruction*/
      int instr = Utils.concatBytes(memory[reg.pc],memory[reg.pc+1]);
      /* Update PC */
      reg.pc += 2;
      /* Execute instruction */
      executeInstr(instr);
      /* Wait for Clock Cycle to end */
      sleep();
    }
  }

  private void sleep() {
    try {
      Thread.sleep(1);
    } catch(InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
  }

  private void executeInstr(int instr)
  {
    int x   = (instr & 0x0F00) >> 8;
    int y   = (instr & 0x00F0) >> 4;
    int kk  = instr & 0x00FF;
    int nnn = instr & 0x0FFF;

    switch(instr & 0xF000)
    {
      case 0x0000:
        if (instr == 0x00E0) display.clear();
        else if (instr == 0x00EE) ret();
        break;
      case 0x1000:
        reg.pc = nnn;
        break;
      case 0x2000:
        call(nnn);
        break;
      case 0x3000:
        se(x,kk);
        break;
      case 0x4000:
        sne(x,kk);
        break;
      case 0x5000:
        se(x,reg.v[y]);
        break;
      case 0x6000:
        reg.v[x] = kk;
        break;
      case 0x7000:
        reg.v[x] += kk;
        break;
      case 0x8000:
        dispatchALU(x, y, Utils.get_nib(instr, 0));
        break;
      case 0x9000:
        sne(x,reg.v[y]);
        break;
      case 0xA000:
        reg.i = nnn;
        break;
      case 0xB000:
        reg.pc = (nnn + reg.v[0]);
        break;
      case 0xC000:
        int rand = randGenerator.nextInt(256);
        reg.v[x] = rand & kk;
        break;
      case 0xD000:
        boolean col = display.draw(reg.v[x], reg.v[y], reg.i, memory, instr & 0x000F);
        reg.v[0xf] = col ? 1 : 0;
        break;
      case 0xE000:
        boolean skp_if_pressed = (instr & 1) == 0;
        if (skp_if_pressed == keyboard.isDown(reg.v[x]))
          reg.pc += 2;
        break;
      case 0xF000:
        dispatchLD(x, kk);
        break;
      default:
        System.exit(1);

    }
  }

  private void ret() {
    /* Return from a sub-routine */
    reg.sp--;
    reg.pc = reg.stack[reg.sp];
  }

  private void se(int x, int bite) {
    if (reg.v[x] == bite)
      reg.pc += 2;
  }

  private void sne(int x, int bite) {
    if (reg.v[x] != bite)
      reg.pc += 2;
  }

  private void call(int nnn) {
    reg.stack[reg.sp] = reg.pc;
    reg.sp++;
    reg.pc = nnn;
  }


  private void dispatchALU(int x, int y, int opcode) {
    int result = 0;
    int vX = reg.v[x];
    int vY = reg.v[y];

    switch (opcode) {
      case 0:
        result = vY;
        break;
      case 1:
        result = vX | vY;
        break;
      case 2:
        result = vX & vY;
        break;
      case 3:
        result = vX ^ vY;
        break;
      case 4:
        result = vX + vY;
        reg.v[0xf] = (result > 0xFF) ? 1 : 0;
        result &= 0xFF;
        break;
      case 5:
        reg.v[0xf] = (vX > vY) ? 1 : 0;
        result = vX - vY;
        break;
      case 6:
        reg.v[0xf] = (vX & 1);
        result = vX >> 1;
        break;
      case 7:
        reg.v[0xf] = (vY > vX) ? 1 : 0;
        result = vY - vX;
        break;
      case 0xE:
        reg.v[0xf] = (vX & 0x80) > 0 ? 1 : 0;
        result = vX << 1;
        break;
    }
    reg.v[x] = 0xFF & result;
  }

  private void dispatchLD(int x, int opcode) {
    switch (opcode) {
      case 0x07:
        reg.v[x] = reg.dt;
        break;
      case 0x0A:
        int k = keyboard.waitForPress();
        reg.v[x] = k;
        break;
      case 0x15:
        reg.dt = reg.v[x];
        break;
      case 0x18:
        reg.st = reg.v[x];
        break;
      case 0x1E:
        reg.i += reg.v[x];
        break;
      case 0x29:
        reg.i = reg.v[x] * SPRITE_LENGTH;
        break;
      case 0x33:
        storeBCD(reg.v[x]);
        break;
      case 0x55:
        reg.store(x,memory);
        break;
      case 0x65:
        reg.load(x, memory);
        break;
      default:
        System.exit(1);
    }
  }

  /* Store the binary digits of a number in memory at I */
  private void storeBCD(int num)
  {
    int hun = num / 100;
    int ten = (num % 100) / 10;
    int one = (num % 100) % 10;

    memory[reg.i] = (byte)hun;
    memory[reg.i+1] = (byte)ten;
    memory[reg.i+2] = (byte)one;
  }

  public void stop() {
    exit = true;
  }

}
