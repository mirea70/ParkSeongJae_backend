package com.wirebarley.in.web.error.handler;

import com.wirebarley.error.info.ErrorCategory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpStatusErrorMapper {
    private static final Map<ErrorCategory, HttpStatus> statusMap = Map.ofEntries(
            Map.entry(ErrorCategory.NOT_FOUND, HttpStatus.NOT_FOUND),
            Map.entry(ErrorCategory.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR),
            Map.entry(ErrorCategory.INVALID_VALUE, HttpStatus.BAD_REQUEST)
    );

    public static HttpStatus map(ErrorCategory errorCategory) {
        return statusMap.getOrDefault(errorCategory, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
