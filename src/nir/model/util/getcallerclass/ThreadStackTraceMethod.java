package nir.model.util.getcallerclass;

public class ThreadStackTraceMethod extends GetCallerClassNameMethod {
    public String  getCallerClassName(int callStackDepth) {
        return Thread.currentThread().getStackTrace()[callStackDepth].getClassName();
    }

    public String getMethodName() {
        return "Current Thread StackTrace";
    }
}