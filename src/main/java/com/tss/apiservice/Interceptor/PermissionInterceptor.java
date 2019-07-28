package com.tss.apiservice.Interceptor;

import com.tss.apiservice.annotation.RequiredPermission;
import com.tss.apiservice.dao.UsersPoMapper;
import com.tss.apiservice.po.UsersPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class PermissionInterceptor implements HandlerInterceptor {

    private static Logger logger = LoggerFactory.getLogger(PermissionInterceptor.class);

    @Autowired
    UsersPoMapper usersPoMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 验证权限
        String userid = request.getParameter("userid");
        if (this.hasPermission(handler, request)) {
            return true;
        }
        //  null == request.getHeader("x-requested-with") TODO 暂时用这个来判断是否为ajax请求
        // 如果没有权限 则抛403异常 springboot会处理，跳转到 /error/403 页面
        response.sendError(HttpStatus.FORBIDDEN.value(), "用户无权限...");
        return false;
    }

    /**
     * 是否有权限
     *
     * @param handler
     * @return
     */
    private boolean hasPermission(Object handler, HttpServletRequest request) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 获取方法上的注解
            RequiredPermission requiredPermission = handlerMethod.getMethod().getAnnotation(RequiredPermission.class);
            // 如果方法上的注解为空 则获取类的注解
            if (requiredPermission == null) {
                requiredPermission = handlerMethod.getMethod().getDeclaringClass().getAnnotation(RequiredPermission.class);
            }
            // 如果标记了注解，则判断权限(requiredPermission.value()是获取注解里面的内容 , 0,1,2)
            String value = (requiredPermission == null)? null:requiredPermission.value();
            if (requiredPermission != null && !StringUtils.isEmpty(value)) {
                // redis或数据库 中获取该用户的权限信息 并判断是否有权限
//                Set<String> permissionSet = adminUserService.getPermissionSet();
//                if (CollectionUtils.isEmpty(permissionSet)) {
//                    return false;
//                }
//                return permissionSet.contains(requiredPermission.value());
                NativeWebRequest webRequest = new ServletWebRequest(request);
                String userid = null;
                if (request.getMethod().equals("GET")) {
                    userid = request.getParameter("userid");
                } else {
                    Map<String, String> map = (Map<String, String>) webRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
                    userid = map.get("userid");
                }
                System.out.println("用户的 userid = " + userid);
                //获取用户的等级
                if (StringUtils.isEmpty(userid)) {
                    return false;
                } else {
                    //获取用户的等级，然后跟注解权限比较，是否允许通过
                    UsersPo usersPo = usersPoMapper.selectByPrimaryKey(Integer.valueOf(userid));
                    Integer permissionLevel = Integer.valueOf(value);
                    boolean b = usersPo.getLevel() < permissionLevel;
                    if (usersPo == null) {
                        logger.info("权限校验 用户 id 查找不到 用户信息...");
                        return false;
                    }
                    if (usersPo.getLevel() > permissionLevel) {
                        logger.info("权限校验 用户 id 权限等级不够 ...userid = {}" + userid);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
