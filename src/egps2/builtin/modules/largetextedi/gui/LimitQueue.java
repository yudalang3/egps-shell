package egps2.builtin.modules.largetextedi.gui;

import java.util.LinkedList;

/**
* @author YFQ
* @date 2019-07-18 17:05:28
* @version 1.0
* <p>Description:</p>
*/
public class LimitQueue<E>{
	 
    private int limit; // 队列长度
        
    private LinkedList<E> queue = new LinkedList<E>();
        
    public LimitQueue(int limit){    
        this.limit = limit;    
    }    
    
    /**
     * 入列：当队列大小已满时，把队头的元素poll掉
     */
    public void offer(E e){    
        if(queue.size() >= limit){    
            queue.poll();    
        }  
        queue.offer(e);    
    }    
    
    public E get(int position) {
    	return queue.get(position);
    }
    
    public E getLast() {
    	return queue.getLast();
    }
    
    public E getFirst() {
    	return queue.getFirst();
    }
    
    public int getLimit() {
    	return limit;
    }
    
    public int size() {    
        return queue.size();    
    }    
    
}
