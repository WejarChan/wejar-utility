package org.wejar.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 上下文容器过滤器
 * @author wejarchan
 *
 */
public class ContextContainerInitFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)throws IOException, ServletException {
        ContextContainer.setRequest((HttpServletRequest) req);
        ContextContainer.setResponse((HttpServletResponse) res);
        filterChain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }
    @Override
    public void destroy() {
    }

}
