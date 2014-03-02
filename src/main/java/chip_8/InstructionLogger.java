package chip_8;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author ckroetsc
 */
public class InstructionLogger
{
  private static Logger log = LoggerFactory.getLogger(InstructionLogger.class);

  public void name(String s)
  {
    log.info(s);
  }

  public void addr(String s, int addr)
  {
    log.info("{} {}",s,addr);
  }

  public void regAddr(String s, int reg, int addr)
  {
    log.info("{} V{}, {}",s,reg,addr);
  }

  public void regAddr(String s, String reg, int addr)
  {
    log.info("{} {}, {}",s,reg,addr);
  }

  public void regReg(String s, int r1, int r2)
  {
    log.info("{} V{}, V{}", s, r1, r2);
  }

  public void reg(String s, int r1)
  {
    log.info("{} V{}", s, r1);
  }

  public void regReg(String s, int r1, String r2)
  {
    log.info("{} V{}, {}", s, r1, r2);
  }

  public void regReg(String s, String r1, int r2)
  {
    log.info("{} {}, V{}", s, r1, r2);
  }

  public void regRegAddr(String s, int r1, int r2, int n)
  {
    log.info("{} V{}, V{}, {}", s, r1, r2, n);
  }
}
