package nir.util.getcallerclass;

public class SecurityManagerMethod extends GetCallerClassNameMethod {
    public String  getCallerClassName(int callStackDepth) {
        return mySecurityManager.getCallerClassName(callStackDepth);
    }

    public String getMethodName() {
        return "SecurityManager";
    }

    /**
     * A custom security manager that exposes the getClassContext() information
     */
    static class MySecurityManager extends SecurityManager {
        public String getCallerClassName(int callStackDepth) {
            return getClassContext()[callStackDepth].getName();
        }
    }

    private final static MySecurityManager mySecurityManager =
            new MySecurityManager();
}