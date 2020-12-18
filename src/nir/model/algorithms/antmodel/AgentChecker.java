package nir.model.algorithms.antmodel;

public class AgentChecker {
    private static int n = 0;
    private static int i = 0;
    public AgentChecker(int n){
        this.n = n;
        this.i = 0;
    }
    public static void setN(int number){
        n = number;
    }

    public static int getI() {
        return i;
    }

    public static boolean isDone(){
        return i == n;
    }
    synchronized static public void check(){
        if (isDone()) return;
        else {
            i++;
        }
    }

    public static void reset(){
        i = 0;
    }
}
