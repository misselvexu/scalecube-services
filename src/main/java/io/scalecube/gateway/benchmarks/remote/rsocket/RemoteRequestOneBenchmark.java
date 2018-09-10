package io.scalecube.gateway.benchmarks.remote.rsocket;

import io.scalecube.gateway.benchmarks.RequestOneBenchmark;
import io.scalecube.gateway.benchmarks.remote.RemoteBenchmarkState;
import io.scalecube.gateway.clientsdk.Client;
import io.scalecube.gateway.clientsdk.ClientSettings;
import reactor.core.publisher.Mono;

public class RemoteRequestOneBenchmark {

  /**
   * Main runner.
   *
   * @param args program arguments
   */
  public static void main(String[] args) {
    RequestOneBenchmark.runWith(
        args,
        benchmarkSettings ->
            new RemoteBenchmarkState(
                benchmarkSettings,
                (address, loopResources) ->
                    Mono.just(
                        Client.onRSocket(
                            ClientSettings.builder()
                                .address(address)
                                .loopResources(loopResources)
                                .build()))));
  }
}
