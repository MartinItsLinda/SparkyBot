package uk.sparkydiscordbot.api.entities.utils;

import com.google.common.collect.Iterators;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * Paginate a text based list
 */
public class PaginatedList<T> implements Iterable<T> {

    private final Collection<T> items;
    private final int page;
    private final int pageSize;
    private final int totalPages;

    /**
     * @param items The {@link Collection} of paginated items
     * @param page  The current page
     */
    private PaginatedList(final Collection<T> items,
                          final int page) {
        this(items, page, items.size());
    }

    /**
     * @param items    The {@link Collection} of paginated items
     * @param pageSize The maximum elements present on a page
     * @param page     The current page
     */
    private PaginatedList(final Collection<T> items,
                          final int page,
                          final int pageSize) {
        //we have no need to modify it but we do supply it so make it immutable
        this.items = Collections.unmodifiableCollection(items);
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((float) (items.size() / pageSize));
    }

    /**
     * Get the paginated list
     *
     * @return An immutable paginated {@link Collection}
     */
    public Collection<T> getPaginatedList() {
        return this.items;
    }

    /**
     * Get the page of this {@link PaginatedList}
     *
     * @return The current page
     */
    public int getPage() {
        return this.page;
    }

    /**
     * @return The maximum number of elements on this page
     */
    public int getPageSize() {
        return this.pageSize;
    }

    /**
     * @return The total amount of pages
     */
    public int getTotalPages() {
        return this.totalPages;
    }

    /**
     * @return How many elements are in this {@link PaginatedList}
     */
    public int count() {
        return this.items.size();
    }

    /**
     * @return An immutable {@link Iterator} for this {@link PaginatedList}
     */
    @NotNull
    @Override
    public Iterator<T> iterator() {
        return Iterators.unmodifiableIterator(this.items.iterator());
    }

    /**
     * Create a new {@link PaginatedList}
     *
     * @param items           The items to paginate
     * @param currentPage     The page to start at
     * @param maxItemsPerPage The maximum number of elements to display per page
     * @return The {@link PaginatedList}
     */
    public static <T> PaginatedList<T> paginate(final Collection<T> items,
                                                final int currentPage,
                                                final int maxItemsPerPage) {
        Objects.checkArgument(!items.isEmpty(), "cannot paginate an empty list");
        Objects.checkArgument(maxItemsPerPage > 0, "page size cannot be less than 1");
        Objects.checkArgument(currentPage > 0, "page number cannot be less than 0");

        return new PaginatedList<>(items.stream().skip((currentPage - 1) * maxItemsPerPage).limit(maxItemsPerPage).collect(Collectors.toList()), currentPage, maxItemsPerPage);
    }

}
