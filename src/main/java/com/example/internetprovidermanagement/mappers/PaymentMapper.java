package com.example.internetprovidermanagement.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.example.internetprovidermanagement.dtos.CreatePaymentDTO;
import com.example.internetprovidermanagement.dtos.PaymentResponseDTO;
import com.example.internetprovidermanagement.dtos.UpdatePaymentDTO;
import com.example.internetprovidermanagement.models.Payment;

@Mapper(componentModel = "spring", uses = {UserBundleMapper.class})
public interface PaymentMapper {

    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    @Mapping(target = "paymentId", source = "id")
    @Mapping(target = "userId", source = "userBundle.user.id")
    @Mapping(target = "userName", expression = "java(payment.getUserBundle().getUser().getFirstName() + \" \" + payment.getUserBundle().getUser().getLastName())")
    @Mapping(target = "bundleId", source = "userBundle.bundle.bundleId")
    @Mapping(target = "bundleName", source = "userBundle.bundle.name")
    PaymentResponseDTO toPaymentResponseDTO(Payment payment);

    @Mapping(target = "userBundle", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    Payment toPayment(CreatePaymentDTO createPaymentDTO);

    @Mapping(target = "userBundle", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updatePaymentFromDto(UpdatePaymentDTO updatePaymentDTO, @MappingTarget Payment payment);
}