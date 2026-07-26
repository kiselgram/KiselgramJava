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

public class FeaturesRoutes {

    public static void registerRoutes(Javalin app, AuthService authService, MessageService messageService,
                                      ChatService chatService, StoryService storyService,
                                      AdminService adminService) {

        app.get("/api/features", ctx -> {
            AppConfig.FeaturesSection features = AppConfig.getInstance().getFeatures();
            Map<String, Object> data = Map.of(
                    "groups", features.isGroups(),
                    "channels", features.isChannels(),
                    "bots", features.isBots(),
                    "video_streaming", features.isVideoStreaming(),
                    "file_sharing", features.isFileSharing(),
                    "reactions", features.isReactions()
            );
            ctx.json(Map.of("success", true, "data", data));
        });
    }
}
