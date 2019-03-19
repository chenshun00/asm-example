package top.huzhurong.asm.example.asm;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import top.huzhurong.asm.example.util.JvmName;

/**
 * @author chenshun00@gmail.com
 * @since 2019/3/19
 */
public class ExampleMethodVisitorAdaptor extends AdviceAdapter {


    private Label start = new Label();
    private Label end = new Label();
    private int tag_interceptor = 0;
    private int tag_ex = 0;
    private long key = 1;
    private int next = Type.getArgumentTypes(this.methodDesc).length;

    /**
     * Creates a new {@link AdviceAdapter}.
     *
     * @param api    the ASM API version implemented by this visitor. Must be one
     *               of {@link Opcodes#ASM4} or {@link Opcodes#ASM5}.
     * @param mv     the method visitor to which this adapter delegates calls.
     * @param access the method's access flags (see {@link Opcodes}).
     * @param name   the method's name.
     * @param desc   the method's descriptor (see {@link Type Type}).
     */
    protected ExampleMethodVisitorAdaptor(int api, MethodVisitor mv, int access, String name, String desc) {
        super(api, mv, access, name, desc);
    }


    //方法开始
    @Override
    public void visitCode() {
        super.visitCode();
        tag_interceptor = loadParam("tag_interceptor", "Ltop/huzhurong/asm/example/demo/Tag;");
        mv.visitLdcInsn(key);
        mv.visitMethodInsn(INVOKESTATIC, JvmName.toJvmName("top.huzhurong.asm.example.demo.TagMap"), "ff", "(J)Ltop/huzhurong/asm/example/demo/Tag;", false);
        mv.visitVarInsn(ASTORE, tag_interceptor);
        mv.visitLabel(start);
        mv.visitVarInsn(ALOAD, tag_interceptor);
        insertParameter();
        mv.visitMethodInsn(INVOKEINTERFACE, "top/huzhurong/asm/example/demo/Tag", "enter", "(JLjava/lang/Object;[Ljava/lang/Object;)V", true);
    }

    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode);
        if (opcode == Opcodes.ATHROW) {
            return;
        }
        mv.visitVarInsn(ALOAD, tag_interceptor);
        insertParameter();
        mv.visitMethodInsn(INVOKEINTERFACE, "top/huzhurong/asm/example/demo/Tag", "out", "(JLjava/lang/Object;[Ljava/lang/Object;)V", true);
    }


    @Override
    public void visitEnd() {
        mv.visitLabel(end);
        mv.visitTryCatchBlock(start, end, end, JvmName.toJvmName("java.lang.Throwable"));

        dup();
        tag_ex = loadParam("tag_ex", "Ljava/lang/Throwable;");
        mv.visitVarInsn(ASTORE, tag_ex);

        mv.visitVarInsn(ALOAD, tag_interceptor);
        mv.visitLdcInsn(key);
        mv.visitVarInsn(ALOAD, tag_ex);
        int size = Type.getArgumentTypes(this.methodDesc).length;
        if (size > 0) {
            loadArgArray();
        } else {
            mv.visitInsn(Opcodes.ACONST_NULL);
        }
        mv.visitMethodInsn(INVOKEINTERFACE, JvmName.toJvmName("top.huzhurong.asm.example.demo.Tag"), "error", "(JLjava/lang/Throwable;[Ljava/lang/Object;)V", true);

        mv.visitVarInsn(ALOAD, tag_ex);
        mv.visitInsn(Opcodes.ATHROW);

        mv.visitEnd();
    }


    private void insertParameter() {
        mv.visitLdcInsn(key);
        //注入非static方法 参考Modifier#isStatic
        if ((this.methodAccess & 0x00000008) == 0) {
            loadThis();
        } else {
            mv.visitInsn(Opcodes.ACONST_NULL);
        }
        int size = Type.getArgumentTypes(this.methodDesc).length;
        if (size > 0) {
            loadArgArray();
        } else {
            mv.visitInsn(Opcodes.ACONST_NULL);
        }
    }

    private int loadParam(String name, String desc) {
        next++;
        mv.visitLocalVariable(name, desc, null, start, end, next);
        return next;
    }

    void loadNull(int solt) {
        mv.visitInsn(ACONST_NULL);
        mv.visitVarInsn(ASTORE, solt);
    }
}
