package br.com.ambevtech.order.config;

import br.com.ambevtech.order.config.exception.BusinessException;
import br.com.ambevtech.order.config.model.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class ControllerConfig {

    @ExceptionHandler({BusinessException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Error businessException(final BusinessException ex) {
        log.error(ex.getMessage(), ex);

        return Error.builder()
            .message(ex.getMessage())
            .build();
    }

}
