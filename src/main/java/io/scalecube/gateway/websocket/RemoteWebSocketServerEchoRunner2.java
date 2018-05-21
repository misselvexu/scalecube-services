package io.scalecube.gateway.websocket;

import io.scalecube.services.Microservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class RemoteWebSocketServerEchoRunner2 {

  public static final Logger LOGGER = LoggerFactory.getLogger(RemoteWebSocketServerEchoRunner2.class);

  /**
   * Run test runner of Websocket server.
   * 
   * @param args - program arguments if any.
   * @throws InterruptedException - in case the program was interrupted.
   */
  public static void main(String[] args) throws InterruptedException {

    Microservices gateway = Microservices.builder()
        .build()
        .startAwait();

    Microservices.builder()
        .seeds(gateway.cluster().address())
        .services(new GreetingServiceImpl()).build().startAwait();

    WebsocketGateway2
        .builder(gateway.call().create())
        .build()
        // on instance
        .start(new InetSocketAddress(8080));

    Thread.currentThread().join();
  }

}
