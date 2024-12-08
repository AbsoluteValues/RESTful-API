package com.AbsoluteValue.RESTful.common.exception;

import jakarta.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionController {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionController.class);

    /**
     * 전역 예외를 처리합니다.
     * <p>
     * 이 메서드는 애플리케이션에서 처리되지 않은 예기치 못한 예외가 발생했을 때 호출됩니다. 모든 예외를 포괄적으로 처리하며, 예외 로그를 에러 레벨로 기록합니다. 클라이언트에게는 내부 서버 오류를 나타내는
     * 에러 응답을 반환하며, 응답 메시지에는 문제의 심각성을 알리고 해결을 요청하는 내용이 포함됩니다.
     *
     * @param ex      발생한 예외 객체. 처리되지 않은 예기치 못한 예외.
     * @param request 웹 요청 객체. 요청 정보를 포함합니다.
     * @return ErrorResponse 오류 응답 객체. 에러 상태와 메시지를 포함합니다.
     */
    @ExceptionHandler(Exception.class)
    private ErrorResponse handleGlobalException(Exception ex, WebRequest request) {
        logger.error("예기치 못한 예외 발생: ", ex);

        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "예상하지 못한 에러가 발생하였습니다. 즉시 해결하세요.",
                request,
                "001"
        );
    }

    /**
     * 데이터 검증 실패를 처리합니다.
     * <p>
     * 이 메서드는 요청 데이터의 유효성 검사에 실패했을 때 호출됩니다. 주로 요청 본문에서 전달된 데이터가 유효성 검사를 통과하지 못했을 때 발생합니다. 이 메서드는 경고 로그를 기록하고 클라이언트에게 유효성
     * 검사 실패에 대한 자세한 정보를 포함하는 에러 응답을 반환합니다.
     *
     * @param ex      발생한 예외 객체. 유효성 검사 실패와 관련된 예외.
     * @param request 웹 요청 객체. 요청 정보를 포함합니다.
     * @return ErrorResponse 오류 응답 객체. 에러 상태, 메시지 및 필드별 오류 정보를 포함합니다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        logger.warn("데이터 검증 실패: ", ex);

        Map<String, Object> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "데이터 검증 에러가 발생하였습니다.",
                request,
                "002",
                Map.of("errors", errors)
        );
    }

    /**
     * 데이터베이스 접근 예외를 처리합니다.
     * <p>
     * 이 메서드는 데이터베이스 관련 오류가 발생했을 때 호출됩니다. 예를 들어 SQL 쿼리 오류나 데이터베이스 연결 실패와 같은 상황에서 사용됩니다. 이 메서드는 오류 로그를 기록하고 클라이언트에게
     * 데이터베이스 오류를 나타내는 에러 응답을 반환합니다.
     *
     * @param ex      발생한 예외 객체. 데이터베이스 접근 관련 예외.
     * @param request 웹 요청 객체. 요청 정보를 포함합니다.
     * @return ErrorResponse 오류 응답 객체. 에러 상태와 메시지를 포함합니다.
     */
    @ExceptionHandler(DataAccessException.class)
    private ErrorResponse handleDataAccessException(DataAccessException ex, WebRequest request) {
        logger.error("데이터 접근 예외 발생: ", ex);

        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "데이터베이스 오류가 발생했습니다.",
                request,
                "003"
        );
    }

    /**
     * 처리할 수 없는 요청을 처리합니다.
     * <p>
     * 이 메서드는 요청한 리소스를 찾을 수 없을 때 발생하는 오류를 처리합니다. 주로 존재하지 않는 URI나 잘못된 리소스 요청 시 호출됩니다. 경고 로그를 기록하고 클라이언트에게 리소스가 없다는 내용을
     * 포함한 에러 응답을 반환합니다.
     *
     * @param ex      발생한 예외 객체. 요청한 리소스를 찾을 수 없는 예외.
     * @param request 웹 요청 객체. 요청 정보를 포함합니다.
     * @return ErrorResponse 오류 응답 객체. 에러 상태, 메시지 및 관련 정보를 포함합니다.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    private ErrorResponse handleNoHandlerFoundException(NoResourceFoundException ex, WebRequest request) {
        logger.warn("처리할 수 없는 요청: ", ex);

        return new ErrorResponse(
                HttpStatus.NOT_FOUND,
                "요청한 리소스를 찾을 수 없습니다.",
                request,
                "004"
        );
    }

    /**
     * 메시지 처리 중 발생한 예외를 처리합니다.
     * <p>
     * 이 메서드는 메시지 처리 과정에서 발생하는 오류를 처리합니다. 이메일 또는 메시지 전송과 관련된 오류가 발생했을 때 호출됩니다. 경고 로그를 기록하고 클라이언트에게 서버 내부 오류임을 알리는 메시지와
     * 함께 에러 응답을 반환합니다.
     *
     * @param ex      발생한 예외 객체. 메시지 처리 중 발생한 예외.
     * @param request 웹 요청 객체. 요청 정보를 포함합니다.
     * @return ErrorResponse 오류 응답 객체. 에러 상태, 메시지 및 관련 정보를 포함합니다.
     */
    @ExceptionHandler(MessagingException.class)
    private ErrorResponse handleMessagingException(MessagingException ex, WebRequest request) {
        logger.warn("메시지 처리 중 예외 발생: ", ex);

        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "서버에서 요청을 처리하던 중 문제가 발생했습니다. 잠시 후 다시 시도해주세요.",
                request,
                "005"
        );
    }

    // (Spring Security 적용 필수)
    /**
     * 접근 권한이 없는 요청을 처리합니다.
     *
     * 이 메서드는 클라이언트가 요청한 리소스에 대한 접근 권한이 없는 경우 호출됩니다.
     * 접근 권한이 없는 요청에 대해 경고 로그를 기록하고 클라이언트에게 권한이 없다는 에러 메시지를 포함한 응답을 반환합니다.
     * 이 메서드는 `Spring Security`를 사용할 때 주로 필요하며,
     * 요청 경로와 관련된 추가 정보를 포함하여 접근 거부 이유를 명확히 전달합니다.
     *
     * @param ex 발생한 예외 객체. 접근 권한이 없는 예외.
     * @param request 웹 요청 객체. 요청 정보를 포함합니다.
     * @return ErrorResponse 오류 응답 객체. 에러 상태, 메시지 및 요청 경로 정보를 포함합니다.
     */
