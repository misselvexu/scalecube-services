package io.scalecube.services.gateway.clientsdk;

import java.net.InetSocketAddress;
import reactor.netty.resources.LoopResources;

public class ClientSettings {

  private static final String DEFAULT_HOST = "localhost";
  private static final String DEFAULT_CONTENT_TYPE = "application/json";

  private final String host;
  private final int port;
  private final String contentType;
  private final LoopResources loopResources;
  private final boolean followRedirect;

  private ClientSettings(Builder builder) {
    this.host = builder.host;
    this.port = builder.port;
    this.contentType = builder.contentType;
    this.loopResources = builder.loopResources;
    this.followRedirect = builder.followRedirect;
  }

  public String host() {
    return host;
  }

  public int port() {
    return port;
  }

  public String contentType() {
    return this.contentType;
  }

  public LoopResources loopResources() {
    return loopResources;
  }

  public boolean followRedirect() {
    return followRedirect;
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("ClientSettings{");
    sb.append("host='").append(host).append('\'');
    sb.append(", port=").append(port);
    sb.append(", contentType='").append(contentType).append('\'');
    sb.append(", loopResources=").append(loopResources);
    sb.append(", followRedirect=").append(followRedirect);
    sb.append('}');
    return sb.toString();
  }

  public static class Builder {
    private String host = DEFAULT_HOST;
    private int port;
    private String contentType = DEFAULT_CONTENT_TYPE;
    private LoopResources loopResources;
    private boolean followRedirect = true;

    private Builder() {}

    public Builder host(String host) {
      this.host = host;
      return this;
    }

    public Builder port(int port) {
      this.port = port;
      return this;
    }

    public Builder address(InetSocketAddress address) {
      return host(address.getHostString()).port(address.getPort());
    }

    public Builder contentType(String contentType) {
      this.contentType = contentType;
      return this;
    }

    public Builder loopResources(LoopResources loopResources) {
      this.loopResources = loopResources;
      return this;
    }

    /**
     * Specifies is auto-redirect enabled for HTTP 301/302 status codes. Enabled by default.
     *
     * @param followRedirect if <code>true</code> auto-redirect is enabled, otherwise disabled
     * @return builder
     */
    public Builder followRedirect(boolean followRedirect) {
      this.followRedirect = followRedirect;
      return this;
    }

    public ClientSettings build() {
      return new ClientSettings(this);
    }
  }
}
