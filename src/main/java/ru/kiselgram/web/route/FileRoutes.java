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
import io.javalin.http.UploadedFile;

import java.io.InputStream;
import java.util.Map;

public class FileRoutes {

    public static void registerRoutes(Javalin app, AuthService authService, MessageService messageService,
                                      ChatService chatService, StoryService storyService,
                                      AdminService adminService) {

        app.post("/api/upload", ctx -> {
            User user = authService.getCurrentUser(ctx);
            if (user == null) {
                ctx.status(401).json(Map.of("success", false, "error",
                        Map.of("code", "UNAUTHORIZED", "message", "Not authenticated")));
                return;
            }
            UploadedFile uploadedFile = ctx.uploadedFile("file");
            if (uploadedFile == null) {
                ctx.status(400).json(Map.of("success", false, "error",
                        Map.of("code", "INVALID_INPUT", "message", "File required")));
                return;
            }
            long fileSize;
            try (InputStream is = uploadedFile.content()) {
                fileSize = is.readAllBytes().length;
            }
            ctx.json(Map.of("success", true, "data", Map.of(
                    "file_id", 0,
                    "file_name", uploadedFile.filename(),
                    "file_size", fileSize,
                    "message", "File uploaded"
            )));
        });

        app.get("/api/files/{fileId}", ctx -> {
            User user = authService.getCurrentUser(ctx);
            if (user == null) {
                ctx.status(401).json(Map.of("success", false, "error",
                        Map.of("code", "UNAUTHORIZED", "message", "Not authenticated")));
                return;
            }
            Long fileId = ctx.pathParamAsClass("fileId", Long.class).get();
            ctx.json(Map.of("success", true, "data", Map.of("id", fileId)));
        });
    }
}
