### ASM

#### 入门，演示ASM注入字节码的例子

未注入前

```java
public class Hello {
    public String set(String info, List<String> infoList, User user) {
        System.out.println(info);
        System.out.println(infoList);
        System.out.println(user);
        return "hello,ASM";
    }
}
```

ASM注入后

```java
public class Hello implements Serializable {
    public Hello() {
    }

    public String set(String info, List<String> infoList, User user) {
        Tag tag_interceptor = TagMap.ff(1L);

        try {
            tag_interceptor.enter(1L, this, new Object[]{info, infoList, user});
            System.out.println(info);
            System.out.println(infoList);
            System.out.println(user);
            tag_interceptor.out(1L, this, new Object[]{info, infoList, user});
            return "hello,ASM";
        } catch (Throwable var6) {
            tag_interceptor.error(1L, var6, new Object[]{info, infoList, user});
            throw var6;
        }
    }
}
```

#### 进阶，agent监控

利用jdk1.5 和 1.6提供的 `ClassFileTransformer` 和 `Instrumentation` 进行监控.
可深入学习 `ClassLoader` 的加载，了解ASM在学习jvm上有一丁点优势.