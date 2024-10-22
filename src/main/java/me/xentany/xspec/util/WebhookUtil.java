package me.xentany.xspec.util;

import me.xentany.xspec.SpecPlugin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class WebhookUtil {

  private static HttpClient httpClient;

  public static void load() {
    WebhookUtil.httpClient = HttpClient.newHttpClient();
  }

  public static void sendWebhookAsync(final String url, final String payload) {
    try {
      var request = HttpRequest.newBuilder()
          .uri(URI.create(url))
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(payload))
          .build();
      httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
          .thenAccept(response -> {
            int statusCode = response.statusCode();
            if (statusCode < 200 || statusCode >= 300) {
              SpecPlugin.getInstance().getLogger().warning("Failed to send webhook. Status code: " + statusCode);
            }
          })
          .exceptionally(e -> {
            SpecPlugin.getInstance().getLogger().severe("Error occurred while sending webhook: " + e.getMessage());
            return null;
          });
    } catch (final Exception e) {
      SpecPlugin.getInstance().getLogger().severe("Error occurred while sending webhook: " + e.getMessage());
    }
  }
}