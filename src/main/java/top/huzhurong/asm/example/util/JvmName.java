package top.huzhurong.asm.example.util;

/**
 * @author chenshun00@gmail.com
 * @since 2019/3/19
 */
public class JvmName {
    public static String toJvmName(String name) {
        return name.replaceAll("\\.", "/");
    }
}
