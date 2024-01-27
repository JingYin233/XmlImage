import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Main {
    public static void main(String[] args) {
        String date = "2024年1月18日，15:07:12 ";  // 后面有一个空格

        // 移除字符串两端的空格
        date = date.trim();

        // 定义日期和时间的格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年M月d日，H:m:s");

        try {
            // 尝试解析日期和时间
            LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
            System.out.println("解析成功: " + dateTime);
        } catch (DateTimeParseException e) {
            // 如果解析失败，抛出一个异常
            throw new IllegalArgumentException("时间格式不对", e);
        }
    }
}
