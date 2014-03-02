package chip_8;


import com.google.common.io.Files;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * @author ckroetsc
 */
public class Memory
{
  public static final int RAM_SIZE = 4096;
  private byte[] ram = new byte[RAM_SIZE];


  public short readInstruction(short pointer)
  {
    return Utils.concatBytes(ram[pointer],ram[pointer+1]);
  }

  /* Store the binary digits of a number in memory at I */
  public void storeBCD(int num, short location)
  {
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
