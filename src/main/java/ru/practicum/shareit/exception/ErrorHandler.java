package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public UserNotFoundException handle(final UserNotFoundException e) {
        return new UserNotFoundException();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ItemNotFoundException handle(final ItemNotFoundException e) {
        return new ItemNotFoundException();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public DuplicateEmailException handle(final DuplicateEmailException e) {
        return new DuplicateEmailException();
    }

}
