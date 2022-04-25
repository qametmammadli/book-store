package com.qamet.book_store.repository;

import com.qamet.book_store.entity.Author_;
import com.qamet.book_store.entity.Book;
import com.qamet.book_store.entity.Book_;
import com.qamet.book_store.entity.User_;
import com.qamet.book_store.rest.dto.BookSpecDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {

    List<Book> findByPublisherId(Integer publisherId);

    default Page<Book> findAllBySpec(BookSpecDTO bookSpecDTO, Pageable pageable) {

        return findAll((root, query, criteriaBuilder) -> {
                    List<Predicate> predicates = new ArrayList<>();

                    if (bookSpecDTO.getBookName().length() > 0) {
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(Book_.NAME)), "%" + bookSpecDTO.getBookName().toLowerCase() + "%"));
                    }

                    if (bookSpecDTO.getBookDescription().length() > 0) {
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(Book_.DESCRIPTION)), "%" + bookSpecDTO.getBookDescription().toLowerCase() + "%"));
                    }

                    if (bookSpecDTO.getPublisherName().length() > 0) {
                        Expression<String> publisherNameConcatExpression = criteriaBuilder.concat(
                                criteriaBuilder.concat(root.get(Book_.PUBLISHER).get(User_.FIRST_NAME),
                                        " "),
                                root.get(Book_.PUBLISHER).get(User_.LAST_NAME));

                        Predicate publisherNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(
                                publisherNameConcatExpression),
                                "%" + bookSpecDTO.getPublisherName().toLowerCase() + "%");

                        predicates.add(publisherNamePredicate);
                    }

                    if (bookSpecDTO.getAuthorName().length() > 0) {
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.join(Book_.AUTHORS).get(Author_.NAME)), "%" + bookSpecDTO.getAuthorName().toLowerCase() + "%"));
                    }

                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(Book_.price), bookSpecDTO.getPriceFrom()));

                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(Book_.price), bookSpecDTO.getPriceTo()));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                },
                pageable);
    }

}
