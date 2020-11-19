package nir.util.getcallerclass;

public class ThrowableStackClassMethod extends GetCallerClassNameMethod {

    public String getCallerClassName(int callStackDepth) {
        return new Throwable().getStackTrace()[callStackDepth].getClassName();
    }

    public String getMethodName() {
        return "Throwable StackTrace";
    }
}