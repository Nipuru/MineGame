package top.nipuru.minegame.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 程序注解配置
 *
 * @author Nipuru
 * @since 2023/10/15 13:30
 */
@Configuration
@EnableAspectJAutoProxy(exposeProxy = true) // 表示通过aop框架暴露该代理对象,AopContext能够访问
public class ApplicationConfig {

}
