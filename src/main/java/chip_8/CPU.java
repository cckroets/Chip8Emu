package chip_8;


import java.io.File;
import java.net.URISyntaxException;
import java.util.Random;


/**
 * @author ckroetsc
 */
public class CPU implements Runnable
{
  private static InstructionLogger log = new InstructionLogger();
  public static final int SPRITE_LENGTH = 5;
  public static final short START_OF_PROGRAM = 0x200;

  private Display display;
  private Registers reg;
  private Keyboard keyboard;
  private Random randGenerator;
  private Memory memory;
  private boolean exit;

  // Use these to avoid annoying explicit casts in arithmetic
  private static final byte zero = 0;
  private static final byte one = 1;

  public CPU(Display d, String romName)
  {
    display = d;
    init(romName);
  }

  private void init(String romName)
  {
    keyboard = new Keyboard();
    randGenerator = new Random();
    reg = new Registers();
    exit = false;
    memory = new Memory();

    File rom = Utils.getRom(romName);
    File digits = Utils.getResourceFile("Digits.bin");

    memory.loadFile(rom, START_OF_PROGRAM);
    memory.loadFile(digits, zero);
    display.addKeyListener(keyboard);
  }


  @Override
  public void run()
  {
    while (! exit) {
      /* Read instruction*/
      short instr = memory.readInstruction(reg.pc);
      /* Update PC */
      reg.pc += 2;
      /* Execute instruction */
      executeInstr(instr);
      /* Wait for Clock Cycle to end */
      sleep();
    }
    display.clear();
  }

  private void sleep() {
    try {
      Thread.sleep(0,10);
    } catch(InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
  }

  private void executeInstr(short instr)
  {
    byte x = Utils.getX(instr);
    byte y = Utils.getY(instr);
    byte kk = Utils.getKK(instr);
    short nnn = Utils.getNNN(instr);

    switch(instr & 0xF000)
    {
      case 0x0000:
        if (instr == 0x00E0) {
          display.clear();
          log.name("CLS");
        }
        else if (instr == 0x00EE) {
          ret();
          log.name("RET");
        }
        break;
      case 0x1000:
        reg.pc = nnn;
        log.addr("JP",nnn);
        break;
      case 0x2000:
        call(nnn);
        log.addr("CALL",nnn);
        break;
      case 0x3000:
        se(x,kk);
        log.regAddr("SE",x,kk);
        break;
      case 0x4000:
        sne(x,kk);
        log.regAddr("SNE",x, kk);
        break;
      case 0x5000:
        se(x,reg.v[y]);
        log.regReg("SE", x, y);
        break;
      case 0x6000:
        reg.v[x] = kk;
        log.regAddr("LD",x,kk);
        break;
      case 0x7000:
        reg.v[x] += kk;
        log.regAddr("ADD",x,kk);
        break;
      case 0x8000:
        dispatchALU(x, y, instr & 0x000F);
        break;
      case 0x9000:
        sne(x,reg.v[y]);
        log.regReg("SNE", x, y);
        break;
      case 0xA000:
        reg.i = nnn;
        log.regAddr("LD", "I", nnn);
        break;
      case 0xB000:
        reg.pc = (short)(nnn + reg.v[0]);
        log.regAddr("JP",0,nnn);
        break;
      case 0xC000:
        int rand = randGenerator.nextInt(256);
        reg.v[x] = (byte)(rand & kk);
        log.regAddr("RND",x,kk);
        break;
      case 0xD000:
        boolean col = display.draw(reg.v[x], reg.v[y], reg.i, memory, (byte)(instr & 0x000F));
        reg.v[0xf] = col ? one : zero;
        log.regRegAddr("DRW", x, y, (instr & 0x000F));
        break;
      case 0xE000:
        boolean skp_if_pressed = (instr & 1) == 0;
        if (skp_if_pressed == keyboard.isDown(reg.v[x])) {
          keyboard.release(reg.v[x]);
          reg.pc += 2;
        }
        String op = (kk == 0x9E) ? "SKP" : "SKNP";
        log.reg(op,x);
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

  private void call(short nnn) {
    reg.stack[reg.sp] = reg.pc;
    reg.sp++;
    reg.pc = nnn;
  }


  private void dispatchALU(byte x, byte y, int opcode) {
    int result = 0;
    byte vX = reg.v[x];
    byte vY = reg.v[y];

    switch (opcode) {
      case 0:
        result = vY;
        break;
      case 1:
        result = (byte)(vX | vY);
        log.regReg("OR", x, y);
        break;
      case 2:
        result = (byte)(vX & vY);
        log.regReg("AND", x, y);
        break;
      case 3:
        result = (byte)(vX ^ vY);
        log.regReg("XOR", x, y);
        break;
      case 4:
        result = (byte)(vX + vY);
        reg.v[0xf] = (byte)((result & 0x100) >> 8);
        result &= 0xFF;
        log.regReg("ADD", x, y);
        break;
      case 5:
        reg.v[0xf] = (vX > vY) ? one : zero;
        result = (byte)(vX - vY);
        log.regReg("SUB", x, y);
        break;
      case 6:
        reg.v[0xf] = (byte)(vX & 1);
        result = (byte)(vX >> 1);
        log.regReg("SHR", x, y);
        break;
      case 7:
        reg.v[0xf] = (vY > vX) ? one : zero;
        result = (byte)(vY - vX);
        log.regReg("SUBN", x, y);
        break;
      case 0xE:
        reg.v[0xf] = (vX & 0x80) != 0 ? one : zero;
        result = (byte)(vX << 1);
        log.regReg("SHL", x, y);
        break;
    }
    reg.v[x] = (byte)(0xFF & result);
  }

  private void dispatchLD(int x, int opcode) {
    switch (opcode) {
      case 0x07:
        reg.v[x] = reg.dt;
        log.regReg("LD", x, "DT");
        break;
      case 0x0A:
        byte k = (byte)keyboard.waitForPress();
        reg.v[x] = k;
        keyboard.release(k);
        log.regReg("LD", x, "K");
        break;
      case 0x15:
        reg.dt = reg.v[x];
        log.regReg("LD", "DT", x);
        break;
      case 0x18:
        reg.st = reg.v[x];
        log.regReg("LD", "ST", x);
        break;
      case 0x1E:
        reg.i += reg.v[x];
        log.regReg("ADD", "I", x);
        break;
      case 0x29:
        reg.i = (short)((reg.v[x] & 0x0F) * SPRITE_LENGTH);
        log.regReg("LD", "F", x);
        break;
      case 0x33:
        memory.storeBCD(reg.v[x], reg.i);
        log.regReg("LD", "B", x);
        break;
      case 0x55:
        memory.store(x,reg);
        log.regReg("LD", "[I]", x);
        break;
      case 0x65:
        memory.load(x, reg);
        log.regReg("LD", x, "[I]");
        break;
    }
  }

  public void stop() {
    exit = true;
  }

  public enum Speed {
    LOW(1), MEDIUM(2), HIGH(3);

    int speed;

    private Speed(int value) {
      this.speed = value;
    }
    public int getValue() {
      return speed;
    }
  }

}

