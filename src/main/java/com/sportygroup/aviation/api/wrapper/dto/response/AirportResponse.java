package com.sportygroup.aviation.api.wrapper.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response contract for our API. Exposes all fields from the aviation provider.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Airport information (full set from provider)")
public class AirportResponse {

    @Schema(description = "Site number")
    private String siteNumber;

    @Schema(description = "Facility type (e.g. AIRPORT)")
    private String type;

    @Schema(description = "Facility name")
    private String facilityName;

    @Schema(description = "FAA identifier (3 letters)")
    private String faaIdent;

    @Schema(description = "ICAO identifier (4 letters)")
    private String icaoIdent;

    @Schema(description = "Region code")
    private String region;

    @Schema(description = "District office")
    private String districtOffice;

    @Schema(description = "State code")
    private String state;

    @Schema(description = "State full name")
    private String stateFull;

    @Schema(description = "County")
    private String county;

    @Schema(description = "City")
    private String city;

    @Schema(description = "Ownership code")
    private String ownership;

    @Schema(description = "Use code")
    private String use;

    @Schema(description = "Manager name")
    private String manager;

    @Schema(description = "Manager phone")
    private String managerPhone;

    @Schema(description = "Latitude (raw, e.g. DMS)")
    private String latitude;

    @Schema(description = "Latitude seconds")
    private String latitudeSec;

    @Schema(description = "Longitude (raw, e.g. DMS)")
    private String longitude;

    @Schema(description = "Longitude seconds")
    private String longitudeSec;

    @Schema(description = "Elevation in feet")
    private String elevation;

    @Schema(description = "Magnetic variation")
    private String magneticVariation;

    @Schema(description = "TPA (Traffic Pattern Altitude)")
    private String tpa;

    @Schema(description = "VFR sectional")
    private String vfrSectional;

    @Schema(description = "Boundary ARTCC code")
    private String boundaryArtcc;

    @Schema(description = "Boundary ARTCC name")
    private String boundaryArtccName;

    @Schema(description = "Responsible ARTCC code")
    private String responsibleArtcc;

    @Schema(description = "Responsible ARTCC name")
    private String responsibleArtccName;

    @Schema(description = "FSS phone number")
    private String fssPhoneNumber;

    @Schema(description = "FSS toll-free number")
    private String fssPhoneNumberTollfree;

    @Schema(description = "NOTAM facility identifier")
    private String notamFacilityIdent;

    @Schema(description = "Status")
    private String status;

    @Schema(description = "Certification type and date")
    private String certificationTypeDate;

    @Schema(description = "Customs airport of entry (Y/N)")
    private String customsAirportOfEntry;

    @Schema(description = "Military joint use (Y/N)")
    private String militaryJointUse;

    @Schema(description = "Military landing (Y/N)")
    private String militaryLanding;

    @Schema(description = "Lighting schedule")
    private String lightingSchedule;

    @Schema(description = "Beacon schedule")
    private String beaconSchedule;

    @Schema(description = "Control tower (Y/N)")
    private String controlTower;

    @Schema(description = "UNICOM frequency")
    private String unicom;

    @Schema(description = "CTAF frequency")
    private String ctaf;

    @Schema(description = "Effective date")
    private String effectiveDate;
}
