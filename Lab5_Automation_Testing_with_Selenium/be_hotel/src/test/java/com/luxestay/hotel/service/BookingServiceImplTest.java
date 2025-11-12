package com.luxestay.hotel.service;

import com.luxestay.hotel.dto.PagedResponse;
import com.luxestay.hotel.dto.booking.BookingRequest;
import com.luxestay.hotel.dto.booking.BookingResponse;
import com.luxestay.hotel.dto.booking.BookingSummary;
import com.luxestay.hotel.model.Account;
import com.luxestay.hotel.model.entity.BookingCustomerDetails;
import com.luxestay.hotel.model.entity.BookingEntity;
import com.luxestay.hotel.model.entity.RoomEntity;
import com.luxestay.hotel.repository.AccountRepository;
import com.luxestay.hotel.repository.BookingCustomerDetailsRepository;
import com.luxestay.hotel.repository.BookingRepository;
import com.luxestay.hotel.repository.PaymentRepository;
import com.luxestay.hotel.repository.RoomRepository;
import com.luxestay.hotel.service.impl.BookingServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private BookingCustomerDetailsRepository bookingCustomerDetailsRepository;

    private RoomEntity room;
    private Account account;
    private BookingRequest bookingRequest;
    private BookingEntity bookingEntity;

    @BeforeEach
    public void setUp() {
        room = new RoomEntity();
        room.setId(1);
        room.setPricePerNight(100);
        room.setRoomName("Deluxe Room");

        account = new Account();
        account.setId(1);

        bookingRequest = new BookingRequest();
        bookingRequest.setRoomId(1L);
        bookingRequest.setCheckIn("2025-12-20");
        bookingRequest.setCheckOut("2025-12-22");
        bookingRequest.setFullName("John Doe");
        bookingRequest.setGender("Male");
        bookingRequest.setPhoneNumber("123456789");
        bookingRequest.setNationalIdNumber("987654321");
        bookingRequest.setDateOfBirth("1990-01-01");

        bookingEntity = new BookingEntity();
        bookingEntity.setId(1);
        bookingEntity.setAccount(account);
        bookingEntity.setRoom(room);
        bookingEntity.setCheckIn(LocalDate.parse("2025-12-20"));
        bookingEntity.setCheckOut(LocalDate.parse("2025-12-22"));
        bookingEntity.setTotalPrice(BigDecimal.valueOf(200));
        bookingEntity.setDepositAmount(BigDecimal.valueOf(60));
        bookingEntity.setStatus("pending");
        bookingEntity.setPaymentState("unpaid");
    }

    // ===== Create Booking =====
    @Test
    public void testCreateBooking_Success() {
        when(roomRepository.findById(1)).thenReturn(Optional.of(room));
        when(accountRepository.findById(1)).thenReturn(Optional.of(account));
        when(bookingRepository.hasActiveConflict(anyInt(), any(LocalDate.class), any(LocalDate.class))).thenReturn(false);
        when(bookingRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        BookingResponse response = bookingService.create(1, bookingRequest);

        assertEquals("pending", response.getStatus());
        assertEquals(200, response.getTotalVnd());
        assertEquals(60, response.getDepositVnd());
        verify(bookingCustomerDetailsRepository).save(any());
    }

    @Test
    public void testCreateBooking_RoomNotFound() {
        when(roomRepository.findById(1)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> bookingService.create(1, bookingRequest));
        assertEquals("Không tìm thấy phòng", ex.getMessage());
    }

    // ===== Payment Captured =====
    @Test
    public void testOnPaymentCaptured_PaidInFull() {
        when(bookingRepository.findById(1)).thenReturn(Optional.of(bookingEntity));
        when(paymentRepository.sumPaidByBooking(1)).thenReturn(BigDecimal.valueOf(200));

        bookingService.onPaymentCaptured(1);

        assertEquals("paid_in_full", bookingEntity.getPaymentState());
        assertEquals("confirmed", bookingEntity.getStatus());
    }

    @Test
    public void testOnPaymentCaptured_Unpaid() {
        when(bookingRepository.findById(1)).thenReturn(Optional.of(bookingEntity));
        when(paymentRepository.sumPaidByBooking(1)).thenReturn(BigDecimal.valueOf(50));

        bookingService.onPaymentCaptured(1);

        assertEquals("unpaid", bookingEntity.getPaymentState());
        assertEquals("pending", bookingEntity.getStatus());
    }

    // ===== Request Cancel =====
    @Test
    public void testRequestCancel_Success() {
        bookingEntity.setStatus("confirmed");
        when(bookingRepository.findByIdAndAccount_Id(1, 1)).thenReturn(Optional.of(bookingEntity));

        bookingService.requestCancel(1, 1, "Test reason");

        assertEquals("cancel_requested", bookingEntity.getStatus());
    }

    // ===== Decide Cancel =====
    @Test
    public void testDecideCancel_Approve() {
        bookingEntity.setStatus("cancel_requested");
        bookingEntity.setCancelReason("Customer reason");
        when(bookingRepository.findById(1)).thenReturn(Optional.of(bookingEntity));

        bookingService.decideCancel(1, 99, true, "Approved by staff");

        assertEquals("cancelled", bookingEntity.getStatus());
        assertTrue(bookingEntity.getCancelReason().contains("Staff note: Approved by staff"));
    }

    // ===== History =====
    @Test
    public void testHistory_Success() {
        Page<BookingEntity> page = new PageImpl<>(List.of(bookingEntity));
        when(bookingRepository.findForHistory(eq(1), eq("pending"), any(Pageable.class))).thenReturn(page);

        PagedResponse<BookingSummary> response = bookingService.history(1, "pending", 0, 10);

        assertEquals(1, response.getTotal());
    }

    // ===== Confirm Booking Payment =====
    @Test
    public void testConfirmBookingPayment_Success() {
        when(bookingRepository.findById(1)).thenReturn(Optional.of(bookingEntity));

        bookingService.confirmBookingPayment(1);

        assertEquals("confirmed", bookingEntity.getStatus());
    }
}