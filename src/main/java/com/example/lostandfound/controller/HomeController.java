package com.example.lostandfound.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<String> home() {
        String html = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Lost & Found API</title>
                <style>
                    body { font-family: system-ui, -apple-system, sans-serif; background: #0f172a; color: #f8fafc; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }
                    .card { background: #1e293b; padding: 2.5rem; border-radius: 16px; box-shadow: 0 20px 25px -5px rgba(0,0,0,0.5); max-width: 480px; text-align: center; border: 1px solid #334155; }
                    h1 { color: #38bdf8; margin: 0 0 0.5rem 0; font-size: 1.75rem; }
                    p { color: #94a3b8; font-size: 0.95rem; line-height: 1.5; }
                    .badge { display: inline-block; background: #166534; color: #4ade80; padding: 0.35rem 0.85rem; border-radius: 9999px; font-weight: 600; font-size: 0.85rem; margin-bottom: 1.25rem; }
                    .btn { display: inline-block; background: #0284c7; color: white; text-decoration: none; padding: 0.75rem 1.5rem; border-radius: 8px; font-weight: 600; margin-top: 1rem; transition: background 0.2s; }
                    .btn:hover { background: #0369a1; }
                </style>
            </head>
            <body>
                <div class="card">
                    <h1>Lost & Found Backend API</h1>
                    <div class="badge">● Server Online (Port 8080)</div>
                    <p>Spring Boot backend is running. Use the link below to access the database management console.</p>
                    <a href="/h2-console" class="btn">Open H2 Database Console</a>
                </div>
            </body>
            </html>
            """;
        return ResponseEntity.ok().header("Content-Type", "text/html").body(html);
    }
}
