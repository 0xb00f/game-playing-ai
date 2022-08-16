import java.util.Comparator;
import java.util.PriorityQueue;

public class testq {

    public class Comper implements Comparator<Integer> {

        public int compare(Integer a, Integer b) {
    
            return a.compareTo(b); //??
            
        }
    
    }

    public static void main(String[] args) {
        
        testq t = new testq();
        PriorityQueue<Integer> q = new PriorityQueue<>(t.new Comper());

        q.add(1);
        q.add(3);
        q.add(2);

        while(!q.isEmpty()) System.out.println(q.poll());


    }
    
}
