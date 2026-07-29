package lk.ijse.parking_service.service.impl;

import lk.ijse.parking_service.dto.req.ParkingSaveRequest;
import lk.ijse.parking_service.dto.req.ParkingUpdateRequest;
import lk.ijse.parking_service.dto.resp.ParkingResponse;
import lk.ijse.parking_service.entity.Parking;
import lk.ijse.parking_service.entity.ParkingStatus;
import lk.ijse.parking_service.exception.ParkingNotFoundException;
import lk.ijse.parking_service.repository.ParkingRepository;
import lk.ijse.parking_service.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingServiceImpl implements ParkingService {

    private final ParkingRepository parkingRepository;

    @Override
    public ParkingResponse saveParking(ParkingSaveRequest request) {

        validateSaveRequest(request);

        Parking parking = Parking.builder()
                .parkingName(request.getParkingName())
                .location(request.getLocation())
                .address(request.getAddress())
                .totalSlots(request.getTotalSlots())
                .availableSlots(request.getTotalSlots())
                .hourlyRate(request.getHourlyRate())
                .status(ParkingStatus.OPEN)
                .build();

        Parking savedParking = parkingRepository.save(parking);

        return mapToResponse(savedParking);
    }

    @Override
    public ParkingResponse updateParking(Long id, ParkingUpdateRequest request) {

        validateUpdateRequest(request);

        Parking parking = parkingRepository.findById(id)
                .orElseThrow(() -> new ParkingNotFoundException("Parking not found"));

        parking.setParkingName(request.getParkingName());
        parking.setLocation(request.getLocation());
        parking.setAddress(request.getAddress());
        parking.setTotalSlots(request.getTotalSlots());

        if (parking.getAvailableSlots() > request.getTotalSlots()) {
            parking.setAvailableSlots(request.getTotalSlots());
        }

        parking.setHourlyRate(request.getHourlyRate());
        parking.setStatus(request.getStatus());

        Parking updatedParking = parkingRepository.save(parking);

        return mapToResponse(updatedParking);
    }

    @Override
    public ParkingResponse getParking(Long id) {

        Parking parking = parkingRepository.findById(id)
                .orElseThrow(() -> new ParkingNotFoundException("Parking not found"));

        return mapToResponse(parking);
    }

    @Override
    public List<ParkingResponse> getAllParking() {

        return parkingRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteParking(Long id) {

        Parking parking = parkingRepository.findById(id)
                .orElseThrow(() -> new ParkingNotFoundException("Parking not found"));

        parkingRepository.delete(parking);
    }

    @Override
    public List<ParkingResponse> filterByLocation(String location) {

        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Location is required");
        }

        return parkingRepository.findByLocationContainingIgnoreCase(location)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private void validateSaveRequest(ParkingSaveRequest request) {

        if (request.getParkingName() == null || request.getParkingName().trim().isEmpty()) {
            throw new IllegalArgumentException("Parking name is required");
        }

        if (request.getLocation() == null || request.getLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("Location is required");
        }

        if (request.getAddress() == null || request.getAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Address is required");
        }

        if (request.getTotalSlots() == null || request.getTotalSlots() <= 0) {
            throw new IllegalArgumentException("Total slots must be greater than zero");
        }

        if (request.getHourlyRate() == null || request.getHourlyRate() < 0) {
            throw new IllegalArgumentException("Hourly rate cannot be negative");
        }
    }

    private void validateUpdateRequest(ParkingUpdateRequest request) {

        validateSaveRequest(
                ParkingSaveRequest.builder()
                        .parkingName(request.getParkingName())
                        .location(request.getLocation())
                        .address(request.getAddress())
                        .totalSlots(request.getTotalSlots())
                        .hourlyRate(request.getHourlyRate())
                        .build()
        );

        if (request.getStatus() == null) {
            throw new IllegalArgumentException("Parking status is required");
        }
    }

    private ParkingResponse mapToResponse(Parking parking) {

        return ParkingResponse.builder()
                .id(parking.getId())
                .parkingName(parking.getParkingName())
                .location(parking.getLocation())
                .address(parking.getAddress())
                .totalSlots(parking.getTotalSlots())
                .availableSlots(parking.getAvailableSlots())
                .hourlyRate(parking.getHourlyRate())
                .status(parking.getStatus())
                .createdAt(parking.getCreatedAt())
                .updatedAt(parking.getUpdatedAt())
                .build();
    }
}