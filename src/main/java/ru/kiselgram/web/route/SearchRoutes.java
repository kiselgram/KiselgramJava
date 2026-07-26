package ru.kiselgram.web.route;

import ru.kiselgram.web.model.User;
import ru.kiselgram.web.service.AuthService;
import ru.kiselgram.web.service.MessageService;
import ru.kiselgram.web.service.ChatService;
import ru.kiselgram.web.service.StoryService;
import ru.kiselgram.web.service.AdminService;
import ru.kiselgram.web.util.Helpers;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

public class SearchRoutes {

    public static void registerRoutes(Javalin app, AuthService authService, MessageService messageService,
                                      ChatService chatService, StoryService storyService,
                                      AdminService adminService) {

        app.get("/api/search", ctx -> {
            User user = authService.getCurrentUser(ctx);
            if (user == null) {
                ctx.status(401).json(Map.of("success", false, "error",
                        Map.of("code", "UNAUTHORIZED", "message", "Not authenticated")));
                return;
            }
            String query = ctx.queryParam("q");
            if (query == null || query.isBlank()) {
                ctx.status(400).json(Map.of("success", false, "error",
                        Map.of("code", "INVALID_INPUT", "message", "Query required")));
                return;
            }
            int page = parseIntParam(ctx.queryParam("page"), 1);
            int perPage = Math.min(parseIntParam(ctx.queryParam("per_page"), 50), 100);
            ctx.json(Map.of("success", true, "data", Map.of("users", List.of(), "query", query)));
        });
    }

    private static int parseIntParam(String val, int def) {
        if (val == null) return def;
        try { return Integer.parseInt(val); } catch (NumberFormatException e) { return def; }
    }
}
