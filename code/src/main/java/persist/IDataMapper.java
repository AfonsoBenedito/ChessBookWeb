package persist;

import java.util.Optional;

interface IDataMapper<E> {
    public Optional<E> find(int id);
    public void update(E e);
    public boolean remove(E e);
    public int insert(E e);
}