//    @ExceptionHandler(AccessDeniedException.class)
//    public ErrorResponse handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
//        logger.warn("접근 불가: ", ex);
//
//        return ErrorResponse.build(
//                HttpStatus.FORBIDDEN,
//                "요청한 리소스에 접근할 권한이 없습니다.",
//                getPathFromRequest(request),
//                "006"
//        );
//    }

    /**
     * 커스텀 예외를 처리합니다.
     * <p>
     * 이 메서드는 애플리케이션에서 정의한 커스텀 예외인 `CustomException`을 처리합니다. 커스텀 예외는 애플리케이션의 비즈니스 로직에 따라 특정 상황에서 발생할 수 있으며, 이 메서드는 이러한
     * 예외에 대한 경고 로그를 기록하고 예외에서 제공하는 에러 코드와 메시지를 포함한 에러 응답을 반환합니다. 응답에는 HTTP 상태 코드와 관련된 에러 코드가 포함되어 클라이언트가 문제를 정확히 이해할 수
     * 있도록 돕습니다.
     *
     * @param ex      발생한 커스텀 예외 객체. 비즈니스 로직에 의해 정의된 예외.
     * @param request 웹 요청 객체. 요청 정보를 포함합니다.
     * @return ErrorResponse 오류 응답 객체. 에러 상태, 메시지 및 에러 코드를 포함합니다.
     */
    @ExceptionHandler(CustomException.class)
    private ErrorResponse handleCustomException(CustomException ex, WebRequest request) {
        logger.warn("커스텀 예외 발생: ", ex);

        return new ErrorResponse(
                ex.getErrorCode().getStatus(),
                ex.getErrorCode().getMessage(),
                request,
                ex.getErrorCode().getErrorCode()
        );
    }
}