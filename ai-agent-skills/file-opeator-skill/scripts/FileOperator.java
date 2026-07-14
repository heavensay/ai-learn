import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileOperator {
    public static void main(String[] args) {
        String command = args[0];
        String filePath = args[1];

        switch (command){
            case "read":
                readFile(filePath);
                break;
            default:
                System.out.println("不支持的操作");
        }
    }

    public static String readFile(String filePath){
        try {
             String content = Files.readString(Paths.get(filePath));
             System.out.println(content);
             return content;
        } catch (IOException e) {
            System.err.println("读取文件时发生错误: " + e.getMessage());
        }
    }
}