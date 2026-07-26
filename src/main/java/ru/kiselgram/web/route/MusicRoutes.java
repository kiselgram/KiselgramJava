package ru.kiselgram.web.route;

import ru.kiselgram.web.model.User;
import ru.kiselgram.web.service.AuthService;
import ru.kiselgram.web.service.MessageService;
import ru.kiselgram.web.service.ChatService;
import ru.kiselgram.web.service.StoryService;
import ru.kiselgram.web.service.AdminService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

public class MusicRoutes {

    public static void registerRoutes(Javalin app, AuthService authService, MessageService messageService,
                                      ChatService chatService, StoryService storyService,
                                      AdminService adminService) {

        app.get("/api/music", ctx -> {
            User user = authService.getCurrentUser(ctx);
            if (user == null) {
                ctx.status(401).json(Map.of("success", false, "error",
                        Map.of("code", "UNAUTHORIZED", "message", "Not authenticated")));
                return;
            }
            ctx.json(Map.of("success", true, "data", List.of()));
        });

        app.post("/api/music", ctx -> {
            User user = authService.getCurrentUser(ctx);
            if (user == null) {
                ctx.status(401).json(Map.of("success", false, "error",
                        Map.of("code", "UNAUTHORIZED", "message", "Not authenticated")));
                return;
            }
            ctx.json(Map.of("success", true, "data", Map.of("message", "Music added")));
        });
    }
}
