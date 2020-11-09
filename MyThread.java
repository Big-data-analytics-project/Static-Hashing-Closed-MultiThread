import java.util.*;

public class MyThread<K,V> implements Runnable {
    String name;
    Thread t;
    ConcurrentStaticClosed hm;
    List<String> title;
    List<String> author;
    String operation;
    K key;
    List<Long> times = new ArrayList<>();

    public MyThread(){};

    public MyThread(String name, ConcurrentStaticClosed hm,  List<String> title,List<String> author ,String operation) {
        this.name = name;
        this.hm = hm;
        this.title = title;
        this.author = author;
        this.operation = operation;
    }

    public void run() {
        if (operation == "Insert") {
            for (int i = 0; i < title.size(); i++) {
                long start = System.nanoTime();
                hm.add(title.get(i), author.get(i));
                long finish = System.nanoTime();
                times.add(finish - start);
            }
        } else if (operation == "Search") {
            hm.get(key);
        }
    }

    public List<Long> getList(){
        return times;
    }

}

