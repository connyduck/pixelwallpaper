package at.connyduck.pixelwallpaper;

import java.io.Serializable;

public class IntList implements Serializable {

    private static final int MIN_CAPACITY_INCREMENT = 12;
    private static final long serialVersionUID = -4046407753817880014L;

    private int size;
    private int[] array;


    public IntList(int initialSize) {
        this.size = 0;
        this.array = new int[initialSize];
    }


    public void add(int x) {
        if (size == array.length) {
            int[] newArray = new int[array.length + MIN_CAPACITY_INCREMENT];
            System.arraycopy(array, 0, newArray, 0, size);
            array = newArray;
        }
        array[size] = x;
        size = size + 1;
    }

    public void set(int index, int x) {
        if(index < 0 || index >= size ) {
            return;
        }

        array[index] = x;

    }

    public int get(int index) {
        if(index < 0 || index >= size ) {
            return 0;
        }
        return array[index];
    }


}
