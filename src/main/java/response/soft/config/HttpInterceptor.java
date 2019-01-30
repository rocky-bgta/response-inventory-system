package response.soft.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import response.soft.core.Core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class HttpInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //System.out.println("I am from pre Handler");
        Core.resetPaginationVariable();
        Core.restHibernateSession();


        return true;
    }
    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {

        //System.out.println("I am from post Handle");
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception exception) throws Exception {
        //System.out.println("I am from afterCompletion Handle");
        Core.resetPaginationVariable();
        Core.closeHibernateSession();
        Core.restHibernateSession();

    }
}
