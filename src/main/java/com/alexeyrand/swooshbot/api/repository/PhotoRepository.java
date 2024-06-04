package com.alexeyrand.swooshbot.api.repository;

import com.alexeyrand.swooshbot.model.entity.publish.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findAllByChatId(Long chatId);

    void deleteAllByChatId(Long chatId);
}
