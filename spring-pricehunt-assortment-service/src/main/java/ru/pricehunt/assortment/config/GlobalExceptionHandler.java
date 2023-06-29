package ru.pricehunt.assortment.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.pricehunt.assortment.dto.ErrorDTO;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(io.jsonwebtoken.security.SignatureException.class)
    public ResponseEntity<ErrorDTO> handleSignatureException(io.jsonwebtoken.security.SignatureException ex) {
        ErrorDTO ErrorDTO = new ErrorDTO();
        ErrorDTO.setStatus(HttpStatus.UNAUTHORIZED.value());
        ErrorDTO.setMessage("SignatureException. " + ex.getMessage());
        ErrorDTO.setTimeStamp(System.currentTimeMillis());

        return new ResponseEntity<>(ErrorDTO, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleMethodArgumentNotValidException(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        ErrorDTO ErrorDTO = new ErrorDTO();
        ErrorDTO.setStatus(HttpStatus.BAD_REQUEST.value());
        ErrorDTO.setMessage("MethodArgumentNotValidException. " + ex.getMessage());
        ErrorDTO.setTimeStamp(System.currentTimeMillis());

        return new ResponseEntity<>(ErrorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleEntityNotFoundException(jakarta.persistence.EntityNotFoundException ex) {
        ErrorDTO ErrorDTO = new ErrorDTO();
        ErrorDTO.setStatus(HttpStatus.NOT_FOUND.value());
        ErrorDTO.setMessage("EntityNotFoundException. " + ex.getMessage());
        ErrorDTO.setTimeStamp(System.currentTimeMillis());

        return new ResponseEntity<>(ErrorDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(java.lang.IllegalArgumentException.class)
    public ResponseEntity<ErrorDTO> handleIllegalArgumentException(java.lang.IllegalArgumentException ex) {
        ErrorDTO ErrorDTO = new ErrorDTO();
        ErrorDTO.setStatus(HttpStatus.BAD_REQUEST.value());
        ErrorDTO.setMessage("IllegalArgumentException. " + ex.getMessage());
        ErrorDTO.setTimeStamp(System.currentTimeMillis());

        return new ResponseEntity<>(ErrorDTO, HttpStatus.BAD_REQUEST);
    }
}
