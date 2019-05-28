package com.pixurvival.gdxcore.util;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class TruncatingQueue<E> {

	private E[] array;
	/**
	 * Index (inclusive) of the oldest entry in the historyArray
	 */
	private int tail;
	/**
	 * Index (exclusive) of the newest entry in the historyArray
	 */
	private int head;

	private int size;

	@SuppressWarnings("unchecked")
	public TruncatingQueue(int size) {
		// array = ArrayReflection.
		array = (E[]) new Object[size];
		tail = head = 0;
	}

	public void push(E element) {
		array[head] = element;
		if (tail == head && size > 0) {
			head = tail = nextIndex(head);
		} else {
			head = nextIndex(head);
			size++;
		}
	}

	public void forEachFromHead(Predicate<E> action) {

		int index = previousIndex(head);
		for (int i = 0; i < size; i++) {
			if (action.test(array[index])) {
				return;
			}
			index = previousIndex(index);
		}
	}

	/**
	 * @param start
	 *            Start index (inclusive) relative to tail position
	 * @param end
	 *            End index (exclusive) relative to tail position
	 * @param action
	 */
	public void forEachRange(int start, int length, Consumer<E> action) {
		if (isEmpty()) {
			return;
		}
		if (start < 0 || length < 0) {
			throw new IllegalArgumentException();
		}
		if (start >= size || start + length > size) {
			throw new IndexOutOfBoundsException();
		}
		int startIndex = tail + start;
		for (int i = 0; i < length; i++) {
			action.accept(array[(startIndex + i) % array.length]);
		}
	}

	public void forEach(Consumer<E> action) {
		forEachRange(0, size, action);
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	private int previousIndex(int index) {
		int result = index - 1;
		if (result < 0) {
			return array.length - 1;
		} else {
			return result;
		}
	}

	private int nextIndex(int index) {
		return (index + 1) % array.length;
	}
}
