package me.xentany.xspec.util;

import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class LatestVersionFetcher {

  private final String author;
  private final String repoName;
  private final HttpClient httpClient;
  private String version;

  public LatestVersionFetcher(final String author, final String repoName) {
    this.author = author;
    this.repoName = repoName;
    this.httpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .connectTimeout(Duration.ofSeconds(5))
        .build();
  }

  public CompletableFuture<Optional<String>> resolve(final boolean tryCache) {
    if (tryCache && version != null) {
      return CompletableFuture.completedFuture(Optional.of(version));
    } else {
      var url = String.format("https://api.github.com/repos/%s/%s/releases/latest", author, repoName);
      var request = HttpRequest.newBuilder()
          .GET()
          .uri(URI.create(url))
          .header("User-Agent", "Java HttpClient")
          .timeout(Duration.ofSeconds(5))
          .build();
      return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
          .thenApply(response -> {
            if (response.statusCode() == 200) {
              var resolvedVersion = parseTagName(response.body());
              resolvedVersion.ifPresent(version -> this.version = version);
              return resolvedVersion;
            }
            return Optional.<String>empty();
          })
          .exceptionally(e -> Optional.empty())
          .orTimeout(6, TimeUnit.SECONDS);
    }
  }

  private Optional<String> parseTagName(final String responseBody) {
    var parser = new JsonParser();
    var jsonElement = parser.parse(responseBody);

    if (jsonElement.isJsonObject()) {
      var jsonObject = jsonElement.getAsJsonObject();

      if (jsonObject.has("tag_name")) {
        return Optional.ofNullable(jsonObject.get("tag_name").getAsString());
      }
    }

    return Optional.empty();
  }
}