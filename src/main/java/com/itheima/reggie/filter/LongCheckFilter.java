package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
// @WebFilter 注解，该过滤器将被自动注册到应用程序中，可以在其他地方引用过滤器，并根据指定的 URL 模式拦截相应的请求。
// 这个过滤器在应用程序启动时被自动注册，并且会在每个请求到达时被调用。
@WebFilter(filterName = "LongCheckFilter",urlPatterns = "/*")
public class LongCheckFilter implements Filter {

    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    // filterChain：过滤器链，用于将请求传递给下一个过滤器或目标资源进行处理。
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 存储 HTTP 请求的对象，包含了客户端发送的请求信息，例如请求的URL、请求参数、请求头等。
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        // 存储 HTTP 响应的对象，包含了发送给客户端的响应信息，例如响应的内容类型、响应的状态码等。
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestURL = request.getRequestURI();
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                //移动端发送短信
                "/user/login",
                // 移动端登陆
                "/doc.html",
                "/webjars/**",
                "/swagger-resources/**",
                "/v2/api-docs",
        };

        boolean check = check(urls, requestURL);
        if(check){
            // filterChain.doFilter()：将请求传递给下一个过滤器或目标资源进行处理。
            filterChain.doFilter(request,response);
            return;
        }
        if(request.getSession().getAttribute("employee") != null){
            Long emId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(emId);
            filterChain.doFilter(request,response);
            return;
        }

        if(request.getSession().getAttribute("user") != null){
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }

        // write() :把数据写入到前端响应中
        response.getWriter().write(JSON.toJSONString(R.error("用户未登录！")));
    }

    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
