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

public class KSettingsRoutes {

    public static void registerRoutes(Javalin app, AuthService authService, MessageService messageService,
                                      ChatService chatService, StoryService storyService,
                                      AdminService adminService) {

        app.get("/api/k_settings", ctx -> {
            User user = authService.getCurrentUser(ctx);
            if (user == null) {
                ctx.status(401).json(Map.of("success", false, "error",
                        Map.of("code", "UNAUTHORIZED", "message", "Not authenticated")));
                return;
            }
            ctx.json(Map.of("success", true, "data", Map.of(
                    "theme", user.getTheme(),
                    "font_size", user.getFontSize(),
                    "font_family", user.getFontFamily()
            )));
        });

        app.put("/api/k_settings", ctx -> {
            User user = authService.getCurrentUser(ctx);
            if (user == null) {
                ctx.status(401).json(Map.of("success", false, "error",
                        Map.of("code", "UNAUTHORIZED", "message", "Not authenticated")));
                return;
            }
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            ctx.json(Map.of("success", true, "data", Map.of("message", "K settings updated")));
        });

        app.get("/api/k/settings", ctx -> {
            ctx.json(Map.of("success", true, "data", Map.of()));
        });

        app.put("/api/k/settings", ctx -> {
            ctx.json(Map.of("success", true, "data", Map.of("message", "K settings updated")));
        });
    }
}
