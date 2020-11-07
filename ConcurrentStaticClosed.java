import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;



public class ConcurrentStaticClosed<K,V>{
	// A thread-safe version of hashtable
	
	
	private class Node {
		K key;
		V value;
		
		public Node (K key, V value) {
			this.key = key;
			this.value = value;
		}
		
		public String toString() {
			return "("+key.toString()+":"+value.toString()+")";
		}
		
	}
	
	private CopyOnWriteArrayList<LinkedList<Node>> buckets;
	private int nBuckets;
	private AtomicInteger size;
	
	public ConcurrentStaticClosed (int nbuckets) {
		//Initializer
		ArrayList<LinkedList<Node>> buckets_copy = new ArrayList<>(nbuckets);
		this.buckets = new CopyOnWriteArrayList<>(buckets_copy);
		this.size = new AtomicInteger(0);
		this.nBuckets = nbuckets;
		for(int i=0; i<this.nBuckets; i++) 
			this.buckets.add(null);
	}
	
	
	private int hashCode(K key)
	{
		return key.hashCode();
	}
	
	
	private int getIndex(K key)
	{
		int hashCode = hashCode(key);
		return hashCode % nBuckets; 
	}
	
	public V get (K key) {
		
		int index = getIndex(key);//get the index to access the bucket
		LinkedList<Node> nodes_list = buckets.get(index);//gets the bucket with K key
		
		if (nodes_list!=null) {
			ListIterator<Node> it = nodes_list.listIterator();//iterates over the linked list of Nodes
			boolean find = false;
			V value = null;
			while(it.hasNext() && !find) { //Try to find the key
				Node n = it.next();
				if(n.key == key) {//We've found it!
					value = n.value;
					find = true;
					}
				}
			
		return value;
		}
		return null;
	}
	
	public void set (K key,V value) {
		int index = getIndex(key);
		LinkedList<Node> nodes_list = buckets.get(index);
		
		if(nodes_list != null) {//Check if bucket is not empty
			ListIterator<Node> it = nodes_list.listIterator();
			boolean find = false;
			while(it.hasNext() && !find) {
				Node n = it.next();
				if(n.key == key) {
					n.value = value;
					find = true;
				}
			}
		}
	}
	
	public void add (K key, V value) {
		int index = getIndex(key);
		//we can add indefinitely, so there's no need to check overflow
		if(buckets.get(index) == null) {
			LinkedList<Node> nodes_list = new LinkedList<>();
			nodes_list.add(new Node(key, value));
			buckets.set(index, nodes_list);
	
		}else {
			LinkedList<Node> nodes_list = buckets.get(index);
			nodes_list.add(new Node(key, value));
			buckets.set(index, nodes_list);
		}
		this.size.incrementAndGet();
	}
	
		
	public int size () {
		return this.size.get();
	}
	
	public V remove(K key)
	{
		int index = getIndex(key);
		LinkedList<Node> nodes_list = buckets.get(index);
		
		if(nodes_list != null) { //Check if nodes_list is null before creating iterator such case is when bucket doesn't exist/ key doesn't exist
		ListIterator<Node> it = nodes_list.listIterator();

		while(it.hasNext())
		{
			Node n = it.next();
			if( n.key == key) {// If we find we send it and decrease the size
				V val = n.value;
				it.remove();
				this.size.getAndDecrement();
				return val;
			}
		}
	}
	return null;
	}
	
	
	public static void main(String[] args) {
		//test cases remove empty, remove first node, remove last node.
		//add indefinitely, set values, check size
		//check initializer
		//remove twice 
		//remove enough to empty list
		long before = System.currentTimeMillis();
		ConcurrentStaticClosed<String, Integer>hash = new ConcurrentStaticClosed<>(10);
		hash.add("Example1", 1);
		//test set with wrong key and set with correct key
		hash.set("Example2",2);
		hash.set("Example1",25);
		//Test get with wrong and correct key and check if they have already changed
		System.out.println(hash.get("2"));
		System.out.println(hash.get("Example1"));
		
		// Add another element
		hash.add("Example2", 2);
		System.out.println(hash.size());
		//Check if remove correctly
		Integer elem = hash.remove("Example1");
		System.out.println(elem);
		System.out.println("Look below, if size has decreased");
		System.out.println(hash.size());
		//Makes the list empty
		elem = hash.remove("Example1");
		System.out.println(elem);
		//Try to remove an already removed element
		elem = hash.remove("Example2");
		System.out.println(hash.size);
		
		System.out.println("Execution time: " + (System.currentTimeMillis() - before));
		
			}

}


