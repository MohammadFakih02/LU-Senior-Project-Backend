package com.example.internetprovidermanagement.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.internetprovidermanagement.dtos.PaymentDTO;
import com.example.internetprovidermanagement.models.Payment;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "userBundleId", source = "userBundle.id")
    PaymentDTO toDto(Payment payment);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "userBundle", ignore = true)
    Payment toEntity(PaymentDTO paymentDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "userBundle", ignore = true)
    void updatePaymentFromDto(PaymentDTO paymentDTO, @MappingTarget Payment payment);
}