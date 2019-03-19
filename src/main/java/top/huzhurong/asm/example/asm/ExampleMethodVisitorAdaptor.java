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

    //开始点和结束点，处理异常时使用
    private Label start = new Label();
    private Label end = new Label();
    //tag_interceptor 局部变量表下标
    private int tag_interceptor = 0;
    //局部变量表下标
    private int tag_ex = 0;
    //demo演示用，agent监控有其他用途，这里无
    private long key = 1;
    //当前局部变量表的大小，局部变量表主要是 0号位的this,和n号位的方法描述符表示，后续需要加上我们注入的字节码数据
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
        //代码  ---> Tag tag_interceptor = null;
        tag_interceptor = loadParam("tag_interceptor", "Ltop/huzhurong/asm/example/demo/Tag;");
        //加载1
        mv.visitLdcInsn(key);
        //调用TagMap的ff方法，入参为key ---> TagMap.ff(1);
        mv.visitMethodInsn(INVOKESTATIC, JvmName.toJvmName("top.huzhurong.asm.example.demo.TagMap"), "ff", "(J)Ltop/huzhurong/asm/example/demo/Tag;", false);
        //存储到局部变量表tag_interceptor位置,结合起来 --->Tag tag_interceptor = TagMap.ff(1);
        mv.visitVarInsn(ASTORE, tag_interceptor);

        //进入try块，这里遇到一个坑,作用域的大小问题，try块内部的变量是不能被外部变量访问的，写代码的时候知道，字节码的层面这里倒了个坑
        mv.visitLabel(start);
        //加载tag_interceptor出的变量到操作数栈顶
        mv.visitVarInsn(ALOAD, tag_interceptor);
        //插入执行方法的参数
        insertParameter();
        //调用接口方法
        mv.visitMethodInsn(INVOKEINTERFACE, "top/huzhurong/asm/example/demo/Tag", "enter", "(JLjava/lang/Object;[Ljava/lang/Object;)V", true);
    }

    /**
     * 退出方法，return的前一句code
     */
    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode);
        //如果是抛出异常，直接返回，不进入
        if (opcode == Opcodes.ATHROW) {
            return;
        }
        //加载tag_interceptor出的变量到操作数栈顶
        mv.visitVarInsn(ALOAD, tag_interceptor);
        insertParameter();
        mv.visitMethodInsn(INVOKEINTERFACE, "top/huzhurong/asm/example/demo/Tag", "out", "(JLjava/lang/Object;[Ljava/lang/Object;)V", true);
    }


    @Override
    public void visitEnd() {
        //try块结束的位置
        mv.visitLabel(end);
        //标明try块开始和结束的位置
        mv.visitTryCatchBlock(start, end, end, JvmName.toJvmName("java.lang.Throwable"));
        //栈顶部的异常拷贝一份再入栈
        dup();
        //加载异常
        tag_ex = loadParam("tag_ex", "Ljava/lang/Throwable;");
        //异常放入局部变量表
        mv.visitVarInsn(ASTORE, tag_ex);
        //参考上述方法
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
        //加载异常到栈顶
        mv.visitVarInsn(ALOAD, tag_ex);
        //取栈顶重新抛出异常
        mv.visitInsn(Opcodes.ATHROW);
        //方法结束
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

    /**
     * 添加一个局部变量，然后局部变量表的槽+1，
     *
     * @param name 变量名称
     * @param desc 描述符
     */
    private int loadParam(String name, String desc) {
        next++;
        //设置局部变量表的位置，这里还遇到了几个坑
        mv.visitLocalVariable(name, desc, null, start, end, next);
        return next;
    }

    //设置变量为null的情况，也需要占用一个局部变量表的槽
    void loadNull(int solt) {
        mv.visitInsn(ACONST_NULL);
        mv.visitVarInsn(ASTORE, solt);
    }
}
