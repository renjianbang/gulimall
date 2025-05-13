package com.xunqi.gulimall;

import cn.hutool.core.util.IdUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GulimallApplicationTests {

    @Test
    void contextLoads() {
        String snowflakeNextIdStr = IdUtil.getSnowflakeNextIdStr();
        System.out.println(snowflakeNextIdStr);
    }

}
