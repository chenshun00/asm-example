package top.huzhurong.asm.example.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author chenshun00@gmail.com
 * @since 2019/3/19
 */
public class ASMExampleVisitor extends ClassVisitor {

    /**
     * 修改字节码是否实现该接口，参考
     * {@link top.huzhurong.asm.example.ASMExample#main(String[])}
     */
    private Class interfaceClass;

    public ASMExampleVisitor(int api, ClassVisitor cv, Class interfaceClass) {
        super(api, cv);
        this.interfaceClass = interfaceClass;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if (interfaceClass == null) {
            super.visit(version, access, name, signature, superName, interfaces);
        } else {
            Set<String> set = new TreeSet<>(Arrays.asList(interfaces));
            //写入接口，参考Hello_Asm.class文件
            set.add(interfaceClass.getName().replace(".", "/"));
            String[] strings = set.toArray(new String[0]);
            super.visit(version, access, name, null, superName, strings);
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        //如果是构造器方法，直接返回
        if (name.equals("<init>")) {
            return cv.visitMethod(access, name, desc, signature, exceptions);
        }
        //否则进入适配器修改字节码
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        return new ExampleMethodVisitorAdaptor(Opcodes.ASM5, mv, access, name, desc);
    }
}
