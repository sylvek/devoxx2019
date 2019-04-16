# Chaos Engineering Toolbox for Java

This presentation was made for Devoxx FR 2019. 

It talks about Chaos Monkey for SpringBoot and Toxiproxy.

3 git tags are availables :
- INITIAL consists of the begining of the demo
- CHAOS initiates Chaos Monkey for SpringBoot
- HYSTRIX allows to fix the Chaos

`Chaos` occurs every time, we can not fix that. But we can improve the reability of our code to survive during `Chaos`.

To start the demonstration, your should launch the echo service.

```
▶ cd echo
▶ go run main.go
```

That starts a service answering on `http://localhost:8888/helloworld``

Now, you can start the targeted code :

```
▶ ./mvnw spring-boot:run
```

Everything works if `Hello Devoxx!` is displayed

```
▶ http :8080/
HTTP/1.1 200
Content-Length: 13
Content-Type: text/plain;charset=UTF-8
Date: Tue, 16 Apr 2019 12:49:20 GMT

Hello Devoxx!
```

To loop a while 

```
▶ for ((;;)) do http :8080
done
```

## Make the Chaos !

`Chas Monkey for SpringBoot` allows us to inject assaults in several layers of our code.

Switch to `CHAOS` and restart maven.

```
▶ git checkout CHAOS
La position précédente de HEAD était sur c7ac600 initial step
HEAD est maintenant sur 4c00ee7 chaosmonkey
▶ ./mvnw spring-boot:run
```

Check if `Chaos` is enabled

```
▶ http POST :8080/actuator/chaosmonkey/enable
HTTP/1.1 200
Content-Length: 23
Content-Type: text/plain;charset=UTF-8
Date: Tue, 16 Apr 2019 12:58:05 GMT

Chaos Monkey is enabled
```

Change the current configuration

```
▶ http POST :8080/actuator/chaosmonkey/assaults < default.json
HTTP/1.1 200
Content-Length: 26
Content-Type: application/json;charset=UTF-8
Date: Tue, 16 Apr 2019 12:58:40 GMT

Assault config has changed
```

And confirm the `Chaos`

```
HTTP/1.1 200
Content-Length: 13
Content-Type: text/plain;charset=UTF-8
Date: Tue, 16 Apr 2019 12:58:55 GMT

Hello Devoxx!

HTTP/1.1 500
Connection: close
Content-Type: application/json;charset=UTF-8
Date: Tue, 16 Apr 2019 12:58:55 GMT
Transfer-Encoding: chunked

{
    "error": "Internal Server Error",
    "message": "Chaos Monkey - RuntimeException",
    "path": "/",
    "status": 500,
    "timestamp": "2019-04-16T12:58:56.378+0000"
}

HTTP/1.1 200
Content-Length: 13
Content-Type: text/plain;charset=UTF-8
Date: Tue, 16 Apr 2019 12:59:00 GMT

Hello Devoxx!
```

## Fix it !

Switch to `HYSTRIX`, re-launch maven and upload the configuration _(see previous steps)_

It's working if you can see `Hello Chaos`!

```
HTTP/1.1 200
Content-Length: 12
Content-Type: text/plain;charset=UTF-8
Date: Tue, 16 Apr 2019 13:01:15 GMT

Hello Chaos!

HTTP/1.1 200
Content-Length: 12
Content-Type: text/plain;charset=UTF-8
Date: Tue, 16 Apr 2019 13:01:16 GMT

Hello Chaos!

HTTP/1.1 200
Content-Length: 13
Content-Type: text/plain;charset=UTF-8
Date: Tue, 16 Apr 2019 13:01:16 GMT

Hello Devoxx!

HTTP/1.1 200
Content-Length: 13
Content-Type: text/plain;charset=UTF-8
Date: Tue, 16 Apr 2019 13:01:16 GMT

Hello Devoxx!
```

## And Toxiproxy?

`Toxiproxy` is a proxy disturbing your connections and managed by APIs.

A Java mapping is available and you could write a Junit Test to create a scenarised `Chaos`.

Start toxiproxy:

```
▶ toxiproxy-server
INFO[0000] API HTTP server starting                      host=localhost port=8474 version=2.1.2
```

Let's show the code ! _(go back to `CHAOS`)_

```
@Before
    public void setUp() throws Exception {
        this.base = new URL("http://localhost:" + port + "/");
        ToxiproxyClient client = new ToxiproxyClient("localhost", 8474);
        proxy = client.createProxy("HelloControllerIT", "localhost:8889", "localhost:8888");
    }
```

This code initiates the proxy.

```
    public void getHello() throws Exception {
        proxy.toxics().latency("my-latency-toxic", DOWNSTREAM, 1200).setJitter(500);
…
```

This code initiates the `Chaos` .. you can check with and without `HYSTRIX` by playing ..

```
▶ ./mvnw compile test
```