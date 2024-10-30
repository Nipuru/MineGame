package top.nipuru.minegame.auth;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.InputStream;

@SpringBootApplication
public class AuthServerApplication {

    public static void main(String[] args) {
        System.setProperty("spring.devtools.restart.enabled", "false"); // 关闭热部署
        SpringApplication application = new SpringApplication(AuthServerApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  AuthServer启动成功   ლ(´ڡ`ლ)ﾞ");
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            File configFile = new File("application.yml");
            if (!configFile.exists()) {
                try (InputStream inputStream = ResourceUtil.getStream(configFile.getName())) {
                    FileUtil.writeFromStream(inputStream, configFile);
                    System.out.println("Default configuration file extracted to " + configFile.getAbsolutePath());
                }
            }
        };
    }
}
