package lk.ijse.parking_service.service.impl;

import lk.ijse.parking_service.dto.req.ParkingSaveRequest;
import lk.ijse.parking_service.dto.req.ParkingUpdateRequest;
import lk.ijse.parking_service.dto.resp.ParkingResponse;
import lk.ijse.parking_service.entity.Parking;
import lk.ijse.parking_service.entity.ParkingStatus;
import lk.ijse.parking_service.exception.BadRequestException;
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
                .city(request.getCity())
                .zone(request.getZone())
                .location(request.getLocation())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .totalSlots(request.getTotalSlots())
                .availableSlots(request.getTotalSlots())
                .hourlyRate(request.getHourlyRate())
                .ownerId(request.getOwnerId())
                .description(request.getDescription())
                .contactNumber(request.getContactNumber())
                .openingTime(request.getOpeningTime())
                .closingTime(request.getClosingTime())
                .status(ParkingStatus.AVAILABLE)
                .build();

        return mapToResponse(parkingRepository.save(parking));
    }

    @Override
    public ParkingResponse updateParking(Long id, ParkingUpdateRequest request) {

        validateUpdateRequest(request);

        Parking parking = parkingRepository.findById(id)
                .orElseThrow(() ->
                        new ParkingNotFoundException("Parking not found"));

        parking.setParkingName(request.getParkingName());
        parking.setCity(request.getCity());
        parking.setZone(request.getZone());
        parking.setLocation(request.getLocation());
        parking.setAddress(request.getAddress());
        parking.setLatitude(request.getLatitude());
        parking.setLongitude(request.getLongitude());

        parking.setTotalSlots(request.getTotalSlots());

        if (parking.getAvailableSlots() > request.getTotalSlots()) {
            parking.setAvailableSlots(request.getTotalSlots());
        }

        parking.setHourlyRate(request.getHourlyRate());
        parking.setOwnerId(request.getOwnerId());
        parking.setDescription(request.getDescription());
        parking.setContactNumber(request.getContactNumber());
        parking.setOpeningTime(request.getOpeningTime());
        parking.setClosingTime(request.getClosingTime());
        parking.setStatus(request.getStatus());

        return mapToResponse(parkingRepository.save(parking));
    }

    @Override
    public ParkingResponse getParking(Long id) {

        Parking parking = parkingRepository.findById(id)
                .orElseThrow(() ->
                        new ParkingNotFoundException("Parking not found"));

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
                .orElseThrow(() ->
                        new ParkingNotFoundException("Parking not found"));

        parkingRepository.delete(parking);
    }

    @Override
    public List<ParkingResponse> filterByLocation(String location) {

        if (location == null || location.trim().isEmpty()) {
            throw new BadRequestException("Location is required");
        }

        return parkingRepository.findByLocationContainingIgnoreCase(location.trim())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ParkingResponse reserveParking(Long id) {

        Parking parking = parkingRepository.findById(id)
                .orElseThrow(() ->
                        new ParkingNotFoundException("Parking not found"));

        if (parking.getAvailableSlots() <= 0) {
            throw new BadRequestException("No parking spaces available");
        }

        parking.setAvailableSlots(parking.getAvailableSlots() - 1);

        if (parking.getAvailableSlots() == 0) {
            parking.setStatus(ParkingStatus.FULL);
        } else {
            parking.setStatus(ParkingStatus.RESERVED);
        }

        return mapToResponse(parkingRepository.save(parking));
    }

    @Override
    public ParkingResponse releaseParking(Long id) {

        Parking parking = parkingRepository.findById(id)
                .orElseThrow(() ->
                        new ParkingNotFoundException("Parking not found"));

        if (parking.getAvailableSlots() >= parking.getTotalSlots()) {
            throw new BadRequestException("All parking spaces are already available");
        }

        parking.setAvailableSlots(parking.getAvailableSlots() + 1);
        parking.setStatus(ParkingStatus.AVAILABLE);

        return mapToResponse(parkingRepository.save(parking));
    }

    private void validateSaveRequest(ParkingSaveRequest request) {

        if (request.getParkingName() == null || request.getParkingName().trim().isEmpty()) {
            throw new BadRequestException("Parking name is required");
        }

        if (request.getCity() == null || request.getCity().trim().isEmpty()) {
            throw new BadRequestException("City is required");
        }

        if (request.getZone() == null || request.getZone().trim().isEmpty()) {
            throw new BadRequestException("Zone is required");
        }

        if (request.getLocation() == null || request.getLocation().trim().isEmpty()) {
            throw new BadRequestException("Location is required");
        }

        if (request.getAddress() == null || request.getAddress().trim().isEmpty()) {
            throw new BadRequestException("Address is required");
        }

        if (request.getLatitude() == null) {
            throw new BadRequestException("Latitude is required");
        }

        if (request.getLongitude() == null) {
            throw new BadRequestException("Longitude is required");
        }

        if (request.getOwnerId() == null) {
            throw new BadRequestException("Owner ID is required");
        }

        if (request.getTotalSlots() == null || request.getTotalSlots() <= 0) {
            throw new BadRequestException("Total slots must be greater than zero");
        }

        if (request.getHourlyRate() == null || request.getHourlyRate() <= 0) {
            throw new BadRequestException("Hourly rate must be greater than zero");
        }

        if (request.getOpeningTime() == null) {
            throw new BadRequestException("Opening time is required");
        }

        if (request.getClosingTime() == null) {
            throw new BadRequestException("Closing time is required");
        }
    }

    private void validateUpdateRequest(ParkingUpdateRequest request) {

        validateSaveRequest(
                ParkingSaveRequest.builder()
                        .parkingName(request.getParkingName())
                        .city(request.getCity())
                        .zone(request.getZone())
                        .location(request.getLocation())
                        .address(request.getAddress())
                        .latitude(request.getLatitude())
                        .longitude(request.getLongitude())
                        .totalSlots(request.getTotalSlots())
                        .hourlyRate(request.getHourlyRate())
                        .ownerId(request.getOwnerId())
                        .description(request.getDescription())
                        .contactNumber(request.getContactNumber())
                        .openingTime(request.getOpeningTime())
                        .closingTime(request.getClosingTime())
                        .build()
        );

        if (request.getStatus() == null) {
            throw new BadRequestException("Parking status is required");
        }
    }

    private ParkingResponse mapToResponse(Parking parking) {

        return ParkingResponse.builder()
                .id(parking.getId())
                .parkingName(parking.getParkingName())
                .city(parking.getCity())
                .zone(parking.getZone())
                .location(parking.getLocation())
                .address(parking.getAddress())
                .latitude(parking.getLatitude())
                .longitude(parking.getLongitude())
                .totalSlots(parking.getTotalSlots())
                .availableSlots(parking.getAvailableSlots())
                .hourlyRate(parking.getHourlyRate())
                .ownerId(parking.getOwnerId())
                .description(parking.getDescription())
                .contactNumber(parking.getContactNumber())
                .openingTime(parking.getOpeningTime())
                .closingTime(parking.getClosingTime())
                .status(parking.getStatus())
                .createdAt(parking.getCreatedAt())
                .updatedAt(parking.getUpdatedAt())
                .build();
    }
}