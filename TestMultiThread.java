import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TestMultiThread {
	
	public static void writeInsertPerformance(String filename) throws IOException, InterruptedException {
		ConcurrentStaticClosed<String, String> shc = new ConcurrentStaticClosed<String, String>(20000);
        ArrayList<String> keys = new ArrayList<String>();
        ArrayList<String> values = new ArrayList<String>();
        Scanner reader = new Scanner(new File("911.csv"));
        reader.nextLine();
        String sep = ",";

        while(reader.hasNextLine()) {
            String [] line = reader.nextLine().split(sep);
            keys.add(line[2]);
            values.add(line[4]);
        }
        
        ArrayList<Long> times = new ArrayList<>();
    	
    	for(int i=5000;i<keys.size();i+=100000) {
    		List<String> keysSub = keys.subList(0, i);
    		List<String> valuesSub = values.subList(0, i);
    		int size = keysSub.size();
            
    		
            List<String> key1 = keysSub.subList(0,(int)size/3);
            List<String> key2 = keysSub.subList((int)size/3,(int) 2*size/3);
            List<String> key3 = keysSub.subList((int) 2*size/3,size);

            List<String> value1 = valuesSub.subList(0,(int)size/3);
            List<String> value2 = valuesSub.subList((int)size/3 + 1,(int) 2*size/3);
            List<String> value3 = valuesSub.subList((int) 2*size/3,size);

            
            MyThread m1 = new MyThread("1", shc, key1, value1, "Insert");
            Thread t1 = new Thread(m1);
            Thread t2 = new Thread(new MyThread("2", shc, key2, value2 ,"Insert"));
            Thread t3 = new Thread(new MyThread("3", shc, key3, value3 ,"Insert"));
            
            long start = System.nanoTime();
            t1.start();
            t2.start();
            t3.start();

            t1.join();
            t2.join();
            t3.join();
            long finish = System.nanoTime();
            
            times.add(finish - start);
            
            t1.interrupt();
            t2.interrupt();
            t3.interrupt();
    	}
    	
    	
    	PrintWriter pw=new PrintWriter(new FileWriter(filename));
	    pw.println(times.toString());
		pw.close();
    }
    
	
    public static void writeAccessPerformance(String filename) throws FileNotFoundException, InterruptedException {
    	ConcurrentStaticClosed<String, String> shc = new ConcurrentStaticClosed<String, String>(5000);
        ArrayList<String> keys = new ArrayList<String>();
        ArrayList<String> values = new ArrayList<String>();
        Scanner reader = new Scanner(new File("911.csv"));
        reader.nextLine();
        String sep = ",";

        while(reader.hasNextLine()) {
            String [] line = reader.nextLine().split(sep);
            keys.add(line[2]);
            values.add(line[4]);
            shc.add(line[2], line[4]);
        }
        
        ArrayList<Long> times = new ArrayList<>();
        
        for(int i=5000;i<keys.size();i+=100000) {
    		List<String> keysSub = keys.subList(0, i);
    		List<String> valuesSub = values.subList(0, i);
    		int size = keysSub.size();
            
    		
            List<String> key1 = keysSub.subList(0,(int)size/3);
            List<String> key2 = keysSub.subList((int)size/3,(int) 2*size/3);
            List<String> key3 = keysSub.subList((int) 2*size/3,size);
            
            List<String> value1 = valuesSub.subList(0,(int)size/3);
            List<String> value2 = valuesSub.subList((int)size/3 + 1,(int) 2*size/3);
            List<String> value3 = valuesSub.subList((int) 2*size/3,size);
            
            MyThread m1 = new MyThread("1", shc, key1, value1, "Search");
            Thread t1 = new Thread(m1);
            Thread t2 = new Thread(new MyThread("2", shc, key2, value2 ,"Search"));
            Thread t3 = new Thread(new MyThread("3", shc, key3, value3 ,"Search"));
            
            long start = System.nanoTime();
            t1.start();
            t2.start();
            t3.start();

            t1.join();
            t2.join();
            t3.join();
            long finish = System.nanoTime();
            
            times.add(finish - start);
            
            t1.interrupt();
            t2.interrupt();
            t3.interrupt();
    	}
    }
    
    
    
    public static void main(String[] args) throws InterruptedException, IOException {

       writeInsertPerformance("insertMulti_time.txt");
       //writeAccessPerformance("insertMulti_time.txt");

        
    }

}
