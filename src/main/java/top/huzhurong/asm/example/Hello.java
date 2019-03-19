package top.huzhurong.asm.example;

import java.util.List;

/**
 * @author chenshun00@gmail.com
 * @since 2019/3/19
 */
public class Hello {
    public String set(String info, List<String> infoList, User user) {
        System.out.println(info);
        System.out.println(infoList);
        System.out.println(user);
        return "hello,ASM";
    }
}
