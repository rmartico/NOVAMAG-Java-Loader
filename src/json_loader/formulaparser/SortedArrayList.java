package json_loader.formulaparser;

import java.util.ArrayList;
import java.util.Collections;

/**
 * SortedArrayList.java
 *  Class to represent a sorted array list
 *  It is used to store the pairs (element, number of occurrences of that element)
 *  in a molecule
 *  
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @version 1.0
 * @since 1.0 
 */
class SortedArrayList<T> extends ArrayList<T> {

	private static final long serialVersionUID = 1L;

    /**
	 * 
	 * Method to insert a new element in the sorted array list
	 * 
	 */
	@SuppressWarnings("unchecked")
    public void insertSorted(T value) {
        add(value);
        Comparable<T> cmp = (Comparable<T>) value;
        for (int i = size()-1; i > 0 && cmp.compareTo(get(i-1)) < 0; i--)
            Collections.swap(this, i, i-1);
    }
}
