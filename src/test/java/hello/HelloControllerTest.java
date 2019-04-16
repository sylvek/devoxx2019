package hello;

import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.ToxiproxyClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import java.net.URL;

import static eu.rekawek.toxiproxy.model.ToxicDirection.DOWNSTREAM;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HelloControllerTest {

    @LocalServerPort
    private int port;

    private URL base;

    @Autowired
    private TestRestTemplate template;

    private Proxy proxy;

    @Before
    public void setUp() throws Exception {
        this.base = new URL("http://localhost:" + port + "/");
        ToxiproxyClient client = new ToxiproxyClient("localhost", 8474);
        proxy = client.createProxy("HelloControllerIT", "localhost:8889", "localhost:8888");
    }

    @After
    public void tearDown() throws Exception {
        proxy.delete();
    }

    @Test
    public void getHello() throws Exception {
        proxy.toxics().latency("my-latency-toxic", DOWNSTREAM, 1200).setJitter(500);

        StopWatch stopWatch = new StopWatch();

        for (int i = 0; i < 10; i++) {
            stopWatch.start();
            ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
            stopWatch.stop();

            final String body = response.getBody();
            final long lastTaskTimeMillis = stopWatch.getLastTaskTimeMillis();
            System.out.println(body + " in " + lastTaskTimeMillis + "ms");

            assertThat(body, containsString("Hello"));
            assertTrue(lastTaskTimeMillis < 1500);

            Thread.sleep(300);
        }

    }
}
