package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.UCSBOrganisation;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBOrganisationRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "UCSBOrganisation")
@RequestMapping("/api/UCSBOrganization")
@RestController
@Slf4j
public class UCSBOrganisationController extends ApiController {

    @Autowired
    UCSBOrganisationRepository UCSBOrganisationRepository;

    @Operation(summary= "List all ucsb organisations")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBOrganisation> allOrganisations() {
        Iterable<UCSBOrganisation> commons = UCSBOrganisationRepository.findAll();
        return commons;
    }

    @Operation(summary= "Create a new organisation")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBOrganisation postorganisation(
        @Parameter(name="orgCode")                          @RequestParam String orgCode,
        @Parameter(name="orgTranslationShort")              @RequestParam String orgTranslationShort,
        @Parameter(name="orgTranslation")                   @RequestParam String orgTranslation,
        @Parameter(name="inactive")                         @RequestParam boolean inactive
        // @Parameter(name="hasDiningCam")                  @RequestParam boolean hasDiningCam,
        // @Parameter(name="latitude")                      @RequestParam double latitude,
        // @Parameter(name="longitude")                     @RequestParam double longitude
        )
        {

        UCSBOrganisation organisation = new UCSBOrganisation();
        organisation.setOrgCode(orgCode);
        organisation.setOrgTranslationShort(orgTranslationShort);
        organisation.setOrgTranslation(orgTranslation);
        organisation.setInactive(inactive);
        // commons.setHasDiningCam(hasDiningCam);
        // commons.setLatitude(latitude);
        // commons.setLongitude(longitude);

        UCSBOrganisation savedOrganisation = UCSBOrganisationRepository.save(organisation);

        return savedOrganisation;
    }

    @Operation(summary= "Get a single organisation")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public UCSBOrganisation getById(
            @Parameter(name="orgCode") @RequestParam String orgCode) {
                UCSBOrganisation organisations = UCSBOrganisationRepository.findById(orgCode)
                .orElseThrow(() -> new EntityNotFoundException(UCSBOrganisation.class, orgCode));

        return organisations;
    }

    @Operation(summary= "Delete a UCSBOrganisation")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteorganisations(
            @Parameter(name="orgCode") @RequestParam String orgCode) {
                UCSBOrganisation organisations = UCSBOrganisationRepository.findById(orgCode)
                .orElseThrow(() -> new EntityNotFoundException(UCSBOrganisation.class, orgCode));

        UCSBOrganisationRepository.delete(organisations);
        return genericMessage("UCSBOrganisation with id %s deleted".formatted(orgCode));
    }

    @Operation(summary= "Update a single organisation")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public UCSBOrganisation updateorganisation(
            @Parameter(name="orgCode") @RequestParam String orgCode,
            @RequestBody @Valid UCSBOrganisation incoming) {

            UCSBOrganisation organisations = UCSBOrganisationRepository.findById(orgCode)
                .orElseThrow(() -> new EntityNotFoundException(UCSBOrganisation.class, orgCode));


                organisations.setOrgCode(incoming.getOrgCode());  
                // organisations.setOrgTranslationShort(incoming.getOrgTranslationShort());
                // organisations.setOrgTranslation(incoming.getOrgTranslation());
                // organisations.setInactive(incoming.getInactive());
                // organisations.setLatitude(incoming.getLatitude());
                // organisations.setLongitude(incoming.getLongitude());

                UCSBOrganisationRepository.save(organisations);

        return organisations;
    }
}