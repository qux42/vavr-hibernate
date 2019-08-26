package io.vavr.hibernate.userstype;

import io.vavr.Tuple;
import io.vavr.collection.List;
import io.vavr.collection.Traversable;
import io.vavr.control.Option;
import lombok.experimental.Delegate;
import org.hibernate.HibernateException;
import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.loader.CollectionAliases;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * @author cbongiorno on 7/5/17.
 */
//@EqualsAndHashCode(of = "delegate")
public class PersistentList<T> extends AbstractPersistentCollection implements List<T> {

    @Delegate
    private List<T> delegate = List.empty();


    @Delegate(excludes = {List.class})
    private Traversable<T> getDelegate() {
        return delegate;
    }


    public PersistentList(SharedSessionContractImplementor session) {
        super(session);
    }

    public PersistentList(SharedSessionContractImplementor session, List delegate) {
        super(session);
        this.delegate = delegate;

    }

    @Override
    public boolean empty() {
        return delegate.isEmpty();
    }

    @Override
    public Collection getOrphans(Serializable snapshot, String entityName) throws HibernateException {
        return null;
    }

    @Override
    public void initializeFromCache(CollectionPersister persister, Serializable disassembled,
                                    Object owner) {
        System.out.println();
    }

    @Override
    public Iterator entries(CollectionPersister persister) {
        return delegate.iterator();
    }

    private transient List<Tuple> loadingEntries;

    @Override
    public Object readFrom(ResultSet rs, CollectionPersister role, CollectionAliases descriptor,
                           Object owner) throws HibernateException, SQLException {

        final Object element = role
                .readElement(rs, owner, descriptor.getSuffixedElementAliases(), getSession());
        if (element != null) {
            final Object index = role.readIndex(rs, descriptor.getSuffixedIndexAliases(), getSession());
            loadingEntries = Option.of(loadingEntries).filter(Objects::nonNull).getOrElse(List.empty());
            loadingEntries = loadingEntries.append(Tuple.of(index, element));
        }
        return element;
    }

    @Override
    public Object getIndex(Object entry, int i, CollectionPersister persister) {
        return delegate.get(i);
    }

    @Override
    public Object getElement(Object entry) {
        return entry;
    }

    @Override
    public Object getSnapshotElement(Object entry, int i) {
        // supposed to copy this element.
        return entry;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void beforeInitialize(CollectionPersister persister, int anticipatedSize) {
        this.delegate = (List<T>) persister.getCollectionType().instantiate(anticipatedSize);

    }

    @Override
    public boolean equalsSnapshot(CollectionPersister persister) {
        return false;
    }

    @Override
    public boolean isSnapshotEmpty(Serializable snapshot) {
        return ((List) snapshot).isEmpty();
    }

    @Override
    public Serializable disassemble(CollectionPersister persister) {
        return delegate.map(v -> persister.getElementType().disassemble(v, getSession(), null));
    }

    @Override
    public Serializable getSnapshot(CollectionPersister persister) {
        return delegate.map(v -> persister.getElementType().deepCopy(v, persister.getFactory()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean entryExists(Object entry, int i) {
        return delegate.contains((T) entry);
    }

    @Override
    public boolean needsInserting(Object entry, int i, Type elemType) {
        return !delegate.get(i).equals(entry);
    }

    @Override
    public boolean needsUpdating(Object entry, int i, Type elemType) {
        return false;
    }

    @Override
    public Iterator getDeletes(CollectionPersister persister, boolean indexIsFormula) {
        return null;
    }

    @Override
    public boolean isWrapper(Object collection) {
        return false;
    }
}
