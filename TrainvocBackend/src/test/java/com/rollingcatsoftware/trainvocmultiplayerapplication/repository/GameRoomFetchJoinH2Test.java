package com.rollingcatsoftware.trainvocmultiplayerapplication.repository;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Regression guard for the Hibernate 7.3 semantics change that ships with Spring Boot 4.1
 * (Hibernate ORM 7.2 → 7.4): {@code Query#getSingleResult()} now always throws when more
 * than one element remains in the result list — with no implicit deduplication at that stage.
 * <p>
 * {@code RoomService} relies on the exact query exercised here (a {@code LEFT JOIN FETCH
 * r.players} re-fetch after room creation) returning ONE room even though the SQL result set
 * has one row per player. Root-entity deduplication for collection fetch joins must keep
 * handling that; this test pins the behavior against a real (H2) database with two players
 * in a single room, so any future Hibernate bump that breaks it fails loudly here instead of
 * at runtime in the WebSocket create-room flow.
 */
@SpringBootTest(classes = GameRoomFetchJoinH2Test.Config.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:fetchjoin;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.jdbc-url=jdbc:h2:mem:fetchjoin;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
@DisplayName("GameRoom LEFT JOIN FETCH + getSingleResult (H2, Hibernate 7.4)")
class GameRoomFetchJoinH2Test {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = GameRoom.class)
    @EnableJpaRepositories(basePackageClasses = GameRoomRepository.class,
            excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX,
                    pattern = ".*\\.repository\\.word\\..*"))
    static class Config {
    }

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    @DisplayName("returns one room with both players despite a two-row SQL result")
    void fetchJoin_getSingleResult_dedupesRootEntity() {
        String roomCode = "HB741";

        EntityManager write = entityManagerFactory.createEntityManager();
        try {
            write.getTransaction().begin();
            GameRoom room = new GameRoom();
            room.setRoomCode(roomCode);
            room.setStarted(false);
            room.setCurrentState(GameState.LOBBY);
            room.setLastUsed(LocalDateTime.now());
            write.persist(room);
            for (String name : List.of("host", "guest")) {
                Player player = new Player();
                player.setId(UUID.randomUUID().toString());
                player.setRoom(room);
                player.setName(name);
                write.persist(player);
            }
            write.getTransaction().commit();
        } finally {
            write.close();
        }

        EntityManager read = entityManagerFactory.createEntityManager();
        try {
            // The exact query shape RoomService.createRoom uses for its post-commit re-fetch.
            GameRoom fetched = read.createQuery(
                            "SELECT r FROM GameRoom r LEFT JOIN FETCH r.players WHERE r.roomCode = :code",
                            GameRoom.class)
                    .setParameter("code", roomCode)
                    .getSingleResult();
            assertEquals(roomCode, fetched.getRoomCode());
            assertEquals(2, fetched.getPlayers().size());
        } finally {
            read.close();
        }
    }
}
