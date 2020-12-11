package no.microdata.datastore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.Supplier;

import static no.microdata.datastore.adapters.api.Constants.*;

@Configuration
class WebMvcConfig implements WebMvcConfigurer {

    private final static Logger log = LoggerFactory.getLogger(WebMvcConfig.class);

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
      // see https://www.baeldung.com/web-mvc-configurer-adapter-deprecated
      // super.addInterceptors(registry)

        registry.addInterceptor(new HandlerInterceptor() {

            private ThreadLocal threadLocal = ThreadLocal.withInitial(new Supplier() {
                @Override
                public Object get() {
                    return null;
                }
            });

            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                String method = request.getMethod().toUpperCase();
                String xRequestId = request.getHeader(X_REQUEST_ID);
                String url = request.getRequestURI();
                if (threadLocal.get() == null) {
                    threadLocal.set(System.currentTimeMillis());
                    MDC.put("method", method);
                    MDC.put("xRequestId", xRequestId);
                    MDC.put("url", url);
                }
                return true;
            }

            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
                long startTime = (long) threadLocal.get();
                String timeUsed = startTime == 0 ? null : ("" + (System.currentTimeMillis() - startTime));

//                String xRequestId = response.getHeader(X_REQUEST_ID);
//                String method = MDC.get("method") != null ? MDC.get("method") : request.getMethod().toUpperCase();
//                String url = MDC.get("url") != null ? MDC.get("url") : request.getRequestURI();
//                String statusCode = "" + response.getStatus();
//
//                MDC.put("statusCode", statusCode);
//                MDC.put("responseTime", timeUsed);
//                MDC.put("xRequestId", xRequestId);
//
//                String message = String.format("Response for %s %s was %s", method, url, statusCode);
//                if (statusCode.startsWith("1") || statusCode.startsWith("2") || statusCode.startsWith("3")) {
//                    log.info(message);
//                } else if (statusCode.startsWith("4")) {
//                    log.warn(message);
//                } else {
//                    log.error(message);
//                }

                MDC.clear();
                threadLocal.set(null);
            }

            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

            }
        });
    }

}