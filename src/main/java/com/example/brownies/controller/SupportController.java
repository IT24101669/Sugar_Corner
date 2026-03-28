package com.example.brownies.controller;

import com.example.brownies.model.AdminSettings;
import com.example.brownies.model.ChatMessage;
import com.example.brownies.model.Feedback;
import com.example.brownies.model.Inquiry;
import com.example.brownies.repository.AdminSettingsRepository;
import com.example.brownies.repository.FeedbackRepository;
import com.example.brownies.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/support")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@RequiredArgsConstructor
public class SupportController {

    private final FeedbackRepository feedbackRepo;
    private final InquiryRepository inquiryRepo;
    private final SimpMessagingTemplate messagingTemplate;
    private final AdminSettingsRepository adminSettingsRepo;

    // ============================
    // 1. SUBMIT FEEDBACK
    // ============================
    @PostMapping("/feedback")
    public ResponseEntity<?> submitFeedback(@RequestBody Feedback feedback) {

        if (feedback.getMessage() == null || feedback.getMessage().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Feedback message is required"));
        }

        feedback.setSubmittedAt(LocalDateTime.now());
        feedback.setStatus("New");

        feedbackRepo.save(feedback);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Feedback received!"
        ));
    }

    // ============================
    // 2. SUBMIT INQUIRY
    // ============================
    @PostMapping("/inquiry")
    public ResponseEntity<?> submitInquiry(@RequestBody Inquiry inquiry) {

        if (inquiry.getInquiryText() == null || inquiry.getInquiryText().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Inquiry text is required"));
        }

        inquiry.setSubmittedAt(LocalDateTime.now());
        inquiry.setStatus("New");

        Inquiry saved = inquiryRepo.save(inquiry);
        messagingTemplate.convertAndSend("/topic/admin/notifications", saved);

        return ResponseEntity.ok(saved); // return ID for chat
    }

    // ============================
    // 3. WEBSOCKET CHAT (USER → ADMIN)
    // ============================

    // ============================
    // 4. GET USER INQUIRIES
    // ============================
    @GetMapping("/inquiry/by-email")
    public List<Inquiry> getInquiriesByEmail(@RequestParam String email) {
        return inquiryRepo.findByEmail(email);
    }

    // ============================
    // 5. ADMIN DATA
    // ============================
    @GetMapping("/feedback/all")
    public ResponseEntity<List<Feedback>> getAllFeedback() {
        return ResponseEntity.ok(
                feedbackRepo.findAllByOrderBySubmittedAtDesc()
        );
    }

    @GetMapping("/inquiry/all")
    public ResponseEntity<List<Inquiry>> getAllInquiries() {
        return ResponseEntity.ok(
                inquiryRepo.findAllByOrderBySubmittedAtDesc()
        );
    }

    // ============================
    // 6. STATS
    // ============================
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {

        Map<String, Object> stats = new HashMap<>();
        stats.put("feedbackTotal", feedbackRepo.count());
        stats.put("inquiryTotal", inquiryRepo.count());

        return ResponseEntity.ok(stats);
    }

    // ============================
    // 7. ADMIN REPLY → FEEDBACK
    // ============================
    @PutMapping("/feedback/{id}/reply")
    public ResponseEntity<?> replyToFeedback(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String replyText = body.get("reply");

        if (replyText == null || replyText.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Reply text is required"));
        }

        Feedback feedback = feedbackRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        feedback.setAdminReply(replyText);
        feedback.setRepliedAt(LocalDateTime.now());
        feedback.setRepliedByAdmin("Admin");
        feedback.setStatus("Resolved");

        feedbackRepo.save(feedback);

        return ResponseEntity.ok(Map.of("success", true));
    }

    // ============================
    // 8. ADMIN REPLY → INQUIRY (WITH LIVE CHAT)
    // ============================
    @PutMapping("/inquiry/{id}/reply")
    public ResponseEntity<?> replyToInquiry(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String reply = payload.get("reply");

        Inquiry inquiry = inquiryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Inquiry not found"));

        inquiry.setAdminReply(reply);
        inquiry.setStatus("Resolved");
        inquiry.setRepliedAt(LocalDateTime.now());
        inquiryRepo.save(inquiry);

        // WebSocket එක හරහා Admin ගේ මැසේජ් එක Customer ට (සහ Admin ගේම screen එකට) යවනවා
        ChatMessage adminMsg = new ChatMessage();
        adminMsg.setInquiryId(id);
        adminMsg.setSender("admin");
        adminMsg.setContent(reply);
        adminMsg.setTimestamp(LocalDateTime.now().toString());

        messagingTemplate.convertAndSend("/topic/inquiry/" + id, adminMsg);

        return ResponseEntity.ok(Map.of("success", true));
    }
    @MessageMapping("/chat.sendMessage")
    public void handleCustomerMessage(@Payload ChatMessage chatMessage) {
        messagingTemplate.convertAndSend("/topic/inquiry/" + chatMessage.getInquiryId(), chatMessage);
    }
    // ඇඩ්මින් විස්තර ලබා ගැනීම
    @GetMapping("/settings")
    public ResponseEntity<AdminSettings> getSettings() {
        return ResponseEntity.ok(adminSettingsRepo.findById(1L).orElse(new AdminSettings()));
    }

    // ඇඩ්මින් විස්තර යාවත්කාලීන කිරීම (Update)
    @PutMapping("/settings")
    public ResponseEntity<?> updateSettings(@RequestBody AdminSettings newSettings) {
        AdminSettings existing = adminSettingsRepo.findById(1L).orElse(new AdminSettings());
        existing.setAdminEmail(newSettings.getAdminEmail());
        existing.setAdminPhone(newSettings.getAdminPhone());
        existing.setAdminAddress(newSettings.getAdminAddress());
        adminSettingsRepo.save(existing);
        return ResponseEntity.ok(Map.of("message", "Settings updated successfully"));
    }
    // 1. Feedback මකා දැමීම
    @DeleteMapping("/feedback/{id}")
    public ResponseEntity<?> deleteFeedback(@PathVariable Long id) {
        if (feedbackRepo.existsById(id)) {
            feedbackRepo.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Feedback deleted successfully"));
        }
        return ResponseEntity.notFound().build();
    }

    // 2. Inquiry මකා දැමීම
    @DeleteMapping("/inquiry/{id}")
    public ResponseEntity<?> deleteInquiry(@PathVariable Long id) {
        if (inquiryRepo.existsById(id)) {
            inquiryRepo.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Inquiry deleted successfully"));
        }
        return ResponseEntity.notFound().build();
    }

}
