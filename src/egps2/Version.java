package egps2;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version {
    public String toString() {
        return getVersionFromAboutHtml();
    }
    
    /**
     * 从 about.html 文件中读取版本号
     */
    private String getVersionFromAboutHtml() {
        try {
            // 获取 about.html 的 URL
            URL url = getClass().getResource("/egps2/frame/html/about.html");
            if (url != null) {
                // 读取文件内容
                try (InputStream inputStream = url.openStream();
                     Scanner scanner = new Scanner(inputStream, "UTF-8")) {
                    String content = scanner.useDelimiter("\\A").next();
                    
                    // 使用正则表达式匹配版本号
                    Pattern pattern = Pattern.compile("Version:\\s*(\\d+\\.\\d+\\.\\d+)");
                    Matcher matcher = pattern.matcher(content);
                    
                    if (matcher.find()) {
                        return matcher.group(1); // 返回匹配到的版本号
                    }
                }
            }
        } catch (IOException | java.util.NoSuchElementException e) {
            // 如果读取失败，返回默认版本号
            System.err.println("Warning: Could not read version from about.html, using default");
        }
        
        // 如果无法从 about.html 读取版本号，则返回默认值
        return "2.1.97";
    }
}