import java.util.LinkedList;
import java.util.Map;
import java.util.Hashtable;

public class Moves {
    public Map<Integer,LinkedList<Integer>> map;

    public Moves() {
        map = new Hashtable<Integer,LinkedList<Integer>>();
    }

    public Moves(Map<Integer,LinkedList<Integer>> m) {
        System.out.println("iterator constructor");
        map = m;
    }

    public int at(int i) {
        for (int j = 0; j < 64; j++) {
            if (get(j) != null) {
                if (get(j).size() > i) {
                    return get(j).get(i);
                }
                else i -= get(j).size();
            }
        }
        return 0;
    }

    public Move atMove(int i) {
        for (int j = 0; j < 64; j++) {
            if (get(j) != null) {
                if (get(j).size() > i) {
                    return new Move(j,(int) get(j).get(i));
                }
                else i -= get(j).size();
            }
        }
        return new Move(0,0);
    }

    public LinkedList<Integer> get(int i) {
        return map.get(i);
    }

    public void put(int i, LinkedList<Integer> l) {
        map.put(i,l);
    }
    
    public void clear() {
        map.clear();
    }

    public boolean containsKey(int i) {
        return map.containsKey(i);
    }

    public int size() {
        int[] size = {0}; // must be array to bypass the lambda expression limitations.  Is effectivly an int
        map.forEach((key,list) -> size[0] += list.size());
        return size[0];
    }
}
