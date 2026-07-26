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

public class ContactRoutes {

    private static AuthService authService;
    private static ChatService chatService;

    public static void registerRoutes(Javalin app, AuthService as, MessageService messageService,
                                      ChatService cs, StoryService storyService,
                                      AdminService adminService) {
        authService = as;
        chatService = cs;

        app.get("/api/contacts", ctx -> {
            User user = authService.getCurrentUser(ctx);
            if (user == null) {
                ctx.status(401).json(Map.of("success", false, "error",
                        Map.of("code", "UNAUTHORIZED", "message", "Not authenticated")));
                return;
            }
            ctx.json(Map.of("success", true, "data", List.of()));
        });

        app.post("/api/contacts", ctx -> {
            User user = authService.getCurrentUser(ctx);
            if (user == null) {
                ctx.status(401).json(Map.of("success", false, "error",
                        Map.of("code", "UNAUTHORIZED", "message", "Not authenticated")));
                return;
            }
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            Long contactId = body.get("contact_id") != null ? ((Number) body.get("contact_id")).longValue() : null;
            if (contactId == null) {
                ctx.status(400).json(Map.of("success", false, "error",
                        Map.of("code", "INVALID_INPUT", "message", "Contact ID required")));
                return;
            }
            Map<String, Object> result = chatService.addContact(user.getId(), contactId);
            if (result.containsKey("error")) {
                ctx.status(400).json(Map.of("success", false, "error",
                        Map.of("code", "ADD_FAILED", "message", result.get("error"))));
                return;
            }
            ctx.json(Map.of("success", true, "data", result));
        });

        app.post("/api/contacts/rename", ctx -> {
            User user = authService.getCurrentUser(ctx);
            if (user == null) {
                ctx.status(401).json(Map.of("success", false, "error",
                        Map.of("code", "UNAUTHORIZED", "message", "Not authenticated")));
                return;
            }
            ctx.json(Map.of("success", true, "data", Map.of("message", "Contact renamed")));
        });

        app.delete("/api/contacts/{contactId}", ctx -> {
            User user = authService.getCurrentUser(ctx);
            if (user == null) {
                ctx.status(401).json(Map.of("success", false, "error",
                        Map.of("code", "UNAUTHORIZED", "message", "Not authenticated")));
                return;
            }
            Long contactId = ctx.pathParamAsClass("contactId", Long.class).get();
            Map<String, Object> result = chatService.removeContact(user.getId(), contactId);
            if (result.containsKey("error")) {
                ctx.status(400).json(Map.of("success", false, "error",
                        Map.of("code", "REMOVE_FAILED", "message", result.get("error"))));
                return;
            }
            ctx.json(Map.of("success", true, "data", result));
        });

        app.get("/api/blocked_users", ctx -> {
            User user = authService.getCurrentUser(ctx);
            if (user == null) {
                ctx.status(401).json(Map.of("success", false, "error",
                        Map.of("code", "UNAUTHORIZED", "message", "Not authenticated")));
                return;
            }
            ctx.json(Map.of("success", true, "data", List.of()));
        });

        app.post("/api/block_user/{userId}", ctx -> {
            User user = authService.getCurrentUser(ctx);
            if (user == null) {
                ctx.status(401).json(Map.of("success", false, "error",
                        Map.of("code", "UNAUTHORIZED", "message", "Not authenticated")));
                return;
            }
            Long blockedId = ctx.pathParamAsClass("userId", Long.class).get();
            Map<String, Object> result = chatService.blockUser(user.getId(), blockedId);
            if (result.containsKey("error")) {
                ctx.status(400).json(Map.of("success", false, "error",
                        Map.of("code", "BLOCK_FAILED", "message", result.get("error"))));
                return;
            }
            ctx.json(Map.of("success", true, "data", result));
        });

        app.post("/api/unblock_user/{userId}", ctx -> {
            User user = authService.getCurrentUser(ctx);
            if (user == null) {
                ctx.status(401).json(Map.of("success", false, "error",
                        Map.of("code", "UNAUTHORIZED", "message", "Not authenticated")));
                return;
            }
            Long blockedId = ctx.pathParamAsClass("userId", Long.class).get();
            Map<String, Object> result = chatService.unblockUser(user.getId(), blockedId);
            if (result.containsKey("error")) {
                ctx.status(400).json(Map.of("success", false, "error",
                        Map.of("code", "UNBLOCK_FAILED", "message", result.get("error"))));
                return;
            }
            ctx.json(Map.of("success", true, "data", result));
        });
    }
}
