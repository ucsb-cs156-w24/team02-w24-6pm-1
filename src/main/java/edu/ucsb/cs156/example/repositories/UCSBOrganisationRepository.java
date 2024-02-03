package edu.ucsb.cs156.example.repositories;

import edu.ucsb.cs156.example.entities.UCSBOrganisation;

// import org.springframework.beans.propertyeditors.StringArrayPropertyEditor; (Commented because its never used, reference for future)
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UCSBOrganisationRepository extends CrudRepository<UCSBOrganisation, String> {
 
}