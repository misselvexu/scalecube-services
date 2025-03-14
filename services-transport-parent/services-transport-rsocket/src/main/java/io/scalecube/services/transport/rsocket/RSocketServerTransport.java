package io.scalecube.services.transport.rsocket;

import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.ByteBufPayload;
import io.scalecube.net.Address;
import io.scalecube.services.methods.ServiceMethodRegistry;
import io.scalecube.services.transport.api.ServerTransport;
import io.scalecube.services.transport.api.ServiceMessageCodec;
import java.net.InetSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.resources.LoopResources;
import reactor.netty.tcp.TcpServer;

/** RSocket server transport implementation. */
public class RSocketServerTransport implements ServerTransport {

  private static final Logger LOGGER = LoggerFactory.getLogger(RSocketServerTransport.class);

  private final ServiceMessageCodec codec;
  private final TcpServer tcpServer;

  private CloseableChannel server; // calculated

  /**
   * Constructor for this server transport.
   *
   * @param codec message codec
   * @param tcpServer tcp server
   */
  public RSocketServerTransport(ServiceMessageCodec codec, TcpServer tcpServer) {
    this.codec = codec;
    this.tcpServer = tcpServer;
  }

  @Override
  public Address address() {
    InetSocketAddress address = server.address();
    return Address.create(address.getHostString(), address.getPort());
  }

  @Override
  public Mono<ServerTransport> bind(ServiceMethodRegistry methodRegistry) {
    return Mono.defer(
        () -> {
          TcpServer tcpServer =
              this.tcpServer.doOnConnection(
                  connection -> {
                    LOGGER.info("Accepted connection on {}", connection.channel());
                    connection.onDispose(
                        () -> LOGGER.info("Connection closed on {}", connection.channel()));
                  });

          return RSocketFactory.receive()
              .frameDecoder(
                  frame ->
                      ByteBufPayload.create(
                          frame.sliceData().retain(), frame.sliceMetadata().retain()))
              .errorConsumer(
                  th -> LOGGER.warn("Exception occurred at rsocket server transport: " + th))
              .acceptor(new RSocketServiceAcceptor(codec, methodRegistry))
              .transport(() -> TcpServerTransport.create(tcpServer))
              .start()
              .doOnSuccess(channel -> this.server = channel)
              .thenReturn(this);
        });
  }

  @Override
  public Mono<Void> stop() {
    return Mono.defer(
        () -> {
          if (server == null) {
            return Mono.empty();
          }
          server.dispose();
          return server.onClose().doOnError(e -> LOGGER.warn("Failed to close server: " + e));
        });
  }
}
