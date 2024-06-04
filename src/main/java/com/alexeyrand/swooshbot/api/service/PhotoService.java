package com.alexeyrand.swooshbot.api.service;

import com.alexeyrand.swooshbot.model.entity.publish.Photo;
import com.alexeyrand.swooshbot.api.repository.ChatRepository;
import com.alexeyrand.swooshbot.api.repository.PhotoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PhotoService {
    private final ChatRepository chatRepository;
    private final PhotoRepository photoRepository;

    public List<Photo> findAllPhotosByChatId(Long chatId) {
        return photoRepository.findAllByChatId(chatId);
    }

    public void save(Photo photo) {
        photoRepository.save(photo);
    }

    public void deleteAllByChatId(Long chatId) {
        photoRepository.deleteAllByChatId(chatId);
    }
}
