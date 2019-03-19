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
            set.add(interfaceClass.getName().replace(".", "/"));
            String[] strings = set.toArray(new String[0]);
            super.visit(version, access, name, null, superName, strings);
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (name.equals("<init>")) {
            return cv.visitMethod(access, name, desc, signature, exceptions);
        }
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        return new ExampleMethodVisitorAdaptor(Opcodes.ASM5, mv, access, name, desc);
    }
}
