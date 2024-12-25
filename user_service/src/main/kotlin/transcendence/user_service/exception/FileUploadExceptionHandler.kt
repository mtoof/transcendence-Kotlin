package transcendence.user_service.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.multipart.MaxUploadSizeExceededException

@ControllerAdvice
class FileUploadExceptionHandler {
    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun handleMaxFileSize(ex: MaxUploadSizeExceededException): ResponseEntity<String>{
        return ResponseEntity
            .status(HttpStatus.PAYLOAD_TOO_LARGE)
            .body("File size exceeds the maximum allowed size!")
    }
}