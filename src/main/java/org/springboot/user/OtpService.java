package org.springboot.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpStorageRepository otpRepo;

    @Autowired
    private EmailService emailService;

    // Generate and send OTP
    public String generateAndSendOtp(String email) throws UnsupportedEncodingException {
        String otp = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

        OtpStorage existing = otpRepo.findByEmail(email);
        if (existing != null) {
            existing.setOtp(otp);
            existing.setExpiry(expiryTime);
            otpRepo.save(existing);
        } else {
            OtpStorage storage = new OtpStorage();
            storage.setEmail(email);
            storage.setOtp(otp);
            storage.setExpiry(expiryTime);
            otpRepo.save(storage);
        }

        emailService.sendOtpEmail(email, otp);
        return "OTP sent to email";
    }

    // Verify OTP
    public boolean verifyOtp(String email, String otp) {
        OtpStorage record = otpRepo.findByEmail(email);
        if (record == null) return false;

        if (record.getOtp().equals(otp) && LocalDateTime.now().isBefore(record.getExpiry())) {
            return true;
        }
        return false;
    }
}
