package ro.btrl.miswebappspringdemo.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles custom exceptions
     * @param ex - CustomException thrown in application
     * @return custom response for exceptions
     */
    @ExceptionHandler(value = {CustomException.class})
    protected ResponseEntity<ExceptionMessage> handleBnrHrException(CustomException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(new ExceptionMessage(ex.getExceptionMsg()));
    }

    /**
     * bad upload request
     * ex: wrong request header
     * @return custom response for multipart exception
     */
//    @ExceptionHandler(MultipartException.class)
//    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
//    @ResponseBody
//    protected ResponseEntity<ExceptionMessage> handleGenericMultipartException(MultipartException e) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExceptionMessage("Probleme la procesarea cererii de incarcare!"));
//    }
}
