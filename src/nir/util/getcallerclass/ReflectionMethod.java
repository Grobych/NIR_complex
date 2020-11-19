package nir.util.getcallerclass;

public class ReflectionMethod extends GetCallerClassNameMethod {
    public String getCallerClassName(int callStackDepth) {
        return sun.reflect.Reflection.getCallerClass(callStackDepth).getName();
    }

    public String getMethodName() {
        return "Reflection";
    }
}