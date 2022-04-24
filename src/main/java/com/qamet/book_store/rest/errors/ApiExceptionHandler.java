package com.qamet.book_store.rest.errors;

import com.qamet.book_store.util.StringUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.DefaultProblem;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.*;

@ControllerAdvice
public class ApiExceptionHandler implements ProblemHandling, SecurityAdviceTrait {
    // https://datatracker.ietf.org/doc/html/rfc7807#section-3
    private static final String VIOLATIONS_KEY = "invalid_params";
    public static final String ERR_VALIDATION = "validation";

    @Override
    public ResponseEntity<Problem> process(@Nullable ResponseEntity<Problem> entity, @NotNull NativeWebRequest request) {
        if (entity == null) {
            return null;
        }

        Problem problem = entity.getBody();
        if (!(problem instanceof ConstraintViolationProblem || problem instanceof DefaultProblem)) {
            return entity;
        }

        ProblemBuilder builder = Problem.builder();

        if (problem instanceof ConstraintViolationProblem) {
            builder
                    .withTitle(ERR_VALIDATION)
                    .with(VIOLATIONS_KEY, this.transformViolations(((ConstraintViolationProblem) problem).getViolations()));

            return new ResponseEntity<>(builder.build(), entity.getHeaders(), HttpStatus.UNPROCESSABLE_ENTITY);
        } else {

            builder
                    .withTitle(problem.getTitle())
                    .withCause(((DefaultProblem) problem).getCause())
                    .withDetail(problem.getDetail())
                    .withInstance(problem.getInstance());
            problem.getParameters().forEach(builder::with);

            return new ResponseEntity<>(builder.build(), entity.getHeaders(), entity.getStatusCode());
        }
    }


    @ExceptionHandler
    public ResponseEntity<Problem> handleBadRequestAlertException(BadRequestAlertException ex, NativeWebRequest request) {
        return create(ex, request, new HttpHeaders());
    }

    private Map<String, List<String>> transformViolations(List<Violation> violations) {
        Map<String, List<String>> violationsMap = new HashMap<>();

        violations.forEach(violation -> {
            String field = StringUtil.toSnakeCase(violation.getField());
            if (violationsMap.containsKey(field)) {
                violationsMap.get(field).add(violation.getMessage());
            } else {
                violationsMap.put(field, new ArrayList<>(Collections.singleton(violation.getMessage())));
            }
        });
        return violationsMap;
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Problem> handleNoSuchElementException(NoSuchElementException ex, NativeWebRequest request) {
        Problem problem = Problem.builder()
                .withTitle("Not Found")
                .withStatus(Status.NOT_FOUND)
                .with("message", "Such data not found in database")
                .build();

        return create(ex, problem, request);
    }
}
