package com.sportygroup.aviation.api.wrapper.mapper;

import com.sportygroup.aviation.api.wrapper.dto.external.AviationApiAirportDto;
import com.sportygroup.aviation.api.wrapper.dto.response.AirportResponse;
import org.springframework.stereotype.Component;

/**
 * Maps external API DTO to our API response. All fields passed through as provided by the provider.
 */
@Component
public class AirportMapper {

    public AirportResponse toResponse(AviationApiAirportDto dto) {
        if (dto == null) {
            return null;
        }
        return AirportResponse.builder()
                .siteNumber(dto.getSiteNumber())
                .type(dto.getType())
                .facilityName(dto.getFacilityName())
                .faaIdent(dto.getFaaIdent())
                .icaoIdent(dto.getIcaoIdent())
                .region(dto.getRegion())
                .districtOffice(dto.getDistrictOffice())
                .state(dto.getState())
                .stateFull(dto.getStateFull())
                .county(dto.getCounty())
                .city(dto.getCity())
                .ownership(dto.getOwnership())
                .use(dto.getUse())
                .manager(dto.getManager())
                .managerPhone(dto.getManagerPhone())
                .latitude(dto.getLatitude())
                .latitudeSec(dto.getLatitudeSec())
                .longitude(dto.getLongitude())
                .longitudeSec(dto.getLongitudeSec())
                .elevation(dto.getElevation())
                .magneticVariation(dto.getMagneticVariation())
                .tpa(dto.getTpa())
                .vfrSectional(dto.getVfrSectional())
                .boundaryArtcc(dto.getBoundaryArtcc())
                .boundaryArtccName(dto.getBoundaryArtccName())
                .responsibleArtcc(dto.getResponsibleArtcc())
                .responsibleArtccName(dto.getResponsibleArtccName())
                .fssPhoneNumber(dto.getFssPhoneNumber())
                .fssPhoneNumberTollfree(dto.getFssPhoneNumberTollfree())
                .notamFacilityIdent(dto.getNotamFacilityIdent())
                .status(dto.getStatus())
                .certificationTypeDate(dto.getCertificationTypeDate())
                .customsAirportOfEntry(dto.getCustomsAirportOfEntry())
                .militaryJointUse(dto.getMilitaryJointUse())
                .militaryLanding(dto.getMilitaryLanding())
                .lightingSchedule(dto.getLightingSchedule())
                .beaconSchedule(dto.getBeaconSchedule())
                .controlTower(dto.getControlTower())
                .unicom(dto.getUnicom())
                .ctaf(dto.getCtaf())
                .effectiveDate(dto.getEffectiveDate())
                .build();
    }
}
