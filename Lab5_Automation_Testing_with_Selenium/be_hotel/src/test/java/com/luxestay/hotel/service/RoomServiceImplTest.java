package com.luxestay.hotel.service;

import com.luxestay.hotel.dto.PagedResponse;
import com.luxestay.hotel.dto.RoomAvailabilityRequest;
import com.luxestay.hotel.dto.RoomDetail;
import com.luxestay.hotel.dto.RoomRequest;
import com.luxestay.hotel.dto.RoomSearchCriteria;
import com.luxestay.hotel.model.Room;
import com.luxestay.hotel.model.entity.RoomEntity;
import com.luxestay.hotel.repository.BedLayoutRepository;
import com.luxestay.hotel.repository.BookingRepository;
import com.luxestay.hotel.repository.RoomImageRepository;
import com.luxestay.hotel.repository.RoomRepository;
import com.luxestay.hotel.service.impl.RoomServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomImageRepository roomImageRepository;

    @Mock
    private BedLayoutRepository bedLayoutRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private RoomServiceImpl roomService;

    private RoomEntity roomEntity;
    private RoomRequest roomRequest;

    @BeforeEach
    void setUp() {
        roomEntity = new RoomEntity();
        roomEntity.setId(1);
        roomEntity.setRoomName("Deluxe Room");
        roomEntity.setRoomNumber("101");
        roomEntity.setPricePerNight(100);
        roomEntity.setCapacity(2);
        roomEntity.setIsVisible(true);
        roomEntity.setStatus("available");

        roomRequest = new RoomRequest();
        roomRequest.setRoomName("Deluxe Room");
        roomRequest.setRoomNumber("101");
        roomRequest.setPricePerNight(100);
        roomRequest.setCapacity(2);
    }

    @Test
    void getDetail_shouldReturnRoomDetails_whenRoomExists() {
        // Arrange
        when(roomRepository.findById(1)).thenReturn(Optional.of(roomEntity));
        when(roomImageRepository.findByRoom_IdOrderByIsPrimaryDescSortOrderAsc(1)).thenReturn(new ArrayList<>());

        // Act
        RoomDetail roomDetail = roomService.getDetail(1L);

        // Assert
        assertNotNull(roomDetail);
        assertEquals("Deluxe Room", roomDetail.getRoom().getName());
        verify(roomRepository, times(1)).findById(1);
    }

    @Test
    void createRoom_shouldCreateRoomSuccessfully() {
        // Arrange
        when(roomRepository.existsByRoomNumber("101")).thenReturn(false);
        when(roomRepository.save(any(RoomEntity.class))).thenReturn(roomEntity);

        // Act
        Room createdRoom = roomService.createRoom(roomRequest);

        // Assert
        assertNotNull(createdRoom);
        assertEquals("Deluxe Room", createdRoom.getName());
        verify(roomRepository, times(1)).save(any(RoomEntity.class));
    }

    @Test
    void createRoom_shouldThrowException_whenRoomNumberExists() {
        // Arrange
        when(roomRepository.existsByRoomNumber("101")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roomService.createRoom(roomRequest);
        });
        assertEquals("Room number already exists", exception.getMessage());
        verify(roomRepository, never()).save(any(RoomEntity.class));
    }

    @Test
    void updateRoom_shouldUpdateRoomSuccessfully() {
        // Arrange
        when(roomRepository.findById(1)).thenReturn(Optional.of(roomEntity));
        when(roomRepository.save(any(RoomEntity.class))).thenReturn(roomEntity);
        RoomRequest updateRequest = new RoomRequest();
        updateRequest.setRoomName("Updated Deluxe Room");

        // Act
        Room updatedRoom = roomService.updateRoom(1L, updateRequest);

        // Assert
        assertNotNull(updatedRoom);
        assertEquals("Updated Deluxe Room", updatedRoom.getName());
        verify(roomRepository, times(1)).save(any(RoomEntity.class));
    }

    @Test
    void deleteRoom_shouldSoftDeleteRoom() {
        // Arrange
        when(roomRepository.findById(1)).thenReturn(Optional.of(roomEntity));

        // Act
        roomService.deleteRoom(1L);

        // Assert
        verify(roomRepository, times(1)).save(roomEntity);
        assertFalse(roomEntity.getIsVisible());
    }

    @Test
    void checkAvailability_shouldReturnAvailableRooms() {
        // Arrange
        RoomAvailabilityRequest request = new RoomAvailabilityRequest();
        request.setCheckIn(LocalDate.now().plusDays(1));
        request.setCheckOut(LocalDate.now().plusDays(3));
        request.setGuests(2);

        Page<RoomEntity> page = new PageImpl<>(List.of(roomEntity));
        when(roomRepository.findAvailableRooms(any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(page);

        // Act
        PagedResponse<Room> response = roomService.checkAvailability(request);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        assertEquals("Deluxe Room", response.getItems().get(0).getName());
    }

    @Test
    void search_shouldReturnFilteredRooms() {
        // Arrange
        RoomSearchCriteria criteria = new RoomSearchCriteria();
        criteria.setGuests(2);

        Page<RoomEntity> page = new PageImpl<>(List.of(roomEntity));
        when(roomRepository.findForList(any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(page);

        // Act
        PagedResponse<Room> response = roomService.search(criteria);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        assertEquals("Deluxe Room", response.getItems().get(0).getName());
    }

    @Test
    void updateRoomStatus_shouldUpdateStatus_whenTransitionIsValid() {
        // Arrange
        roomEntity.setStatus("available");
        when(roomRepository.findById(1)).thenReturn(Optional.of(roomEntity));

        // Act
        roomService.updateRoomStatus(1L, "maintenance", "Cleaning");

        // Assert
        verify(roomRepository, times(1)).save(roomEntity);
        assertEquals("maintenance", roomEntity.getStatus());
    }

    @Test
    void updateRoomStatus_shouldThrowException_whenTransitionIsInvalid() {
        // Arrange
        roomEntity.setStatus("occupied");
        when(roomRepository.findById(1)).thenReturn(Optional.of(roomEntity));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roomService.updateRoomStatus(1L, "maintenance", "Cleaning");
        });
        assertFalse(exception.getMessage().contains("Can not move 'occupied' to 'maintenance'"));
        verify(roomRepository, never()).save(any(RoomEntity.class));
    }
}
