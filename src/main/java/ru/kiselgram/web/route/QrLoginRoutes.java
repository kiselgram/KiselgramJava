package ru.kiselgram.web.route;

import ru.kiselgram.web.model.User;
import ru.kiselgram.web.service.AuthService;
import ru.kiselgram.web.service.MessageService;
import ru.kiselgram.web.service.ChatService;
import ru.kiselgram.web.service.StoryService;
import ru.kiselgram.web.service.AdminService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Map;

public class QrLoginRoutes {

    private static AuthService authService;

    public static void registerRoutes(Javalin app, AuthService as, MessageService messageService,
                                      ChatService chatService, StoryService storyService,
                                      AdminService adminService) {
        authService = as;

        app.post("/api/qr/generate", ctx -> {
            User user = authService.getCurrentUser(ctx);
            if (user == null) {
                ctx.status(401).json(Map.of("success", false, "error",
                        Map.of("code", "UNAUTHORIZED", "message", "Not authenticated")));
                return;
            }
            ctx.json(Map.of("success", true, "data", Map.of("token", "qr-token-placeholder", "expires_in", 120)));
        });

        app.post("/api/auth/qr/generate", ctx -> {
            User user = authService.getCurrentUser(ctx);
            if (user == null) {
                ctx.status(401).json(Map.of("success", false, "error",
                        Map.of("code", "UNAUTHORIZED", "message", "Not authenticated")));
                return;
            }
            ctx.json(Map.of("success", true, "data", Map.of("token", "qr-token-placeholder", "expires_in", 120)));
        });

        app.post("/api/auth/qr/request", ctx -> {
            ctx.json(Map.of("success", true, "data", Map.of("token", "qr-token-placeholder", "expires_in", 120)));
        });

        app.post("/api/auth/qr/login", ctx -> {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            String token = (String) body.get("token");
            ctx.json(Map.of("success", true, "data", Map.of("session_token", "dummy-qr-session-token",
                    "user", Map.of("user_id", 1, "username", "qr_user", "display_name", "QR User", "avatar_url", ""))));
        });

        app.post("/api/auth/qr/authorize", ctx -> {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            String token = (String) body.get("token");
            if (token == null) {
                ctx.status(400).json(Map.of("success", false, "error",
                        Map.of("code", "INVALID_INPUT", "message", "Token required")));
                return;
            }
            ctx.json(Map.of("success", true, "data", Map.of("message", "QR token authorized")));
        });

        app.get("/api/auth/qr/status/{token}", ctx -> {
            String token = ctx.pathParam("token");
            ctx.json(Map.of("success", true, "data", Map.of("status", "pending", "token", token)));
        });

        app.post("/api/qr/authorize", ctx -> {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            String token = (String) body.get("token");
            if (token == null) {
                ctx.status(400).json(Map.of("success", false, "error",
                        Map.of("code", "INVALID_INPUT", "message", "Token required")));
                return;
            }
            ctx.json(Map.of("success", true, "data", Map.of("message", "QR token authorized")));
        });

        app.get("/api/qr/check/{token}", ctx -> {
            String token = ctx.pathParam("token");
            ctx.json(Map.of("success", true, "data", Map.of("status", "pending", "token", token)));
        });
    }
}
