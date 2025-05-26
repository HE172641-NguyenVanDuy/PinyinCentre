package com.pinyincentre.pinyin.schedule;

import com.pinyincentre.pinyin.repository.InvalidatedTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvalidatedTokenCleanupJob {

    private final InvalidatedTokenRepository tokenRepository;

    /**
     * Cron: 0 0 0 * * * → chạy mỗi ngày lúc 00:00
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanExpiredTokens() {
        log.info("Start scheduled task: cleanExpiredTokens");
        tokenRepository.deleteAllExpiredTokens();
        log.info("Expired invalidated tokens have been deleted.");
    }
}
