package chip_8;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author ckroetsc
 */
public class InstructionLogger
{
  private static Logger log = LoggerFactory.getLogger(InstructionLogger.class);

  public void setReg(Registers reg)
  {
    this.reg = reg;
  }

  public void setMem(Memory mem)
  {
    this.mem = mem;
  }

  private Registers reg;
  private Memory mem;

  public InstructionLogger() { }

  public InstructionLogger(Registers registers, Memory memory)
  {
    this.reg = registers;
    this.mem = memory;
  }



  public void name(String s)
  {
    log.debug(s);
  }

  public void addr(String s, int addr)
  {
    log.debug("{} {}", s, addr);
  }

  public void regAddr(String s, int reg, int addr)
  {
    log.debug("{} V{}, {}", s, reg, addr);
  }

  public void regAddr(String s, String reg, int addr)
  {
    log.debug("{} {}, {}", s, reg, addr);
  }

  public void regReg(String s, int r1, int r2)
  {
    log.debug("{} V{}, V{}", s, r1, r2);
  }

  public void reg(String s, int r1)
  {
    log.debug("{} V{}", s, r1);
  }

  public void regReg(String s, int r1, String r2)
  {
    log.debug("{} V{}, {}", s, r1, r2);
  }

  public void regReg(String s, String r1, int r2)
  {
    log.debug("{} {}, V{}", s, r1, r2);
  }

  public void regRegAddr(String s, int r1, int r2, int n)
  {
    log.debug("{} V{}, V{}, {}", s, r1, r2, n);
  }

  public void dumpI()
  {
    log.debug("I=" + reg.i);
  }

  public void dumpV()
  {
    log.debug("V0={}, V1={}, V2={}, V3={}, V4={}, V5={}, V6={}, V7={}, " +
              "V8={}, V9={}, VA={}, VB={}, VC={}, VD={}, VE={}, VF={}",
              reg.v[0],reg.v[1],reg.v[2],reg.v[3],reg.v[4],reg.v[5],
              reg.v[6],reg.v[7],reg.v[8],reg.v[9],reg.v[10],reg.v[11],
              reg.v[12],reg.v[13],reg.v[14],reg.v[15]);
  }

  public void dumpTimers()
  {
    log.debug("DT={}, ST={}", reg.dt, reg.st);
  }

  public void dumpAllReg()
  {
    dumpI();
    dumpTimers();
    dumpV();
  }

  public void dumpSprite(int length, boolean condition)
  {
    if (! condition) return;
    for (int p=0; p < length; p++) {
      int bite = 0xFF & mem.at((short)(p+reg.i));
      log.debug(Integer.toBinaryString(bite));
    }
  }

  public void dumpSprite(int length)
  {
    dumpSprite(length,true);
  }
}
