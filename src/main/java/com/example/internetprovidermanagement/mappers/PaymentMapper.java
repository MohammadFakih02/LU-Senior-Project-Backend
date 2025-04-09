package com.example.internetprovidermanagement.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.internetprovidermanagement.dtos.PaymentDTO;
import com.example.internetprovidermanagement.models.Payment;
import com.example.internetprovidermanagement.models.User;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "userId", source = "user.id")
    PaymentDTO toPaymentDTO(Payment payment);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Payment toPayment(PaymentDTO paymentDTO);

    @Mapping(target = "id", source = "paymentDTO.id")
    @Mapping(target = "status", source = "paymentDTO.status") // Explicit status mapping
    @Mapping(target = "user", source = "user")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Payment toPaymentWithUser(PaymentDTO paymentDTO, User user);
}