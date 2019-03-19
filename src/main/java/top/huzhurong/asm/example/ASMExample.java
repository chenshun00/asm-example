package top.huzhurong.asm.example;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import top.huzhurong.asm.example.asm.ASMExampleVisitor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author chenshun00@gmail.com
 * @since 2019/3/19
 */
public class ASMExample {
    public static void main(String[] args) throws IOException {
        //ClassReader读取字节码，可以通过多种方式进行读取，具体看doc
        ClassReader classReader = new ClassReader("top.huzhurong.asm.example.Hello");
        //asm/字节码框架修改过之后的字节码
        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
        //visit模式
        ClassVisitor classVisitor = new ASMExampleVisitor(Opcodes.ASM5, classWriter, Serializable.class);
        //accept，然后对读取对数据进行修改和处理，从这里开始
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);

        //将操作完成之后对字节，写入文件当中，这里可以自定义classloader去加载这个类，然后运行
        FileOutputStream fileOutputStream = new FileOutputStream("Hello_ASM.class");
        fileOutputStream.write(classWriter.toByteArray());
        fileOutputStream.flush();
        fileOutputStream.close();
    }
}
