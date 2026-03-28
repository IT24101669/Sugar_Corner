package com.example.brownies.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private Long inquiryId;
    private String sender;
    private String content;
    private String timestamp; // අවශ්‍ය නම් වෙලාව පෙන්නන්න පුළුවන්
}