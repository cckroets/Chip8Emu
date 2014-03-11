package chip_8;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class TestIO extends TestCase
{
  private static final int TEST_I = 0x500;

  /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public TestIO(String testName)
  {
    super( testName );
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite()
  {
    return new TestSuite( TestMemory.class );
  }


  /**
   * Test loading and storing registers to memory
   */
  public void testRegIO() throws IOException
  {
    /* Load registers with random values */
    Registers reg = new Registers();
    reg.i = 0x522;
    reg.dt = 2;
    reg.st = 2;
    reg.pc = 433;
    reg.sp = 211;
    for (int i = 0; i < 16; i++)
      reg.setVx(i,i);

    /* Open streams for saving and loading state */
    PipedInputStream pin = new PipedInputStream();
    DataInputStream  in = new DataInputStream(pin);
    DataOutputStream out = new DataOutputStream(new PipedOutputStream(pin));

    /* Save the registers */
    reg.saveState(out);
    out.flush();

    /* Reset the registers */
    reg.reset();

    /* Load the registers */
    reg.loadState(in);
    in.close();

    /* Assert that the registers contain the same values */
    assertEquals(0x522, reg.i);
    assertEquals(433, reg.pc);
    assertEquals(211, reg.sp);
    assertEquals(2, reg.dt);
    assertEquals(2, reg.st);
    for (int i = 0; i < 16; i++) {
      assertEquals(i,reg.v(i));
    }
  }

}
