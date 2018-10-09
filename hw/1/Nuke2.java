import java.io.*;

public class Nuke2 {
  public static void main(String[] arg) throws Exception {
    BufferedReader keybd = new BufferedReader(new InputStreamReader(System.in));
    String s = keybd.readLine();
    String pos = s.substring(2);
    String pre = Character.toString(s.charAt(0));
    System.out.println(pre + pos);
  }
}
