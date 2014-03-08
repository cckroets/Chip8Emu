package chip_8;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class TestMemory extends TestCase
{
  private static final int TEST_I = 0x500;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TestMemory(String testName)
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
    public void testMemoryReadWrite()
    {
      /* Create blank Memory and registers */
      Memory m = new Memory("pong2.rom");
      Registers reg = new Registers();
      m.reset();

      /* Set I to an arbitrary location */
      reg.i = TEST_I;

      /* Fill  v0-v15 with their own index */
      for (byte v=0; v < 16; v++) {
        reg.v[v] = v;
      }

      /* Store the registers to memory */
      m.store(15,reg);

      /* Reset the V registers all to zero */
      reg.reset();
      reg.i = TEST_I;

      /* Load the registers back from memory */
      m.load(15,reg);

      /* Assert the V registers have their correct value */
      for (byte v=0; v < 16; v++) {
        assertEquals(v,reg.v[v]);
      }
    }

}
