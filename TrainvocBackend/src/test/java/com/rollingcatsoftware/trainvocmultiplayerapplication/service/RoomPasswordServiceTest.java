package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.exception.RoomPasswordException;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoomPasswordService Tests")
class RoomPasswordServiceTest {

    @Mock
    private RoomService roomService;

    @InjectMocks
    private RoomPasswordService passwordService;

    private GameRoom roomWithPassword;
    private GameRoom roomWithoutPassword;
    private static final String ROOM_CODE = "ABC123";
    private static final String CORRECT_HASH = "hashedPassword123";

    @BeforeEach
    void setUp() {
        roomWithPassword = new GameRoom();
        roomWithPassword.setRoomCode(ROOM_CODE);
        roomWithPassword.setHashedPassword(CORRECT_HASH);

        roomWithoutPassword = new GameRoom();
        roomWithoutPassword.setRoomCode(ROOM_CODE);
        roomWithoutPassword.setHashedPassword(null);
    }

    @Nested
    @DisplayName("checkPassword")
    class CheckPassword {

        @Test
        @DisplayName("returns true when password matches")
        void returnsTrue_whenPasswordMatches() {
            when(roomService.findByRoomCode(ROOM_CODE)).thenReturn(roomWithPassword);

            boolean result = passwordService.checkPassword(ROOM_CODE, CORRECT_HASH);

            assertTrue(result);
            verify(roomService).findByRoomCode(ROOM_CODE);
        }

        @Test
        @DisplayName("returns false when password does not match")
        void returnsFalse_whenPasswordDoesNotMatch() {
            when(roomService.findByRoomCode(ROOM_CODE)).thenReturn(roomWithPassword);

            boolean result = passwordService.checkPassword(ROOM_CODE, "wrongPassword");

            assertFalse(result);
        }

        @Test
        @DisplayName("returns true when room has no password")
        void returnsTrue_whenRoomHasNoPassword() {
            when(roomService.findByRoomCode(ROOM_CODE)).thenReturn(roomWithoutPassword);

            boolean result = passwordService.checkPassword(ROOM_CODE, null);

            assertTrue(result);
        }

        @Test
        @DisplayName("returns false when room not found")
        void returnsFalse_whenRoomNotFound() {
            when(roomService.findByRoomCode(ROOM_CODE)).thenReturn(null);

            boolean result = passwordService.checkPassword(ROOM_CODE, CORRECT_HASH);

            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("validatePassword")
    class ValidatePassword {

        @Test
        @DisplayName("succeeds when password is correct")
        void succeeds_whenPasswordIsCorrect() {
            when(roomService.findByRoomCode(ROOM_CODE)).thenReturn(roomWithPassword);

            assertDoesNotThrow(() -> passwordService.validatePassword(ROOM_CODE, CORRECT_HASH));
        }

        @Test
        @DisplayName("succeeds when room has no password")
        void succeeds_whenRoomHasNoPassword() {
            when(roomService.findByRoomCode(ROOM_CODE)).thenReturn(roomWithoutPassword);

            assertDoesNotThrow(() -> passwordService.validatePassword(ROOM_CODE, null));
        }

        @Test
        @DisplayName("throws RoomNotFound when room does not exist")
        void throws_whenRoomNotFound() {
            when(roomService.findByRoomCode(ROOM_CODE)).thenReturn(null);

            RoomPasswordException exception = assertThrows(
                    RoomPasswordException.class,
                    () -> passwordService.validatePassword(ROOM_CODE, CORRECT_HASH)
            );

            assertEquals("RoomNotFound", exception.getError());
        }

        @Test
        @DisplayName("throws RoomPasswordRequired when password not provided")
        void throws_whenPasswordNotProvided() {
            when(roomService.findByRoomCode(ROOM_CODE)).thenReturn(roomWithPassword);

            RoomPasswordException exception = assertThrows(
                    RoomPasswordException.class,
                    () -> passwordService.validatePassword(ROOM_CODE, null)
            );

            assertEquals("RoomPasswordRequired", exception.getError());
        }

        @Test
        @DisplayName("throws InvalidRoomPassword when password is incorrect")
        void throws_whenPasswordIncorrect() {
            when(roomService.findByRoomCode(ROOM_CODE)).thenReturn(roomWithPassword);

            RoomPasswordException exception = assertThrows(
                    RoomPasswordException.class,
                    () -> passwordService.validatePassword(ROOM_CODE, "wrongPassword")
            );

            assertEquals("InvalidRoomPassword", exception.getError());
        }
    }

    @Nested
    @DisplayName("hasPassword")
    class HasPassword {

        @Test
        @DisplayName("returns true when room has password")
        void returnsTrue_whenRoomHasPassword() {
            when(roomService.findByRoomCode(ROOM_CODE)).thenReturn(roomWithPassword);

            boolean result = passwordService.hasPassword(ROOM_CODE);

            assertTrue(result);
        }

        @Test
        @DisplayName("returns false when room has no password")
        void returnsFalse_whenRoomHasNoPassword() {
            when(roomService.findByRoomCode(ROOM_CODE)).thenReturn(roomWithoutPassword);

            boolean result = passwordService.hasPassword(ROOM_CODE);

            assertFalse(result);
        }

        @Test
        @DisplayName("returns false when room not found")
        void returnsFalse_whenRoomNotFound() {
            when(roomService.findByRoomCode(ROOM_CODE)).thenReturn(null);

            boolean result = passwordService.hasPassword(ROOM_CODE);

            assertFalse(result);
        }

        @Test
        @DisplayName("returns false when password is empty string")
        void returnsFalse_whenPasswordIsEmpty() {
            roomWithPassword.setHashedPassword("");
            when(roomService.findByRoomCode(ROOM_CODE)).thenReturn(roomWithPassword);

            boolean result = passwordService.hasPassword(ROOM_CODE);

            assertFalse(result);
        }
    }
}
