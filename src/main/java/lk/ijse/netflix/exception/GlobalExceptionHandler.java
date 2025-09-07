package lk.ijse.netflix.exception;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lk.ijse.netflix.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse handleUsernameNotFoundException
            (UsernameNotFoundException ex) {
        return new ApiResponse(
                404,
                "User Not Fount",
                ex.getMessage());
    }
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse handleBadCredentialsException
            (BadCredentialsException ex) {
        return new ApiResponse(401,
                "Unauthorized",
                "Invalid username or password");
    }
    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse handleExpiredJwtException
            (ExpiredJwtException ex) {
        return new ApiResponse(401,
                "Unauthorized",
                "Expired JWT Token");
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path.startsWith("/add")) {
            return new ApiResponse(403, "Forbidden", "Not Admin");
        } else {
            return new ApiResponse(402, "Payment Required", "Upgrade to a paid plan to access this feature");
        }
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse handleRuntimeException
            (RuntimeException ex) {
        return new ApiResponse(500,
                "Internal Server Error",
                ex.getMessage());
    }
}
