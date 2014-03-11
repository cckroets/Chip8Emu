package chip_8;


import Emulation.Hardware;
import com.google.common.io.Files;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;


/**
 * @author ckroetsc
 */
public class Memory implements Hardware
{
  public static final int RAM_SIZE = 4096;
  private byte[] ram = new byte[RAM_SIZE];
  private static File digits = Utils.getResourceFile("digits.bin");


  public Memory()
  {
    /* Load the digits font into memory */
    this.loadFile(digits, Chip8Processor.zero);
  }

  @Override
  public void saveState(DataOutputStream out)
      throws IOException
  {
    System.out.println("memSAVE");
    out.write(ram);
  }

  @Override
  public void loadState(DataInputStream in)
      throws IOException
  {
    System.out.println("memLOAD");
    if (in.read(ram) != RAM_SIZE)
      throw new IOException();
  }

  /* Reset the memory. Blank the loaded rom, but leave the loaded digits */
  @Override
  public void reset()
  {
    int begin = Chip8Processor.START_OF_PROGRAM;
    int end = RAM_SIZE - begin;
    Arrays.fill(ram, begin, end, Chip8Processor.zero);
  }

  /* Read an instruction from memory at pointer */
  public short readInstruction(int pointer)
  {
    return Utils.concatBytes(ram[pointer],ram[pointer+1]);
  }

  /* Store the binary digits of a number in memory at I */
  public void storeBCD(int num, int location)
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
    System.arraycopy(reg.getV(),0,ram,reg.i,x+1);
  }

  /* Load memory at I into [v[0],v[x]] */
  public void load(int x, Registers reg)
  {
    // Source: ram[i], Dest: v[0], length: x
    System.arraycopy(ram,reg.i,reg.getV(),0,x+1);
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
