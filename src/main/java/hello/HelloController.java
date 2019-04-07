package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    EchoService echoService;

    @RequestMapping("/")
    //@HystrixCommand(fallbackMethod = "reliable")
    public String index() {
        return String.format("Hello %s!", echoService.getEcho());
    }

    public String reliable() {
        return "Hello Chaos!";
    }
}