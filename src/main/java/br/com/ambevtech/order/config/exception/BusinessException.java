package br.com.ambevtech.order.config.exception;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class BusinessException extends RuntimeException {

    private String message;
}
