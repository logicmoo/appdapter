package org.appdapter.gui.util;

import java.util.Collection;
import java.util.List;

public abstract class CollectionSetUtils {

	public interface TAccepts<S> {

		boolean isCompleteOn(S e);

		boolean resultOf(S e);

	}

	public static <T> boolean addIfNew(List<T> list, T element) {
		if (list.contains(element))
			return false;
		if (element instanceof HRKRefinement.DontAdd) {
			return false;
		}
		if (element instanceof HRKRefinement) {
			list.add(0, element);
			return true;
		}
		list.add(element);
		return true;
	}

	public static <T> boolean addIfNew(Collection<T> list, T element) {
		if (list.contains(element))
			return false;
		if (element instanceof HRKRefinement.DontAdd) {
			return false;
		}
		list.add(element);
		return true;
	}

	public static <T, ET> boolean addAllNew(List<T> list, ET[] elements) {
		boolean changed = false;
		for (Object t0 : elements) {
			T t;
			try {
				t = (T) t0;
			} catch (ClassCastException cce) {
				cce.printStackTrace();
				continue;
			}
			if (addIfNew(list, t))
				changed = true;
		}
		return changed;
	}

	public static <T, ET> boolean addAllNew(List<T> list, Iterable<ET> elements) {
		boolean changed = false;
		for (ET t0 : elements) {
			T t;
			try {
				t = (T) t0;
			} catch (ClassCastException cce) {
				cce.printStackTrace();
				continue;
			}
			if (addIfNew(list, t))
				changed = true;
		}
		return changed;
	}

	public static <T> boolean containsOne(T[] elements, TAccepts<T> e) {
		for (Object t0 : elements) {
			T t;
			try {
				t = (T) t0;
			} catch (ClassCastException cce) {
				cce.printStackTrace();
				continue;
			}
			if (e.isCompleteOn(t))
				return e.resultOf(t);
		}
		return false;
	}

	public static <T> T[] arrayOf(T... args) {
		return args;
	}
}