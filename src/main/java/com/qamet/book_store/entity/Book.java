package com.qamet.book_store.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "books")
public class Book extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String description;

    private BigDecimal price;

    @Column(name = "publisher_id")
    private Integer publisherId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id", insertable = false, updatable = false)
    @ToString.Exclude
    private User publisher;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_author",
            joinColumns = @JoinColumn(name = "book_id", referencedColumnName = "id", nullable = false, updatable = false),
            inverseJoinColumns = @JoinColumn(name = "author_id", referencedColumnName = "id", nullable = false, updatable = false))
    private Set<Author> authors = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Book)) {
            return false;
        }
        return id != null && id.equals(((Book) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
