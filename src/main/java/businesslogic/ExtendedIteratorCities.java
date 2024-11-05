package businesslogic;

import java.util.List;

public class ExtendedIteratorCities<T> implements ExtendedIterator<T> {
    private List<T> list;
    private int position;

    public ExtendedIteratorCities(List<T> list) {
        this.list = list;
        this.position = 0;
    }

    @Override
    public boolean hasNext() {
        return position < list.size();
    }

    @Override
    public T next() {
        return list.get(position++);
    }

    @Override
    public T previous() {
        return list.get(--position);
    }

    @Override
    public boolean hasPrevious() {
        return position > 0;
    }

    @Override
    public void goFirst() {
        position = 0;
    }

    @Override
    public void goLast() {
        position = list.size() - 1;
    }
}
