import java.awt.Point;
import java.util.HashSet;

public class test {
    
    public static void main(String... args){
        GameNode p = new GameNode(1,0);
        GameNode q = new GameNode(0,1);

        HashSet<GameNode> h = new HashSet<GameNode>();

        h.add(p);

        if(h.contains(p)) System.out.println("contains p as expected");
        if(!h.contains(q)) System.out.println("not contains q as expected");
        if(h.contains(new GameNode(1,0))) System.out.println("contains same point but new");

        System.out.println(p.hashCode());
        System.out.println(q.hashCode());

        Point y = new Point(1,0);
        System.out.println(y.hashCode());



    }

}
