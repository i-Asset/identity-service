package eu.nimble.core.infrastructure.identity.system;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Johannes Innerbichler on 2018-12-27.
 */
public class ControllerUtils {
    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "company not found")
    public static class CompanyNotFoundException extends RuntimeException {
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "person not found")
    public static class PersonNotFoundException extends RuntimeException {
    }

    @ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "File size exceeds limit")
    public static class FileTooLargeException extends RuntimeException {
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "document not found")
    public static class DocumentNotFoundException extends RuntimeException {
        DocumentNotFoundException() {
        }

        DocumentNotFoundException(String message) {
            super(message);
        }
    }

    @ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "unauthorised access")
    public static class UnauthorisedAccess extends RuntimeException {
    }
}
