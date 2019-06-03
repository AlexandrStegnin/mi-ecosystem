package com.stegnin.miecosystem.service;

import com.stegnin.miecosystem.model.DeviceInfo;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

/**
 * @author Alexandr Stegnin
 */

@Slf4j
@Service
public class ExtractDeviceInfoService {

    private Connection connect(String path) {
        // SQLite connection string
        String url = "jdbc:sqlite:" + path;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return conn;
    }

    public DeviceInfo extractDeviceInfo(MemoryBuffer buffer) {
        File file;
        try (InputStream input = buffer.getInputStream()) {
            file = File.createTempFile("tmp-", ".sqlite", null);
            Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            file.deleteOnExit();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Произошла ошибка " + e.getMessage());
        }
        return extractDeviceInfo(file.getAbsolutePath());
    }

    public DeviceInfo extractDeviceInfo(String pathToDb) {
        String sql = "SELECT * FROM ZDEVICE WHERE ZTOKEN IS NOT '';";
        DeviceInfo deviceInfo = new DeviceInfo();
        try (Connection conn = connect(pathToDb);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                deviceInfo.setIp(rs.getString("ZLOCALIP"));
                deviceInfo.setMac(rs.getString("ZMAC"));
                deviceInfo.setModel(rs.getString("ZMODEL"));
                deviceInfo.setName(rs.getString("ZNAME"));
                deviceInfo.setToken(
                        hexadecimalToString(
                                encodedToHexadecimal(rs.getString("ZTOKEN"))
                        )
                );
            }
            return deviceInfo;
        } catch (SQLException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            log.error(e.getMessage());
        }
        return deviceInfo;
    }

    private String encodedToHexadecimal(String encrypted) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        String KEY_STRING = "00000000000000000000000000000000";
        SecretKey sKey = new SecretKeySpec(DatatypeConverter
                .parseHexBinary(KEY_STRING), "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, sKey);

        byte[] result = cipher.doFinal(DatatypeConverter
                .parseHexBinary(encrypted));

        return DatatypeConverter.printHexBinary(result);
    }

    private String hexadecimalToString(String hexadecimalString) {
        byte[] bytes = DatatypeConverter.parseHexBinary(hexadecimalString);
        return new String(bytes, StandardCharsets.UTF_8).trim();
    }

}
