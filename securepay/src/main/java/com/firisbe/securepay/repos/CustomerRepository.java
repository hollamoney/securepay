package com.firisbe.securepay.repos;

import com.firisbe.securepay.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
    Customer findByUsername(String username);

    Customer findByEmail(String email);

    Customer findByCreditCard(String creditCard);



    @Query("SELECT COUNT(c) FROM Customer c WHERE c.role = 'ADMIN'")
    long countAdmins();

}
