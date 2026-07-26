package ru.kiselgram.web.route;

import ru.kiselgram.web.model.User;
import ru.kiselgram.web.repository.UserRepository;
import ru.kiselgram.web.service.AuthService;
import ru.kiselgram.web.service.MessageService;
import ru.kiselgram.web.service.ChatService;
import ru.kiselgram.web.service.StoryService;
import ru.kiselgram.web.service.AdminService;
import ru.kiselgram.web.util.Helpers;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.*;

public class ProfileRoutes {

    private static AuthService authService;

    public static void registerRoutes(Javalin app, AuthService as, MessageService messageService,
                                      ChatService chatService, StoryService storyService,
                                      AdminService adminService) {
        authService = as;

        app.get("/api/profile", ctx -> {
            User user = authService.getCurrentUser(ctx);
            if (user == null) {
                ctx.status(401).json(Map.of("success", false, "error",
                        Map.of("code", "UNAUTHORIZED", "message", "Not authenticated")));
                return;
            }
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("user_id", user.getId());
            data.put("username", user.getUsername());
            data.put("display_name", user.getDisplayName());
            data.put("email", user.getEmail());
            data.put("email_verified", user.isEmailVerified());
            data.put("avatar_url", user.getAvatarUrl());
            data.put("bio", user.getBio());
            data.put("status_emoji", user.getStatusEmoji());
            data.put("is_premium", false);
            data.put("is_admin", user.isAdmin());
            ctx.json(Map.of("success", true, "data", data));
        });

        app.put("/api/profile", ctx -> {
            User user = authService.getCurrentUser(ctx);
            if (user == null) {
                ctx.status(401).json(Map.of("success", false, "error",
                        Map.of("code", "UNAUTHORIZED", "message", "Not authenticated")));
                return;
            }
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            if (body.containsKey("display_name")) user.setDisplayName((String) body.get("display_name"));
            if (body.containsKey("bio")) user.setBio((String) body.get("bio"));
            if (body.containsKey("avatar_url")) user.setAvatarUrl((String) body.get("avatar_url"));
            ctx.json(Map.of("success", true, "data", user.toMap()));
        });

        app.get("/api/user/{userId}", ctx -> {
            User currentUser = authService.getCurrentUser(ctx);
            if (currentUser == null) {
                ctx.status(401).json(Map.of("success", false, "error",
                        Map.of("code", "UNAUTHORIZED", "message", "Not authenticated")));
                return;
            }
            Long userId = ctx.pathParamAsClass("userId", Long.class).get();
            ctx.json(Map.of("success", true, "data", Map.of("id", userId)));
        });

        app.put("/api/profile/settings", ctx -> {
            User user = authService.getCurrentUser(ctx);
            if (user == null) {
                ctx.status(401).json(Map.of("success", false, "error",
                        Map.of("code", "UNAUTHORIZED", "message", "Not authenticated")));
                return;
            }
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            if (body.containsKey("theme")) user.setTheme((String) body.get("theme"));
            if (body.containsKey("font_size")) user.setFontSize((int) body.get("font_size"));
            if (body.containsKey("bubble_radius")) user.setBubbleRadius((int) body.get("bubble_radius"));
            if (body.containsKey("font_family")) user.setFontFamily((String) body.get("font_family"));
            if (body.containsKey("my_message_color")) user.setMyMessageColor((String) body.get("my_message_color"));
            if (body.containsKey("their_message_color")) user.setTheirMessageColor((String) body.get("their_message_color"));
            if (body.containsKey("wallpaper")) user.setWallpaper((String) body.get("wallpaper"));
            ctx.json(Map.of("success", true, "data", user.toMap()));
        });

        app.get("/api/profile/privacy", ctx -> {
            User user = authService.getCurrentUser(ctx);
            if (user == null) {
                ctx.status(401).json(Map.of("success", false, "error",
                        Map.of("code", "UNAUTHORIZED", "message", "Not authenticated")));
                return;
            }
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("last_seen", "everyone");
            data.put("profile_photo", "everyone");
            data.put("forward", "everyone");
            data.put("calls", "everyone");
            ctx.json(Map.of("success", true, "data", data));
        });

        app.put("/api/profile/privacy", ctx -> {
            User user = authService.getCurrentUser(ctx);
            if (user == null) {
                ctx.status(401).json(Map.of("success", false, "error",
                        Map.of("code", "UNAUTHORIZED", "message", "Not authenticated")));
                return;
            }
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            if (body.containsKey("privacy_last_seen")) user.setPrivacyLastSeen((String) body.get("privacy_last_seen"));
            if (body.containsKey("privacy_photo")) user.setPrivacyPhoto((String) body.get("privacy_photo"));
            if (body.containsKey("privacy_forward")) user.setPrivacyForward((String) body.get("privacy_forward"));
            if (body.containsKey("privacy_calls")) user.setPrivacyCalls((String) body.get("privacy_calls"));
            ctx.json(Map.of("success", true, "data", user.toMap()));
        });

        app.put("/api/profile/notifications", ctx -> {
            User user = authService.getCurrentUser(ctx);
            if (user == null) {
                ctx.status(401).json(Map.of("success", false, "error",
                        Map.of("code", "UNAUTHORIZED", "message", "Not authenticated")));
                return;
            }
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            if (body.containsKey("notification_sound")) user.setNotificationSound((String) body.get("notification_sound"));
            if (body.containsKey("mute_all")) user.setMuteAll((boolean) body.get("mute_all"));
            if (body.containsKey("do_not_disturb")) user.setDoNotDisturb((boolean) body.get("do_not_disturb"));
            ctx.json(Map.of("success", true, "data", user.toMap()));
        });

        app.get("/api/users", ctx -> {
            String search = ctx.queryParam("search");
            UserRepository repo = new UserRepository();
            List<User> users;
            if (search != null && !search.isBlank()) {
                users = repo.search(search, 1, 50);
            } else {
                users = repo.findAll(1, 50);
            }
            ctx.json(Map.of("success", true, "users",
                users.stream().map(u -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", u.getId());
                    m.put("username", u.getUsername());
                    m.put("display_name", u.getDisplayName());
                    m.put("avatar_url", u.getAvatarUrl());
                    m.put("bio", u.getBio());
                    return m;
                }).toList()));
        });
    }
}
