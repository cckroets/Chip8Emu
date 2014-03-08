package chip_8;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author ckroetsc
 */
public class TestInstructions extends TestCase
{
    Display d = new Display();
    CPU cpu = new CPU(d,"invaders.rom");

  /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public TestInstructions(String testName)
  {
    super( testName );
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite()
  {
    return new TestSuite( TestInstructions.class );
  }

  public void testALU()
  {
    byte a = (byte)0xF0;
    byte b = (byte)0x0F;
    cpu.reg.v[0] = (byte)0xF0;
    cpu.reg.v[1] = (byte)0x0F;
    cpu.dispatchALU((byte)0,(byte)1,0x1);

    assertEquals((byte)0xFF,cpu.reg.v[0]);


  }
}
