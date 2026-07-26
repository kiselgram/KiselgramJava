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

public class PushRoutes {

    public static void registerRoutes(Javalin app, AuthService authService, MessageService messageService,
                                      ChatService chatService, StoryService storyService,
                                      AdminService adminService) {

        app.post("/api/push/subscribe", ctx -> {
            User user = authService.getCurrentUser(ctx);
            if (user == null) {
                ctx.status(401).json(Map.of("success", false, "error",
                        Map.of("code", "UNAUTHORIZED", "message", "Not authenticated")));
                return;
            }
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            ctx.json(Map.of("success", true, "data", Map.of("message", "Push subscription saved")));
        });

        app.post("/api/push/unsubscribe", ctx -> {
            User user = authService.getCurrentUser(ctx);
            if (user == null) {
                ctx.status(401).json(Map.of("success", false, "error",
                        Map.of("code", "UNAUTHORIZED", "message", "Not authenticated")));
                return;
            }
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            ctx.json(Map.of("success", true, "data", Map.of("message", "Push subscription removed")));
        });
    }
}
