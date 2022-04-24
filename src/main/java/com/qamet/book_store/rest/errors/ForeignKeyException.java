package com.qamet.book_store.rest.errors;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.zalando.problem.AbstractThrowableProblem;

@Getter
@ResponseStatus(code = HttpStatus.CONFLICT, value = HttpStatus.CONFLICT)
public class ForeignKeyException extends AbstractThrowableProblem {
    private final Integer id;

    public ForeignKeyException(Integer id) {
        this.id = id;
    }
}
