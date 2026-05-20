package com.techindna.eventsync.mapper;

import com.techindna.eventsync.dto.ExternalLinkDto;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class ExternalLinkMapper {

    private ExternalLinkMapper() {}

    public static void bindInsertExternalLinkParams(PreparedStatement ps, ExternalLinkDto link, UUID userId) throws SQLException {
        ps.setString(1, link.getName());
        ps.setString(2, link.getUrl());
        ps.setObject(3, userId);
    }
}
