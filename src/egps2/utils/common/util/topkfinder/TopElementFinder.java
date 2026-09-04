package egps2.utils.common.util.topkfinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * 这个类的作用是根据Comparator保留满足条件的Top元素。
 * 使用方式为不断addOneElement，类会自动保留Top K元素，这样不至于把所有需要排序的对象都存在内存中。
 * 被舍弃的元素自动回收。
 * 
 * @implSpec 1. 这个类是懒加载的，即只有在调用getFinalElements时才会加载。2. 用JDK自带的PriorityQueue实现，它就是用了堆这个数据结构。
 * <p>
 * 这个类的目的是减少内存的占用。
 * @author yudalang
 *
 * @param <T>
 */
public class TopElementFinder<T> {

	private final PriorityQueue<T> minHeap;
	private final int k;
	private int numOfFirstElements = 0;
	private final Comparator<T> comparator;

	public TopElementFinder(Comparator<T> comparator, int k) {
		minHeap = new PriorityQueue<>(comparator);
		this.comparator = comparator;
		this.k = k;
	}

	public void addOneElement(T ele) {
		if (numOfFirstElements < k) {
			minHeap.add(ele);
			numOfFirstElements++;
		} else {
			if (comparator.compare(minHeap.peek(), ele) > 0) {
				// nothing to do
			} else {
				minHeap.poll();
				minHeap.add(ele);
			}
		}
	}

	public List<T> getFinalElements() {
		List<T> ret = new ArrayList<>();
		Iterator<T> iterator = minHeap.iterator();
		while (iterator.hasNext()) {
			ret.add(iterator.next());
		}
		// 重要操作要sort一下
		Collections.sort(ret, comparator);
		// 这是一个重要的操作要reverse一下
		Collections.reverse(ret);
		return ret;
	}

	public static void firstElements(int arr[], int size, int k) {

		// Creating Min Heap for given
		// array with only k elements
		// Create min heap with priority queue
		PriorityQueue<Integer> minHeap = new PriorityQueue<>();
		for (int i = 0; i < k; i++) {
			minHeap.add(arr[i]);
		}

		// Loop For each element in array
		// after the kth element
		for (int i = k; i < size; i++) {

			// If current element is smaller
			// than minimum ((top element of
			// the minHeap) element, do nothing
			// and continue to next element
			if (minHeap.peek() > arr[i])
				continue;

			// Otherwise Change minimum element
			// (top element of the minHeap) to
			// current element by polling out
			// the top element of the minHeap
			else {
				minHeap.poll();
				minHeap.add(arr[i]);
			}
		}

		// Now min heap contains k maximum
		// elements, Iterate and print
		Iterator<Integer> iterator = minHeap.iterator();

		while (iterator.hasNext()) {
			System.out.print(iterator.next() + " ");
		}
	}

	public static void main1(String[] args) {
		{
			// 得到最小的四个值

			Comparator<Integer> comp = new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					return o2.compareTo(o1);
				}
			};
			int arr[] = { 11, 3, 2, 1, 15, 5, 4, 45, 88, 96, 50, 45 };
			int k = 4;
			TopElementFinder<Integer> topElementFinder = new TopElementFinder<>(comp, k);

			for (int i : arr) {
				topElementFinder.addOneElement(i);
			}

			List<Integer> finalElements = topElementFinder.getFinalElements();

			System.out.println(finalElements);

		}
		// 得到最大的四个值
		{
			Comparator<Integer> comp2 = new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					return o1.compareTo(o2);
				}
			};
			int arr[] = { 11, 3, 2, 1, 15, 5, 4, 45, 88, 96, 50, 45 };
			int k = 4;
			TopElementFinder<Integer> topElementFinder = new TopElementFinder<>(comp2, k);

			for (int i : arr) {
				topElementFinder.addOneElement(i);
			}

			List<Integer> finalElements = topElementFinder.getFinalElements();

			System.out.println(finalElements);

		}
	}

	public static void main(String[] args) {
		int arr[] = { 11, 3, 2, 1, 15, 5, 4, 45, 88, 96, 50, 45, 5, 5, 5, 6, 6, 6, 6, 5, 5, 96, 96, 96, 6 };
		int k = 3;
		{
			// 得到最小的四个值

			Comparator<Integer> comp = new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					return o2.compareTo(o1);
				}
			};
			TopElementFinder<Integer> topElementFinder = new TopElementFinder<>(comp, k);

			for (int i : arr) {
				topElementFinder.addOneElement(i);
			}

			List<Integer> finalElements = topElementFinder.getFinalElements();

			System.out.println(finalElements);

		}
		// 得到最大的四个值
		{
			Comparator<Integer> comp2 = new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					return o1.compareTo(o2);
				}
			};
			TopElementFinder<Integer> topElementFinder = new TopElementFinder<>(comp2, k);

			for (int i : arr) {
				topElementFinder.addOneElement(i);
			}

			List<Integer> finalElements = topElementFinder.getFinalElements();

			System.out.println(finalElements);

		}
	}
}
