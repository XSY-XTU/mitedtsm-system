package com.meession.etm.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目的启动类
 *
 * @author 密讯
 */
@SuppressWarnings("SpringComponentScan") // 忽略 IDEA 无法识别 ${mitedtsm.info.base-package}
@SpringBootApplication(scanBasePackages = {"${mitedtsm.info.base-package}.server", "${mitedtsm.info.base-package}.module"})
public class MitedtsmServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MitedtsmServerApplication.class, args);
//        new SpringApplicationBuilder(MitedtsmServerApplication.class)
//                .applicationStartup(new BufferingApplicationStartup(20480))
//                .run(args);

    }

}
