package chip_8;


import com.google.common.io.Files;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;


/**
 * @author ckroetsc
 */
public class Memory
{
  public static final int RAM_SIZE = 4096;
  private byte[] ram = new byte[RAM_SIZE];
  private static File digits = Utils.getResourceFile("digits.bin");


  public Memory(String rom)
  {
    /* Load the digits font into memory */
    this.loadFile(digits, CPU.zero);
    this.loadFile(Utils.getRom(rom),CPU.START_OF_PROGRAM);
  }

  /* Reset the memory. Blank the loaded rom, but leave the loaded digits */
  public void reset()
  {
    int begin = CPU.START_OF_PROGRAM;
    int end = RAM_SIZE - begin;
    Arrays.fill(ram, begin, end, CPU.zero);
  }

  /* Read an instruction from memory at pointer */
  public short readInstruction(short pointer)
  {
    return Utils.concatBytes(ram[pointer],ram[pointer+1]);
  }

  /* Store the binary digits of a number in memory at I */
  public void storeBCD(int num, short location)
  {
    num &= 0xFF;
    int hun = (num / 100);
    int ten = ((num % 100) / 10);
    int one = ((num % 100) % 10);

    ram[location] = (byte)hun;
    ram[location+1] = (byte)ten;
    ram[location+2] = (byte)one;
  }

  /* Store [v[0],v[x]] at I in memory */
  public void store(int x, Registers reg) {
    // Source: v[0], Dest: ram[i], length: x
    System.arraycopy(reg.v,0,ram,reg.i,x+1);
  }

  /* Load memory at I into [v[0],v[x]] */
  public void load(int x, Registers reg)
  {
    // Source: ram[i], Dest: v[0], length: x
    System.arraycopy(ram,reg.i,reg.v,0,x+1);
  }

  /* Get byte at index in memory */
  public byte at(short index)
  {
    return ram[index];
  }

  /* Load bytes from a file into memory at the given location */
  public void loadFile(File file, short location)
  {
    byte[] bytes;

    try {
      int len = (int)file.length();
      bytes = Files.toByteArray(file);
      System.arraycopy(bytes,0,ram,location,len);
    /* Handle File Not Found */
    } catch (FileNotFoundException e) {
      System.err.print("File not found!");
    /* Handle IO Exception */
    } catch (IOException e) {
      System.err.print("Cannot read File!");
    }
  }

}
