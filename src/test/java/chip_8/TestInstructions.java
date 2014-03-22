package chip_8;

import Emulation.Rom;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author ckroetsc
 */
public class TestInstructions extends TestCase
{
  Chip8Processor cpu = new Chip8Processor();
  Display d = new Display(cpu);

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


  public void testFoo()
  {
    byte b = (byte)0x80;
    byte c = (byte)0x01;

    byte result = (byte)(b + c);
    assertEquals(0x81,result);


  }
}
