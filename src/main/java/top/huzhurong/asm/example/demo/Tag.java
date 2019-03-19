package top.huzhurong.asm.example.demo;

/**
 * @author chenshun00@gmail.com
 * @since 2019/3/19
 */
public interface Tag {
    void enter(long key, Object currentObject, Object[] args);

    void out(long key, Object currentObject, Object[] args);

    void error(long key, Throwable throwable, Object[] args);
}
