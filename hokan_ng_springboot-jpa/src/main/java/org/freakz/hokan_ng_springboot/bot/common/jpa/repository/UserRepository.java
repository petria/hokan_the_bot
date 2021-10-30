package org.freakz.hokan_ng_springboot.bot.common.jpa.repository;

import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by Petri Airio on 11.3.2015.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    User findFirstByNick(String nick);

    Optional<User> findByTelegramID(int telegramId);

}
