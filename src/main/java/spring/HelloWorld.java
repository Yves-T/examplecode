package spring;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import pojo.Greeting;
import pojo.HelloMessage;

@Controller
public class HelloWorld {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting hello(HelloMessage message) {
        System.out.println("called with message" + message);
        return new Greeting("Hello, " + message.getName());
    }

    @RequestMapping("/")
    public String showSocketStartPage() {
        return "index";
    }


    @RequestMapping("/test")
    public String test() {
        return "test";
    }
}
