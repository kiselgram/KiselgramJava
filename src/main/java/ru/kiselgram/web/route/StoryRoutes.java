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

public class StoryRoutes {

    private static AuthService authService;
    private static StoryService storyService;

    public static void registerRoutes(Javalin app, AuthService as, MessageService messageService,
                                      ChatService chatService, StoryService ss,
                                      AdminService adminService) {
        authService = as;
        storyService = ss;

        app.post("/api/stories/create", ctx -> {
            User user = authService.getCurrentUser(ctx);
            if (user == null) {
                ctx.status(401).json(Map.of("success", false, "error",
                        Map.of("code", "UNAUTHORIZED", "message", "Not authenticated")));
                return;
            }
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            String mediaPath = (String) body.get("media_path");
            String mediaType = (String) body.get("media_type");
            String caption = (String) body.get("caption");
            if (mediaPath == null || mediaType == null) {
                ctx.status(400).json(Map.of("success", false, "error",
                        Map.of("code", "INVALID_INPUT", "message", "Media path and type required")));
                return;
            }
            Map<String, Object> result = storyService.createStory(user.getId(), mediaPath, mediaType, caption);
            if (result.containsKey("error")) {
                ctx.status(400).json(Map.of("success", false, "error",
                        Map.of("code", "CREATE_FAILED", "message", result.get("error"))));
                return;
            }
            ctx.json(Map.of("success", true, "data", result));
        });

        app.get("/api/stories", ctx -> {
            User user = authService.getCurrentUser(ctx);
            if (user == null) {
                ctx.status(401).json(Map.of("success", false, "error",
                        Map.of("code", "UNAUTHORIZED", "message", "Not authenticated")));
                return;
            }
            Map<String, Object> result = storyService.getActiveStories(user.getId());
            if (result.containsKey("error")) {
                ctx.status(400).json(Map.of("success", false, "error",
                        Map.of("code", "FETCH_FAILED", "message", result.get("error"))));
                return;
            }
            ctx.json(Map.of("success", true, "data", result));
        });

        app.post("/api/stories/{storyId}/view", ctx -> {
            User user = authService.getCurrentUser(ctx);
            if (user == null) {
                ctx.status(401).json(Map.of("success", false, "error",
                        Map.of("code", "UNAUTHORIZED", "message", "Not authenticated")));
                return;
            }
            Long storyId = ctx.pathParamAsClass("storyId", Long.class).get();
            Map<String, Object> result = storyService.viewStory(storyId, user.getId());
            if (result.containsKey("error")) {
                ctx.status(400).json(Map.of("success", false, "error",
                        Map.of("code", "VIEW_FAILED", "message", result.get("error"))));
                return;
            }
            ctx.json(Map.of("success", true, "data", result));
        });

        app.post("/api/stories/{storyId}/like", ctx -> {
            User user = authService.getCurrentUser(ctx);
            if (user == null) {
                ctx.status(401).json(Map.of("success", false, "error",
                        Map.of("code", "UNAUTHORIZED", "message", "Not authenticated")));
                return;
            }
            Long storyId = ctx.pathParamAsClass("storyId", Long.class).get();
            Map<String, Object> result = storyService.likeStory(storyId, user.getId());
            if (result.containsKey("error")) {
                ctx.status(400).json(Map.of("success", false, "error",
                        Map.of("code", "LIKE_FAILED", "message", result.get("error"))));
                return;
            }
            ctx.json(Map.of("success", true, "data", result));
        });
    }
}
