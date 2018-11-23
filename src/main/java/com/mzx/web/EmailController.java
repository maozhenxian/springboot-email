package com.mzx.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.mzx.util.SendEmailUtil;

@Controller
public class EmailController {

    @GetMapping("/email")
    public String sendEmail() {
        boolean flag = SendEmailUtil.sendEmail("这是一封测试邮件", new String[] { "3133005620@qq.com" }, null,
                "<h3><a href='http://www.baidu.com'>百度一下，你就知道</a></h3>", null);
        String str = String.valueOf(flag);
        return str;
    }
}
