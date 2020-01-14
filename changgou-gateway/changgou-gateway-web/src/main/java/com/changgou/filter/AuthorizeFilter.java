package com.changgou.filter;

import com.changgou.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Author: nullWagesException
 * @Date: 2020/1/12 11:23
 * @Description:
 */
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {

    //令牌头名字
    private static final String AUTHORIZE_TOKEN = "Authorization";

    /***
     * 全局过滤器
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //获取请求的URI
        String path = request.getURI().getPath();
        //如果是登录、goods等开放的微服务[这里的goods部分开放],则直接放行,这里不做完整演示，完整演示需要设计一套权限系统
        if (path.startsWith("/api/user/login") || path.startsWith("/api/brand/search/")) {
            //放行
            Mono<Void> filter = chain.filter(exchange);
            return filter;
        }

        //检查请求参数中是否携带
        String token = request.getQueryParams().getFirst("AUTHORIZE_TOKEN");

        //请求参数中没有，去请求头寻找
        if(StringUtils.isEmpty(token)){
            token = request.getHeaders().getFirst("AUTHORIZE_TOKEN");
        }

        //请求参数也没有，去cookie中寻找
        if (StringUtils.isEmpty(token)){
            HttpCookie httpCookie = request.getCookies().getFirst("AUTHORIZE_TOKEN");
            if (httpCookie!=null){
                token = httpCookie.getValue();
            }
        }

        //如果为空，则输出错误代码
        if (StringUtils.isEmpty(token)) {
            //设置方法不允许被访问，405错误代码
            response.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
            return response.setComplete();
        }

        //解析令牌数据
        try {
            Claims claims = JwtUtil.parseJWT(token);
            request.mutate().header(AUTHORIZE_TOKEN,claims.toString());
        } catch (Exception e) {
            e.printStackTrace();
            //解析失败，响应401错误
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
