package com.user.payload.request;

public record ResetPasswordRequest(String email, String otp, String newPassword) {
}