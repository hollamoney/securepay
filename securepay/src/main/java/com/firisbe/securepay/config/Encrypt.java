package com.firisbe.securepay.config;

import com.firisbe.securepay.services.EncryptionService;
import org.springframework.context.annotation.Configuration;

import javax.persistence.AttributeConverter;

@Configuration
public class Encrypt implements AttributeConverter<String, String> {

    private final EncryptionService encryptionUtil;

    public Encrypt(EncryptionService encryptionUtil) {
        this.encryptionUtil = encryptionUtil;
    }

    @Override
    public String convertToDatabaseColumn(String s) {
        return encryptionUtil.encrypt(s);
    }

    @Override
    public String convertToEntityAttribute(String s) {
        return encryptionUtil.decrypt(s);
    }
}