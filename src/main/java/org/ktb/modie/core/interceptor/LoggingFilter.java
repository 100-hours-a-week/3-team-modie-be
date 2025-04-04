package org.ktb.modie.core.interceptor;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoggingFilter extends OncePerRequestFilter {
	private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);
	private static final String REQUEST_ID = "requestId";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		long startTime = System.currentTimeMillis();

		String requestId = request.getHeader("X-Request-ID");
		if (requestId == null || requestId.isEmpty()) {
			requestId = UUID.randomUUID().toString();
		}

		MDC.put(REQUEST_ID, requestId);

		try {
			response.addHeader("X-Request-ID", requestId);

			logRequest(request, requestId);

			filterChain.doFilter(request, response);

			logResponse(response, requestId, System.currentTimeMillis() - startTime);

		} finally {
			MDC.remove(REQUEST_ID);
		}
	}

	private void logRequest(HttpServletRequest request, String requestId) {
		String method = request.getMethod();
		String uri = request.getRequestURI();
		String queryString = request.getQueryString();
		String fullUrl = uri + (queryString != null ? "?" + queryString : "");

		log.info("[{}] Request: {} {}", requestId, method, fullUrl);
	}

	private void logResponse(HttpServletResponse response, String requestId, long duration) {
		int status = response.getStatus();
		log.info("[{}] Response: {} ({}ms)", requestId, status, duration);
	}
}
