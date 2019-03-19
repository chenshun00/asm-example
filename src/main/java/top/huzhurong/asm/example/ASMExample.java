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
        ClassReader classReader = new ClassReader("top.huzhurong.asm.example.Hello");
        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
        ClassVisitor classVisitor = new ASMExampleVisitor(Opcodes.ASM5, classWriter, Serializable.class);
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);

        FileOutputStream fileOutputStream = new FileOutputStream("Hello_ASM.class");
        fileOutputStream.write(classWriter.toByteArray());
        fileOutputStream.flush();
        fileOutputStream.close();
    }
}
