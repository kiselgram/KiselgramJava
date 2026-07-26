package ru.kiselgram.web.route;

import ru.kiselgram.web.config.AppConfig;
import ru.kiselgram.web.model.User;
import ru.kiselgram.web.service.AuthService;
import ru.kiselgram.web.service.MessageService;
import ru.kiselgram.web.service.ChatService;
import ru.kiselgram.web.service.StoryService;
import ru.kiselgram.web.service.AdminService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Map;

public class OAuthRoutes {

    private static AuthService authService;

    public static void registerRoutes(Javalin app, AuthService as, MessageService messageService,
                                      ChatService chatService, StoryService storyService,
                                      AdminService adminService) {
        authService = as;

        app.get("/api/auth/google/login", ctx -> {
            String clientId = AppConfig.getInstance().getGoogle().getClientId();
            String redirectUri = "http://localhost:8080/api/auth/google/callback";
            String googleUrl = "https://accounts.google.com/o/oauth2/v2/auth?"
                    + "client_id=" + clientId
                    + "&redirect_uri=" + redirectUri
                    + "&response_type=code"
                    + "&scope=openid%20email%20profile";
            ctx.redirect(googleUrl);
        });

        app.get("/api/auth/google/callback", ctx -> {
            String code = ctx.queryParam("code");
            if (code == null) {
                ctx.status(400).json(Map.of("success", false, "error",
                        Map.of("code", "INVALID_INPUT", "message", "Authorization code required")));
                return;
            }
            Map<String, Object> result = authService.googleOAuth(code);
            if (result.containsKey("error")) {
                ctx.status(400).json(Map.of("success", false, "error",
                        Map.of("code", "OAUTH_FAILED", "message", result.get("error"))));
                return;
            }
            ctx.json(Map.of("success", true, "data", result));
        });
    }
}
