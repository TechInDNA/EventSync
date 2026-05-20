package com.techindna.eventsync.mapper;

import com.techindna.eventsync.dto.ExternalLinkDto;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ExternalLinkMapper {

    private ExternalLinkMapper() {}

    public static void bindInsertExternalLinkParams(PreparedStatement ps, ExternalLinkDto link, UUID userId) throws SQLException {
        ps.setString(1, link.getName());
        ps.setString(2, link.getUrl());
        ps.setObject(3, userId);
    }

    public static ExternalLinkDto mapExternalLink(ResultSet rs) throws SQLException {
        ExternalLinkDto link = new ExternalLinkDto();
        link.setName(rs.getString("link_name"));
        link.setUrl(rs.getString("link_url"));
        return link;
    }
}
