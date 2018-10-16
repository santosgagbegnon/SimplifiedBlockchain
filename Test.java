import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.StandardOpenOption;


import java.util.ArrayList;
public class Test{
    public static void main(String[] args){
        Path file = Paths.get("newfile.txt");
        ArrayList<String> lines = new ArrayList<>();
        lines.add("this");
        lines.add("is");
        lines.add("z");
        lines.add("test");
        
        try{
                    Files.write(file,lines, StandardOpenOption.APPEND);

        }
        catch(IOException e){
                    lines.add("no");


        }
        
    }
}