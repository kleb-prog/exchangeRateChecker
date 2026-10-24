package com.lebedev.exchangeRate.dto;

import java.util.Map;

public record ApiErrorResponse(String message, Map<String, String> fields) {
}
